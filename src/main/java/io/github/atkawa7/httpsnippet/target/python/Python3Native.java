package io.github.atkawa7.httpsnippet.target.python;

import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.http.HttpScheme;
import io.github.atkawa7.httpsnippet.target.Target;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import lombok.NonNull;

import java.net.URL;
import java.util.Map;

public class Python3Native extends Target {
    public Python3Native() {
        super(Client.PYTHON3, Language.PYTHON);
    }

    @Override
    public String code(@NonNull final HarRequest harRequest) throws Exception {

        URL url = new URL(harRequest.getUrl());
        CodeBuilder code = new CodeBuilder();
        // Start Request
        code.push("import http.client").blank();

        if (HttpScheme.HTTPS.equalsIgnoreCase(url.getProtocol())) {
            code.push(
                    "conn = http.client.HTTPSConnection(\"%s\", \"%s\")",
                    url.getHost(), Integer.toString(url.getPort()))
                    .blank();
        } else {
            code.push(
                    "conn = http.client.HTTPConnection(\"%s\", \"%s\")",
                    url.getHost(), Integer.toString(url.getPort()))
                    .blank();
        }

        HarPostData postData = harRequest.getPostData();

        String payload = null;
        // Create payload string if it exists

        if (hasText(postData)) {
            payload = toJson(postData.getText());
            code.push("payload = %s", payload).blank();
        }

        // Create HttpHeaders
        Map<String, String> headers = asHeaders(harRequest);
        if (headers.size() == 1) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                code.push("headers = { \"%s\": \"%s\" }", header.getKey(), header.getValue()).blank();
            }
        } else if (headers.size() > 1) {
            int count = 1;

            code.push("headers = {");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (count++ != headers.size()) {
                    code.push("    \"%s\": \"%s\",", header.getKey(), header.getValue());
                } else {
                    code.push("    \"%s\": \"%s\"", header.getKey(), header.getValue());
                }
            }

            code.push("    }").blank();
        }

        // Make Request
        String method = harRequest.getMethod();
        String path = url.getPath();

        if (ObjectUtils.isNotNull(payload) && headers.size() > 0) {
            code.push("conn.request(\"%s\", \"%s\", payload, headers)", method, path);
        } else if (ObjectUtils.isNotNull(payload)) {
            code.push("conn.request(\"%s\", \"%s\", payload)", method, path);
        } else if (headers.size() > 0) {
            code.push("conn.request(\"%s\", \"%s\", headers=headers)", method, path);
        } else {
            code.push("conn.request(\"%s\", \"%s\")", method, path);
        }

        // Get Response
        code.blank()
                .push("res = conn.getresponse()")
                .push("data = res.read()")
                .blank()
                .push("print(data.decode(\"utf-8\"))");

        return code.join();
    }
}
