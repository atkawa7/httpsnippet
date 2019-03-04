package io.github.atkawa7.httpsnippet.generators.node;

import com.smartbear.har.model.HarParam;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeNative extends CodeGenerator {
    public NodeNative() {
        super(Client.NODE, Language.NODE);
    }

    @Override
    protected String generateCode(final HarRequest harRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);
        URL uri = new URL(harRequest.getUrl());

        Map<String, Object> reqOpts = new HashMap<>();
        reqOpts.put("method", harRequest.getMethod());
        reqOpts.put("hostname", uri.getHost());
        reqOpts.put("port", uri.getPort());
        reqOpts.put("path", uri.getPath());
        reqOpts.put("headers", asHeaders(harRequest));
        code.push("var http = require(\"%s\");", uri.getProtocol());

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

        HarPostData postData = harRequest.getPostData();
        if (hasText(postData)) {
            String mimeType  = this.getMimeType(postData);
            switch (mimeType) {
                case MediaType.APPLICATION_FORM_URLENCODED: {
                    List<HarParam> params = postData.getParams();
                    if (ObjectUtils.isNotEmpty(params)) {
                        code.push("var qs = require(\"querystring\");");
                        code.push("req.write(qs.stringify(%s));", toJson(asParams(params)));
                    }
                }

                break;

                case MediaType.APPLICATION_JSON:
                    code.push("req.write(JSON.stringify(%s));", postData.getText());
                    break;

                default:
                    code.push("req.write(%s);", postData.getText());
            }
        }

        code.push("req.end();");

        return code.join();
    }
}
