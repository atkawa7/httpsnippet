package io.github.atkawa7.httpsnippet.generators.csharp;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class RestSharp extends CodeGenerator {
  private final List<String> SUPPORTED_METHODS =
      Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

  public RestSharp() {
    super(Client.RESTSHARP, Language.CSHARP);
  }

  public boolean isNotSupported(String method) {
    return !SUPPORTED_METHODS.contains(method.toUpperCase());
  }

  @Override
  protected String generateCode(final CodeRequest codeRequest) throws Exception {
    if (isNotSupported(codeRequest.getMethod())) {
      throw new Exception("Method not supported");
    }

    CodeBuilder code = new CodeBuilder();
    code.push("var client = new RestClient(\"%s\");", codeRequest.getFullUrl());
    code.push("var request = new RestRequest(Method.%s);", codeRequest.getMethod().toUpperCase());

    if (codeRequest.hasHeaders()) {
      codeRequest
          .getHeaders()
          .forEach(
              harHeader ->
                  code.push(
                      "request.AddHeader(\"%s\", \"%s\");",
                      harHeader.getName(), harHeader.getValue()));
    }

    if (codeRequest.hasCookies()) {
      codeRequest
          .getCookies()
          .forEach(
              cookie ->
                  code.push(
                      "request.AddCookie(\"%s\", \"%s\");", cookie.getName(), cookie.getValue()));
    }

    if (codeRequest.hasBody()) {
      switch (codeRequest.getMimeType()) {
        case MediaType.APPLICATION_JSON:
          if (codeRequest.hasText()) {
            code.push(
                "request.AddParameter(\"%s\", %s, ParameterType.RequestBody);",
                codeRequest.getMimeType(), codeRequest.toJsonString());
          }
          break;
        case MediaType.APPLICATION_FORM_URLENCODED:
          if (codeRequest.hasParams()) {
            code.push(
                "request.AddParameter(\"%s\", \"%s\", ParameterType.RequestBody);",
                codeRequest.getMimeType(), codeRequest.paramsToString());
          }
          break;

        case MediaType.MULTIPART_FORM_DATA:
          if (codeRequest.hasParams()) {
            codeRequest
                .getParams()
                .forEach(
                    p -> {
                      if (StringUtils.isNotBlank(p.getFileName())) {
                        code.push("request.AddFile(\"%s\", \"%s\");", p.getName(), p.getFileName());
                      } else {
                        code.push(
                            "request.AddParameter(\"%s\", \"%s\", ParameterType.RequestBody);",
                            p.getName(), p.getValue());
                      }
                    });
          }
          break;
        default:
          {
            code.push(
                "request.AddParameter(\"%s\", \"%s\", ParameterType.RequestBody);",
                codeRequest.getMimeType(), codeRequest.getText());
          }
      }
    }

    code.push("IRestResponse response = client.Execute(request);").blank();
    return code.join();
  }
}
