package io.github.atkawa7.httpsnippet.generators.node;

import com.smartbear.har.model.*;
import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeRequest extends CodeGenerator {
    public NodeRequest() {
        super(Client.NODE_REQUEST, Language.NODE);
    }

    @Override
    protected String generateCode(final HarRequest harRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);
        boolean includeFS = false;

        code.push("var request = require(\"request\");").blank();

        Map<String, Object> reqOpts = new HashMap<>();
        reqOpts.put("method", harRequest.getMethod());
        reqOpts.put("url", harRequest.getUrl());

        List<HarQueryString> queryStrings = harRequest.getQueryString();
        if (ObjectUtils.isNotEmpty(queryStrings)) {
            reqOpts.put("qs", asQueryStrings(queryStrings));
        }

        List<HarHeader> headers = harRequest.getHeaders();

        if (ObjectUtils.isNotEmpty(headers)) {
            reqOpts.put("headers", asHeaders(headers));
        }

        HarPostData postData = harRequest.getPostData();
        if (hasText(postData)) {
            String mimeType  = this.getMimeType(postData);
            switch (mimeType) {
                case MediaType.APPLICATION_FORM_URLENCODED:
                    reqOpts.put("forms", asParams(postData.getParams()));
                    break;

                case MediaType.APPLICATION_JSON: {
                    reqOpts.put("body", postData.getText());
                    reqOpts.put("json", Boolean.TRUE);
                }
                break;

                case MediaType.MULTIPART_FORM_DATA: {
                    Map<String, Object> formData = new HashMap<>();
                    List<HarParam> params = postData.getParams();
                    if (ObjectUtils.isNotEmpty(params)) {
                        for (HarParam param : params) {
                            Map<String, Object> attachment = new HashMap<>();

                            if (StringUtils.isEmpty(param.getFileName())
                                    && StringUtils.isEmpty(param.getContentType())) {
                                formData.put(param.getName(), param.getValue());
                            } else if (StringUtils.isNotEmpty(param.getFileName())
                                    && StringUtils.isEmpty(param.getValue())) {
                                includeFS = true;
                                attachment.put("value", "fs.createReadStream(\"" + param.getFileName() + "\")");
                            } else if (StringUtils.isNotEmpty(param.getValue())) {
                                attachment.put("value", param.getValue());
                            }

                            if (StringUtils.isNotEmpty(param.getFileName())) {
                                Map<String, Object> options = new HashMap<>();
                                options.put("fileName", param.getFileName());
                                options.put(
                                        "contentType", StringUtils.defaultString(param.getContentType(), null));
                                attachment.put("options", options);
                                formData.put(param.getName(), attachment);
                            }
                        }

                        reqOpts.put("formData", formData);
                    }
                }
                break;

                default: {
                    reqOpts.put("body", postData.getText());
                }
            }
        }

        List<HarCookie> cookies = harRequest.getCookies();
        // construct cookies argument
        if (ObjectUtils.isNotEmpty(cookies)) {
            reqOpts.put("jar", "JAR");
            code.push("var jar = request.jar();");

            for (HarCookie harCookie : cookies) {
                code.push(
                        "jar.setCookie(request.cookie(\"%s=%s\"), \"%s\");",
                        harCookie.getName(), harCookie.getValue(), harCookie.getDomain());
            }
            code.blank();
        }

        if (includeFS) {
            code.push("var fs = require(\"fs\");");
        }

        code.push("var options = %s;", toJson(reqOpts)).blank();

        code.push("request(options, %s", "function (error, response, body) {")
                .push(1, "if (error) throw new Error(error);")
                .blank()
                .push(1, "console.log(body);")
                .push("});")
                .blank();

        return code.join().replace("\"JAR\"", "jar");
    }
}
