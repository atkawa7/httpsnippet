package io.github.atkawa7.httpsnippet.generators.javascript;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.smartbear.har.model.HarParam;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class JQuery extends CodeGenerator {

  private final Boolean async;
  private final Boolean crossDomain;

  public JQuery() {
    super(Client.JQUERY, Language.JAVASCRIPT);
    this.async = TRUE;
    this.crossDomain = TRUE;
  }

  @Override
  protected String generateCode(final CodeRequest codeRequest) throws Exception {
    CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

    Map<String, Object> settings = new LinkedHashMap<>();
    settings.put("async", async);
    settings.put("crossDomain", crossDomain);
    settings.put("url", codeRequest.getFullUrl());
    settings.put("method", codeRequest.getMethod());
    settings.put("headers", codeRequest.allHeadersAsMap());

    if (codeRequest.hasBody()) {
      switch (codeRequest.getMimeType()) {
        case MediaType.APPLICATION_FORM_URLENCODED:
          if (codeRequest.hasParams()) {
            settings.put("data", codeRequest.paramsAsMap());
          }
          break;

        case MediaType.APPLICATION_JSON:
          if (codeRequest.hasText()) {
            settings.put("processData", FALSE);
            settings.put("data", codeRequest.getText());
          }
          break;

        case MediaType.MULTIPART_FORM_DATA:
          if (codeRequest.hasParams()) {
            code.push("var form = new FormData();");

            for (HarParam harParam : codeRequest.getParams()) {
              String value =
                  StringUtils.firstNonEmpty(
                      harParam.getFileName(), harParam.getValue(), CodeBuilder.SPACE);
              code.push("form.append(%s, %s);", toJson(harParam.getName()), toJson(value));
            }

            settings.put("processData", FALSE);
            settings.put("contentType", FALSE);
            settings.put("mimeType", MediaType.MULTIPART_FORM_DATA);
            settings.put("data", "[form]");
            code.blank();
          }
          break;
        default:
          if (codeRequest.hasText()) {
            settings.put("data", codeRequest.getText());
          }
      }
    }

    code.push("var settings = " + toPrettyJson(settings).replace("\"[form]\"", "form"))
        .blank()
        .push("$.ajax(settings).done(function (response) {")
        .push(1, "console.log(response);")
        .push("});")
        .blank();

    return code.join();
  }
}
