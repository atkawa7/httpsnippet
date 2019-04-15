package io.github.atkawa7.httpsnippet.generators.javascript;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.smartbear.har.model.HarParam;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class XMLHttpRequest extends CodeGenerator {
  private boolean cors;

  public XMLHttpRequest() {
    super(Client.XHR, Language.JAVASCRIPT);
    this.cors = true;
  }

  @Override
  protected String generateCode(final CodeRequest codeRequest) throws Exception {
    CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

    if (codeRequest.hasBody()) {
      switch (codeRequest.getMimeType()) {
        case MediaType.APPLICATION_JSON:
          if (codeRequest.hasText()) {
            code.push("var data = JSON.stringify(%s);", codeRequest.toPrettyJsonString())
                .push(CodeBuilder.EMPTY);
          }
          break;
        case MediaType.APPLICATION_FORM_URLENCODED:
          if (codeRequest.hasParams()) {
            code.push("var data = %s;", toJson(codeRequest.paramsToString())).blank();
          }
          break;

        case MediaType.MULTIPART_FORM_DATA:
          if (codeRequest.hasParams()) {
            code.push("var data = new FormData();");
            for (HarParam harParam : codeRequest.getParams()) {
              String value =
                  StringUtils.firstNonEmpty(
                      harParam.getFileName(), harParam.getValue(), CodeBuilder.SPACE);
              code.push("data.append(%s, %s);", toJson(harParam.getName()), toJson(value));
            }

            code.blank();
          }
          break;
        default:
          {
            code.push("var data = %s;", toJson(codeRequest.getText())).blank();
          }
      }
    } else {
      code.push("var data = null;").blank();
    }

    code.push("var xhr = new XMLHttpRequest();");

    if (cors) {
      code.push("xhr.withCredentials = true;");
    }

    code.blank()
        .push("xhr.addEventListener(\"readystatechange\", function () {")
        .push(1, "if (this.readyState === this.DONE) {")
        .push(2, "console.log(this.responseText);")
        .push(1, "}")
        .push("});")
        .blank()
        .push(
            "xhr.open(%s, %s);", toJson(codeRequest.getMethod()), toJson(codeRequest.getFullUrl()));

    Map<String, String> allHeaders = codeRequest.allHeadersAsMap();

    if (ObjectUtils.isNotEmpty(allHeaders)) {
      allHeaders.forEach(
          (k, v) -> {
            try {
              code.push("xhr.setRequestHeader(%s, %s);", toJson(k), toJson(v));
            } catch (Exception e) {
            }
          });
    }

    code.blank().push("xhr.send(data);").blank();

    return code.join();
  }
}
