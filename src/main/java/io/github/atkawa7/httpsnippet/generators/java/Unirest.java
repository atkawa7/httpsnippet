package io.github.atkawa7.httpsnippet.generators.java;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class Unirest extends CodeGenerator {
  private static final List<String> SUPPORTED_METHODS =
      Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

  public Unirest() {
    super(Client.UNIREST, Language.JAVA);
  }

  public boolean isNotSupported(String method) {
    return !SUPPORTED_METHODS.contains(method.toUpperCase());
  }

  @Override
  protected String generateCode(final CodeRequest codeRequest) throws Exception {
    CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

    String method = codeRequest.getMethod();

    if (isNotSupported(method)) {
      code.push(
          "HttpResponse<String> response = Unirest.customMethod(\"%s\",\"%s\")",
          method.toUpperCase(), codeRequest.getFullUrl());
    } else {
      code.push(
          "HttpResponse<String> response = Unirest.%s(\"%s\")",
          method.toLowerCase(), codeRequest.getFullUrl());
    }

    if (codeRequest.hasHeadersAndCookies()) {
      codeRequest
          .allHeadersAsMap()
          .forEach(
              (k, v) -> {
                code.push(1, ".header(\"%s\", \"%s\")", k, v);
              });
    }
    if (codeRequest.hasBody()) {
      switch (codeRequest.getMimeType()) {
        case MediaType.APPLICATION_JSON:
          if (codeRequest.hasText()) {
            code.push(1, ".body(%s)", codeRequest.toJsonString());
          }
          break;

        case MediaType.APPLICATION_FORM_URLENCODED:
          if (codeRequest.hasParams()) {
            codeRequest
                .getParams()
                .forEach(p -> code.push(1, ".field(\"%s\",\"%s\")", p.getName(), p.getValue()));
          }
          break;

        case MediaType.MULTIPART_FORM_DATA:
          {
            if (codeRequest.hasParams()) {
              codeRequest
                  .getParams()
                  .forEach(
                      p -> {
                        if (StringUtils.isNotBlank(p.getFileName())) {
                          code.push(
                              1, ".field(\"%s\", new File(\"%s\"))", p.getName(), p.getFileName());
                        } else {
                          code.push(1, ".field(\"%s\",\"%s\")", p.getName(), p.getValue());
                        }
                      });
            }
          }
          break;
        default:
          {
            if (codeRequest.hasText()) {
              code.push(1, ".body(\"%s\")", codeRequest.getText());
            }
          }
      }
    }

    code.push(1, ".asString();").blank();

    return code.join();
  }
}
