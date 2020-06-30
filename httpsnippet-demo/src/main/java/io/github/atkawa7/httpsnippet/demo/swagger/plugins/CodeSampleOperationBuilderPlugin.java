package io.github.atkawa7.httpsnippet.demo.swagger.plugins;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import io.atkawa7.har.HarHeader;
import io.atkawa7.har.HarPostData;
import io.atkawa7.har.HarRequest;
import io.github.atkawa7.httpsnippet.demo.config.DemoProperties;
import io.github.atkawa7.httpsnippet.demo.dto.SpeakerDTO;
import io.github.atkawa7.httpsnippet.demo.swagger.extensions.CodeSampleVendorExtension;
import io.github.atkawa7.httpsnippet.demo.swagger.models.CodeSample;
import io.github.atkawa7.httpsnippet.generators.HttpSnippetCodeGenerator;
import io.github.atkawa7.httpsnippet.http.HttpVersion;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.utils.HarUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.wrapper.NameValueExpression;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 100)
public class CodeSampleOperationBuilderPlugin implements OperationBuilderPlugin {
  private final DemoProperties properties;

  public CodeSampleOperationBuilderPlugin(DemoProperties properties) {
    Objects.requireNonNull(properties, "Properties cannot be null");
    this.properties = properties;
  }

  @Override
  public void apply(OperationContext operationContext) {
    String X_CODE_SAMPLES = "x-code-samples";

    List<ResolvedMethodParameter> resolvedParameters = operationContext.getParameters();

    Object example = getExample(resolvedParameters);

    List<HarHeader> headers = getHarHeaders(operationContext);

    String body;
    try {
      body = Objects.isNull(example) ? "" : HarUtils.toJsonString(example);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to create json from example");
    }

    HarPostData postData =
        Objects.isNull(example)
            ? null
            : new HarPostData()
                .withMimeType(MediaType.APPLICATION_JSON)
                .withText(body);

    HarRequest harRequest =
        new HarRequest()
            .withMethod(operationContext.httpMethod().name())
            .withUrl(
                String.format(
                    "%s%s",
                    properties.getApplicationBaseUrl(), operationContext.requestMappingPattern()))
            .withHeaders(headers)
            .withHttpVersion(HttpVersion.HTTP_1_1.toString())
            .withPostData(postData)
            ;

    try {
      final List<CodeSample> codeSamples =
          new HttpSnippetCodeGenerator()
              .snippets(harRequest).stream()
                  .map(
                      c ->
                          CodeSample.builder()
                              .label(c.getDisplayName().toLowerCase())
                              .lang(c.getLanguage().getTitle())
                              .source(c.getCode())
                              .build())
                  .collect(Collectors.toList());
      if (codeSamples.size() > 0) {
        final CodeSampleVendorExtension vendorExtension =
            new CodeSampleVendorExtension(X_CODE_SAMPLES, codeSamples);
        operationContext.operationBuilder().extensions(Collections.singletonList(vendorExtension));
      }
    } catch (Exception e) {
      logger.error("Failed to create code samples", e);
      throw new RuntimeException("Failed to create sample codes");
    }
  }

  private Object getExample(List<ResolvedMethodParameter> resolvedParameters) {
    Object example = null;

    String LINKEDIN = "https://www.linkedin.com/in/%s";
    String FACEBOOK = "https://www.facebook.com/%s";
    String GITHUB = "https://github.com/%s";
    String TWITTER = "https://twitter.com/%s";
    Faker FAKER = new Faker();

    if (Objects.nonNull(resolvedParameters)) {
      for (ResolvedMethodParameter resolvedMethodParameter : resolvedParameters) {
        Optional<String> optional = resolvedMethodParameter.defaultName();

        if (optional.isPresent()) {
          String name = optional.get();
          logger.info("Current parameter is {}", name);
        }

        ResolvedType resolvedType = resolvedMethodParameter.getParameterType();
        final Class<?> erasedType = resolvedType.getErasedType();
        logger.info("Current class name", erasedType.getSimpleName());

        Optional<RequestBody> requestBody =
            resolvedMethodParameter.findAnnotation(RequestBody.class);
        if (requestBody.isPresent()) {

          if (erasedType.isAssignableFrom(SpeakerDTO.class)) {
            SpeakerDTO speakerDTO =
                SpeakerDTO.builder()
                    .firstName(FAKER.name().firstName())
                    .lastName(FAKER.name().lastName())
                    .company(FAKER.company().name())
                    .biography(FAKER.lorem().paragraph())
                    .build();
            String username =
                StringUtils.lowerCase(
                    String.format("%s.%s", speakerDTO.getFirstName(), speakerDTO.getLastName()));

            speakerDTO.setLinkedIn(String.format(LINKEDIN, username));
            speakerDTO.setFacebook(String.format(FACEBOOK, username));
            speakerDTO.setGithub(String.format(GITHUB, username));
            speakerDTO.setTwitter(String.format(TWITTER, username));
            example = speakerDTO;
          }
        }
      }
    }
    return example;
  }

  private List<HarHeader> getHarHeaders(OperationContext operationContext) {
    List<HarHeader> headers = new ArrayList<>();

    Set<NameValueExpression<String>> nameValueExpressions = operationContext.headers();
    if (ObjectUtils.isNotEmpty(nameValueExpressions)) {
      for (NameValueExpression<String> nameValueExpression : nameValueExpressions) {
        HarHeader harHeader =
            new HarHeader()
                .withName(nameValueExpression.getName())
                .withValue(nameValueExpression.getValue());
        headers.add(harHeader);
      }
    }

    DocumentationContext documentationContext = operationContext.getDocumentationContext();
    if (Objects.nonNull(documentationContext)) {
      Set<String> consumes = documentationContext.getConsumes();
      if (ObjectUtils.isNotEmpty(consumes)) {
        HarHeader harHeader =
            new HarHeader()
                .withName(HttpHeaders.CONTENT_TYPE)
                .withValue(StringUtils.join(consumes, ","));
        headers.add(harHeader);
      }

      Set<String> produces = documentationContext.getProduces();

      if (ObjectUtils.isNotEmpty(consumes)) {
        HarHeader harHeader =
            new HarHeader()
                .withName(HttpHeaders.ACCEPT)
                .withValue(StringUtils.join(produces, ","));
        headers.add(harHeader);
      }
    }

    return headers;
  }

  @Override
  public boolean supports(DocumentationType documentationType) {
    return DocumentationType.SWAGGER_2.equals(documentationType);
  }
}
