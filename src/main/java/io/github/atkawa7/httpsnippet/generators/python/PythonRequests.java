package io.github.atkawa7.httpsnippet.generators.python;

import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarQueryString;
import com.smartbear.har.model.HarRequest;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public class PythonRequests extends CodeGenerator {
    public PythonRequests() {
        super(Client.PYTHON_REQUESTS, Language.PYTHON);
    }

    @Override
    public String code(@NonNull final HarRequest harRequest) throws Exception {
        // Start code
        CodeBuilder code = new CodeBuilder("    ");

        // Import requests
        code.push("import requests").blank();

        // Set URL
        code.push("url = \"%s\"", harRequest.getUrl()).blank();

        List<HarQueryString> queryStrings = harRequest.getQueryString();
        // Construct query string
        if (ObjectUtils.isNotEmpty(queryStrings)) {
            String qs = "querystring = " + toJson(asQueryStrings(queryStrings));

            code.push(qs).blank();
        }

        // Construct payload
        String payload = null;

        HarPostData postData = harRequest.getPostData();
        if (hasText(postData)) {
            payload = toJson(postData.getText());
            code.push("payload = %s", payload);
        }

        // Construct headers
        Map<String, String> headers = asHeaders(harRequest);
        int headerCount = headers.size();

        if (headerCount == 1) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                code.push("headers = {\"%s\": \"%s\"}", header.getKey(), header.getValue()).blank();
            }
        } else if (headerCount > 1) {
            int count = 1;

            code.push("headers = {");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (count++ != headerCount) {
                    code.push(1, "\"%s\": \"%s\",", header.getKey(), header.getValue());
                } else {
                    code.push(1, "\"%s\": \"%s\"", header.getKey(), header.getValue());
                }
            }

            code.push(1, "}").blank();
        }

        // Construct request
        String method = harRequest.getMethod();
        String request = String.format("response = requests.request(\"%s\", url", method);

        if (ObjectUtils.isNotEmpty(payload)) {
            request += ", data=payload";
        }

        if (headerCount > 0) {
            request += ", headers=headers";
        }

        if (ObjectUtils.isNotEmpty(queryStrings)) {
            request += ", params=querystring";
        }

        request += ")";

        code.push(request)
                .blank()

                // Print response
                .push("print(response.text)");

        return code.join();
    }
}
