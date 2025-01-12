package io.github.atkawa7.httpsnippet.generators.node;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class NodeNative extends CodeGenerator {
  public NodeNative() {
    super(Client.NODE, Language.NODE);
  }

  @Override
  protected String generateCode(final CodeRequest codeRequest) throws Exception {
    CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

    Map<String, Object> reqOpts = new LinkedHashMap<>();
    reqOpts.put("method", codeRequest.getMethod());
    reqOpts.put("hostname", codeRequest.getHost());
    reqOpts.put("port", codeRequest.getPort());
    reqOpts.put("path", codeRequest.getFullPath());
    reqOpts.put("headers", codeRequest.allHeadersAsMap());

    if (MediaType.APPLICATION_FORM_URLENCODED.equalsIgnoreCase(codeRequest.getMimeType())) {
      code.push("var qs = require(\"querystring\");");
    }

    if (codeRequest.hasAttachments()) {
      code.push("var fs = require(\"fs\")");
    }

    code.push("var http = require(\"%s\");", codeRequest.getProtocol().toLowerCase());

    if (MediaType.MULTIPART_FORM_DATA.equalsIgnoreCase(codeRequest.getMimeType())) {
      code.push("var FormData = require(\"form-data\");");
    } else {
      code.blank()
          .push("var options = %s;", toPrettyJson(reqOpts))
          .blank()
          .push("var req = http.request(options, function (res) {")
          .push(1, "var chunks = [];")
          .blank()
          .push(1, "res.on(\"data\", function (chunk) {")
          .push(2, "chunks.push(chunk);")
          .push(1, "});")
          .blank()
          .push(1, "res.on(\"end\", function () {")
          .push(2, "var body = Buffer.concat(chunks);")
          .push(2, "console.log(body.toString());")
          .push(1, "});")
          .push("});")
          .blank();
    }

    if (codeRequest.hasBody()) {
      switch (codeRequest.getMimeType()) {
        case MediaType.APPLICATION_FORM_URLENCODED:
          {
            if (codeRequest.hasParams()) {
              code.push("req.write(qs.stringify(%s));", codeRequest.paramsToJSONString());
            }
          }
          break;
        case MediaType.APPLICATION_JSON:
          if (codeRequest.hasText()) {
            code.push("req.write(JSON.stringify(%s));", codeRequest.getText());
          }
          break;

        case MediaType.MULTIPART_FORM_DATA:
          if (codeRequest.hasParams()) {
            code.push("var form = new FormData()");

            codeRequest
                .getParams()
                .forEach(
                    p -> {
                      if (StringUtils.isNotBlank(p.getFileName())) {
                        code.push(
                            "form.append(\"%s\", fs.createReadStream(\"%s\"));",
                            p.getName(), p.getFileName());
                      } else {
                        code.push("form.append(\"%s\", \"%s\");", p.getName(), p.getValue());
                      }
                    });

            reqOpts.put("headers", "[headers]");

            code.push("let headers = form.getHeaders();");

            code.blank()
                .push(
                    "var options = %s;", toPrettyJson(reqOpts).replace("\"[headers]\"", "headers"))
                .blank()
                .push("var req = http.request(options, function (res) {")
                .push(1, "var chunks = [];")
                .blank()
                .push(1, "res.on(\"data\", function (chunk) {")
                .push(2, "chunks.push(chunk);")
                .push(1, "});")
                .blank()
                .push(1, "res.on(\"end\", function () {")
                .push(2, "var body = Buffer.concat(chunks);")
                .push(2, "console.log(body.toString());")
                .push(1, "});")
                .push("});")
                .blank()
                .push("form.pipe(req);")
                .blank();
          }
          break;

        default:
          if (codeRequest.hasText()) {
            code.push("req.write(\"%s\");", codeRequest.getText());
          }
      }
    }

    if (!MediaType.MULTIPART_FORM_DATA.equalsIgnoreCase(codeRequest.getMimeType())) {
      code.push("req.end();").blank();
    }

    return code.join();
  }
}
