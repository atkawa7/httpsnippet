package io.github.atkawa7.httpsnippet.generators.node;

import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import com.smartbear.har.model.*;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeUnirest extends CodeGenerator {
    public NodeUnirest() {
        super(Client.NODE_UNIREST, Language.NODE);
    }

    @Override
    public String code(@NonNull final HarRequest harRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);
        boolean includeFS = false;

        code.push("var unirest = require(\"unirest\");")
                .blank()
                .push("var req = unirest(\"%s\", \"%s\");", harRequest.getMethod(), harRequest.getUrl())
                .blank();

        List<HarCookie> cookies = harRequest.getCookies();

        if (ObjectUtils.isNotEmpty(cookies)) {
            code.push("var CookieJar = unirest.jar();");

            cookies.forEach(
                    harCookie -> {
                        code.push(
                                "CookieJar.add(\"%s=%s\",\"%s\");",
                                harCookie.getName(), harCookie.getValue(), harCookie.getDomain());
                    });

            code.push("req.jar(CookieJar);").blank();
        }

        List<HarQueryString> queryStrings = harRequest.getQueryString();

        if (ObjectUtils.isNotEmpty(queryStrings)) {
            code.push("req.query(%s);", toJson(asQueryStrings(queryStrings))).blank();
        }

        List<HarHeader> headers = harRequest.getHeaders();

        if (ObjectUtils.isNotEmpty(headers)) {
            code.push("req.headers(%s);", toJson(asHeaders(headers))).blank();
        }

        HarPostData postData = harRequest.getPostData();

        if (ObjectUtils.isNotNull(postData)) {
            List<HarParam> params = postData.getParams();
            switch (postData.getMimeType()) {
                case HttpHeaders.APPLICATION_FORM_URLENCODED:
                    if (hasParams(params)) {
                        code.push("req.form(%s);", toJson(asParams(params)));
                    }
                    break;

                case HttpHeaders.APPLICATION_JSON:
                    if (hasText(postData)) {
                        code.push("req.type(\"json\");").push("req.send(%s);", postData.getText());
                    }
                    break;

                case HttpHeaders.MULTIPART_FORM_DATA:
                    if (hasParams(params)) {
                        List<Object> multipart = new ArrayList<>();

                        for (HarParam param : params) {
                            Map<String, Object> part = new HashMap<>();

                            if (StringUtils.isNotEmpty(param.getFileName())
                                    && StringUtils.isNotEmpty(param.getValue())) {
                                includeFS = true;
                                part.put("body ", "fs.createReadStream(\"" + param.getFileName() + "\")");
                            } else if (StringUtils.isNotEmpty(param.getValue())) {
                                part.put("body", param.getValue());
                            }

                            if (part.containsKey("body")) {
                                if (StringUtils.isNotEmpty(param.getContentType())) {
                                    part.put("content-type", param.getContentType());
                                }

                                multipart.add(part);
                            }
                        }

                        code.push("req.multipart(%s);", toJson(multipart));
                    }
                    break;

                default:
                    if (hasText(postData)) {
                        code.push(CodeBuilder.SPACE + "req.send(%s);", postData.getText());
                    }
            }
        }

        if (includeFS) {
            code.push("var fs = require(\"fs\");");
        }

        code.blank()
                .push("req.end(function (res) {")
                .push(1, "if (res.error) throw new Error(res.error);")
                .blank()
                .push(1, "console.log(res.body);")
                .push("});")
                .blank();

        return code.join();
    }
}
