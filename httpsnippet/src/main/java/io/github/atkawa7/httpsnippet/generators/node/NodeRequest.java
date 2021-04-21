package io.github.atkawa7.httpsnippet.generators.node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.atkawa7.har.HarParam;
import org.apache.commons.lang3.StringUtils;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class NodeRequest extends CodeGenerator {
  public NodeRequest() {
    super(Client.NODE_REQUEST, Language.NODE);
  }

  @Override
  protected String generateCode(final CodeRequest codeRequest) throws Exception {
    CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

    if (codeRequest.hasAttachments()) {
      code.push("var fs = require(\"fs\");");
    }
    code.push("var request = require(\"request\");").blank();

    Map<String, Object> reqOpts = new LinkedHashMap<>();
    reqOpts.put("method", codeRequest.getMethod());
    reqOpts.put("url", codeRequest.getUrl());

    if (codeRequest.hasCookies()) {
      reqOpts.put("jar", "JAR");
      code.push("var jar = request.jar();");
      codeRequest
          .getCookies()
          .forEach(
              (h) ->
                  code.push(
                      "jar.setCookie(request.cookie(\"%s=%s\"), \"%s\");",
                      h.getName(), h.getValue(), codeRequest.getUrl()));
      code.blank();
    }

    if (codeRequest.hasQueryStrings()) {
      reqOpts.put("qs", codeRequest.unwrapQueryStrings());
    }

    if (codeRequest.hasHeaders()) {
      reqOpts.put("headers", codeRequest.headersAsMap());
    }

    if (codeRequest.hasBody()) {

      switch (codeRequest.getMimeType()) {
        case MediaType.APPLICATION_FORM_URLENCODED:
          if (codeRequest.hasParams()) {
            reqOpts.put("form", codeRequest.paramsAsMap());
          }
          break;

        case MediaType.APPLICATION_JSON:
          if (codeRequest.hasText()) {
            reqOpts.put("body", codeRequest.fromJsonString());
            reqOpts.put("json", Boolean.TRUE);
          }
          break;

        case MediaType.MULTIPART_FORM_DATA:
          if (codeRequest.hasParams()) {
            Map<String, Object> formData = new HashMap<>();
            for (HarParam param : codeRequest.getParams()) {
              if (StringUtils.isNotEmpty(param.getFileName())) {
                Map<String, Object> options = new HashMap<>();
                options.put("filename", param.getFileName());
                options.put("contentType", param.getContentType());

                Map<String, Object> fileAttachment = new HashMap<>();
                fileAttachment.put("value", "fs.createReadStream(\"" + param.getFileName() + "\")");
                fileAttachment.put("options", options);

                formData.put(param.getName(), fileAttachment);

              } else {
                formData.put(param.getName(), param.getValue());
              }
            }
            reqOpts.put("formData", formData);
          }
          break;
        default:
          if (codeRequest.hasText()) {
            reqOpts.put("body", codeRequest.getText());
          }
      }
    }

    code.push("var options = %s;", toPrettyJson(reqOpts)).blank();
    code.push("request(options, %s", "function (error, response, body) {")
        .push(1, "if (error) throw new Error(error);")
        .blank()
        .push(1, "console.log(body);")
        .push("});")
        .blank();

    return code.join()
        .replace("\"JAR\"", "jar")
        .replace("\"fs.createReadStream(\\\"", "fs.createReadStream(\"")
        .replace("\\\")\"", "\")");
  }
}
