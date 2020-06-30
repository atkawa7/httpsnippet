package io.github.atkawa7.httpsnippet.generators.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.atkawa7.har.HarCookie;
import io.atkawa7.har.HarParam;
import org.apache.commons.lang3.StringUtils;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class NodeUnirest extends CodeGenerator {
  public NodeUnirest() {
    super(Client.NODE_UNIREST, Language.NODE);
  }

  @Override
  protected String generateCode(final CodeRequest codeRequest) throws Exception {
    CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

    if (codeRequest.hasAttachments()) {
      code.push("var fs = require(\"fs\");");
    }
    code.push("var unirest = require(\"unirest\");")
        .blank()
        .push("var req = unirest(\"%s\", \"%s\");", codeRequest.getMethod(), codeRequest.getUrl())
        .blank();

    List<HarCookie> cookies = codeRequest.getCookies();

    if (codeRequest.hasCookies()) {
      code.push("var CookieJar = unirest.jar();");

      cookies.forEach(
          harCookie -> {
            code.push(
                "CookieJar.add(\"%s=%s\",\"%s\");",
                harCookie.getName(), harCookie.getValue(), codeRequest.getUrl());
          });

      code.push("req.jar(CookieJar);").blank();
    }

    if (codeRequest.hasQueryStrings()) {
      code.push("req.query(%s);", codeRequest.queryStringsToJsonString()).blank();
    }

    if (codeRequest.hasHeaders()) {
      code.push("req.headers(%s);", codeRequest.headersToJsonString()).blank();
    }

    if (codeRequest.hasBody()) {
      switch (codeRequest.getMimeType()) {
        case MediaType.APPLICATION_FORM_URLENCODED:
          if (codeRequest.hasParams()) {
            code.push("req.form(%s);", codeRequest.paramsToPrettyJSONString());
          }
          break;

        case MediaType.APPLICATION_JSON:
          if (codeRequest.hasText()) {
            code.push("req.type(\"json\");")
                .push("req.send(%s);", codeRequest.toPrettyJsonString());
          }
          break;

        case MediaType.MULTIPART_FORM_DATA:
          if (codeRequest.hasParams()) {
            List<Object> multipart = new ArrayList<>();

            for (HarParam param : codeRequest.getParams()) {
              Map<String, Object> part = new HashMap<>();

              if (StringUtils.isNotEmpty(param.getFileName())) {
                part.put("body", "fs.createReadStream(\"" + param.getFileName() + "\")");
              } else {
                part.put("body", param.getValue());
              }

              if (StringUtils.isNotEmpty(param.getContentType())) {
                part.put("content-type", param.getContentType());
              }
              multipart.add(part);
            }

            code.push("req.multipart(%s);", toPrettyJson(multipart));
          }
          break;

        default:
          if (codeRequest.hasText()) {
            code.push(CodeBuilder.SPACE + "req.send(\"%s\");", codeRequest.getText());
          }
      }
    }

    code.blank()
        .push("req.end(function (res) {")
        .push(1, "if (res.error) throw new Error(res.error);")
        .blank()
        .push(1, "console.log(res.body);")
        .push("});")
        .blank();

    return code.join()
        .replace("\"fs.createReadStream(\\\"", "fs.createReadStream(\"")
        .replace("\\\")\"", "\")");
  }
}
