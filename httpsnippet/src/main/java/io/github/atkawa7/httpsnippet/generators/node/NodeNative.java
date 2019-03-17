package io.github.atkawa7.httpsnippet.generators.node;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

import java.util.HashMap;
import java.util.Map;

public class NodeNative extends CodeGenerator {
    public NodeNative() {
        super(Client.NODE, Language.NODE);
    }

    @Override
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

        Map<String, Object> reqOpts = new HashMap<>();
        reqOpts.put("method", codeRequest.getMethod());
        reqOpts.put("hostname", codeRequest.getHost());
        reqOpts.put("port", codeRequest.getPort());
        reqOpts.put("path", codeRequest.getPath());
        reqOpts.put("headers", codeRequest.allHeadersAsMap());
        code.push("var http = require(\"%s\");", codeRequest.getProtocol().toLowerCase());

        code.blank()
                .push("var options = %s;", toJson(reqOpts))
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

        if (codeRequest.hasBody()) {
            switch (codeRequest.getMimeType()) {
                case MediaType.APPLICATION_FORM_URLENCODED: {
                    if (codeRequest.hasParams()) {
                        code.push("var qs = require(\"querystring\");");
                        code.push("req.write(qs.stringify(%s));", codeRequest.paramsToJSONString());
                    }
                }
                break;
                case MediaType.APPLICATION_JSON:
                    if (codeRequest.hasText()) {
                        code.push("req.write(%s);", codeRequest.toJsonString());
                    }
                    break;
                default:
                    if (codeRequest.hasText()) {
                        code.push("req.write(%s);", codeRequest.getText());
                    }
            }
        }

        code.push("req.end();");

        return code.join();
    }
}
