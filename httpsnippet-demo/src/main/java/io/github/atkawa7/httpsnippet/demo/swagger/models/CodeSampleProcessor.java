package io.github.atkawa7.httpsnippet.demo.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import com.google.common.base.Optional;
import com.smartbear.har.builder.HarHeaderBuilder;
import com.smartbear.har.builder.HarPostDataBuilder;
import com.smartbear.har.builder.HarRequestBuilder;
import com.smartbear.har.model.HarHeader;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.HttpSnippetCodeGenerator;
import io.github.atkawa7.httpsnippet.demo.dto.SpeakerDTO;
import io.github.atkawa7.httpsnippet.demo.swagger.extensions.CodeSampleVendorExtension;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.generators.c.LibCurl;
import io.github.atkawa7.httpsnippet.generators.csharp.RestSharp;
import io.github.atkawa7.httpsnippet.generators.go.GoNative;
import io.github.atkawa7.httpsnippet.generators.java.OkHttp;
import io.github.atkawa7.httpsnippet.generators.javascript.JQuery;
import io.github.atkawa7.httpsnippet.generators.node.NodeNative;
import io.github.atkawa7.httpsnippet.generators.objc.ObjNSURLSession;
import io.github.atkawa7.httpsnippet.generators.python.Python3Native;
import io.github.atkawa7.httpsnippet.generators.ruby.RubyNative;
import io.github.atkawa7.httpsnippet.generators.shell.Curl;
import io.github.atkawa7.httpsnippet.http.HttpVersion;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 100)
public class CodeSampleProcessor implements OperationBuilderPlugin {
private static final String SPRING_APPLICATION_BASE_URL = "spring.application.base-url";
private static final Faker FAKER = new Faker();
private final List<CodeGenerator> generators;
private final Environment environment;

public CodeSampleProcessor(Environment environment) {
	this.generators = new ArrayList<>();
	this.environment = environment;
	this.add(new OkHttp());
	this.add(new LibCurl());
	this.add(new RestSharp());
	this.add(new GoNative());
	this.add(new JQuery());
	this.add(new NodeNative());
	this.add(new ObjNSURLSession());
	this.add(new RubyNative());
	this.add(new Python3Native());
	this.add(new Curl());
}

private void add(CodeGenerator generator) {
	this.generators.add(generator);
}

@Override
public void apply(OperationContext operationContext) {
	List<ResolvedMethodParameter> resolvedParameters = operationContext.getParameters();

	Object example = null;

	if (resolvedParameters != null) {
	for (ResolvedMethodParameter resolvedMethodParameter : resolvedParameters) {
		Optional<String> optional = resolvedMethodParameter.defaultName();

		if (optional.isPresent()) {
		String name = optional.get();
		logger.info("Current parameter is {}", name);
		}

		ResolvedType resolvedType = resolvedMethodParameter.getParameterType();
		Optional<RequestBody> requestBody =
			resolvedMethodParameter.findAnnotation(RequestBody.class);
		if (requestBody.isPresent()) {
		final Class<?> erasedType = resolvedType.getErasedType();
		if (erasedType.isAssignableFrom(SpeakerDTO.class)) {
			SpeakerDTO speakerDTO =
				SpeakerDTO.builder()
					.firstName(FAKER.name().firstName())
					.lastName(FAKER.name().lastName())
					.company(FAKER.company().name())
					.biography(FAKER.lorem().paragraph())
					.build();
			String username =
				String.format(
					"%s.%s", speakerDTO.getFirstName().charAt(0), speakerDTO.getLastName());

			speakerDTO.setLinkedIn(String.format("https://www.linkedin.com/in/%s", username));
			speakerDTO.setFacebook(String.format("https://www.facebook.com/%s", username));
			speakerDTO.setGithub(String.format("https://github.com/%s", username));
			speakerDTO.setTwitter(String.format("https://twitter.com/%s", username));
			example = speakerDTO;
		}
		}
	}
	}

	List<HarHeader> headers = new ArrayList<>();

	Set<NameValueExpression<String>> nameValueExpressions = operationContext.headers();
	if (nameValueExpressions != null && nameValueExpressions.size() > 0) {
	for (NameValueExpression<String> nameValueExpression : nameValueExpressions) {
		HarHeader harHeader =
			new HarHeaderBuilder()
				.withName(nameValueExpression.getName())
				.withValue(nameValueExpression.getValue())
				.build();
		headers.add(harHeader);
	}
	}

	String body;
	try {
	body = ObjectUtils.isNull(example) ? "" : ObjectUtils.writeValueAsString(example);
	} catch (JsonProcessingException e) {
	throw new RuntimeException("Failed to create json from example");
	}

	HarPostData postData =
		new HarPostDataBuilder().withMimeType(MediaType.APPLICATION_JSON).withText(body).build();

	HarRequest harRequest =
		new HarRequestBuilder()
			.withMethod(operationContext.httpMethod().name())
			.withUrl(
				String.format(
					"%s%s",
					environment.getProperty(SPRING_APPLICATION_BASE_URL),
					operationContext.requestMappingPattern()))
			.withHeaders(headers)
			.withHttpVersion(HttpVersion.HTTP_1_1.toString())
			.withPostData(postData)
			.build();

	try {
	final List<CodeSample> codeSamples =
		new HttpSnippetCodeGenerator(generators)
			.snippets(harRequest).stream()
				.map(
					c ->
						CodeSample.builder()
							.label(c.getClient().getTitle())
							.lang(c.getLanguage().getTitle())
							.source(c.getCode())
							.build())
				.collect(Collectors.toList());
	if (codeSamples.size() > 0) {
		final CodeSampleVendorExtension vendorExtension =
			new CodeSampleVendorExtension("x-code-samples", codeSamples);
		operationContext.operationBuilder().extensions(Collections.singletonList(vendorExtension));
	}
	} catch (Exception e) {
	logger.error("Failed to create code samples", e);
	throw new RuntimeException("Failed to create sample codes");
	}
}

@Override
public boolean supports(DocumentationType documentationType) {
	return DocumentationType.SWAGGER_2.equals(documentationType);
}
}
