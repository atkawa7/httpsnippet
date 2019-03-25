package io.github.atkawa7.httpsnippet.generators.javascript;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class Fetch extends CodeGenerator {
  public Fetch() {
    super(Client.FETCH, Language.JAVASCRIPT);
  }

  @Override
  protected String generateCode(CodeRequest codeRequest) throws Exception {
    CodeBuilder code = new CodeBuilder(" ");

    String url = codeRequest.getFullUrl();
    Map<String, Object> fetchOptions = new LinkedHashMap<>();
    fetchOptions.put("mode", "cors");
    fetchOptions.put("method", codeRequest.getMethod());
    fetchOptions.put("headers", codeRequest.allHeadersAsMap());

    switch (codeRequest.getMimeType()) {
      case MediaType.APPLICATION_FORM_URLENCODED:
        if (codeRequest.hasParams()) {
          code.push("const details =%s;", codeRequest.paramsToJSONString());
          code.push("const form = Object.entries(details)");
          code.push(
              4,
              ".map(([key, value]) => encodeURIComponent(key) + '=' + encodeURIComponent(value))");
          code.push(4, ".join('&')");
          fetchOptions.put("body", "[form]");
        }
        break;

      case MediaType.APPLICATION_JSON:
        if (codeRequest.hasText()) {
          fetchOptions.put("body", codeRequest.getText());
        }
        break;

      case MediaType.MULTIPART_FORM_DATA:
        if (codeRequest.hasParams()) {
          code.push("let form = new FormData();");
          codeRequest
              .getParams()
              .forEach(
                  (param) -> {
                    String value =
                        StringUtils.isNotBlank(param.getFileName())
                            ? param.getFileName()
                            : param.getValue();
                    code.push("form.append(\"%s\", \"%s\");", param.getName(), value);
                  });
          fetchOptions.put("body", "[form]");

          code.blank();
        }

        break;

      default:
        if (codeRequest.hasText()) {
          fetchOptions.put("body", codeRequest.getText());
        }
    }

    code.push("const fetchOptions = " + toPrettyJson(fetchOptions).replace("\"[form]\"", "form"))
        .blank()
        .push("fetch(\"" + url + "\", fetchOptions)")
        .push(1, ".then(response => response.json())")
        .push(1, ".then(data => console.log(data))")
        .push(1, ".catch(error => console.log(error));");

    return code.join();
  }
}
