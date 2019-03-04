package io.github.atkawa7.httpsnippet.generators.java;

import com.smartbear.har.model.HarCookie;
import com.smartbear.har.model.HarHeader;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;

import java.util.Arrays;
import java.util.List;

public class OkHttp extends CodeGenerator {

    private static final List<String> SUPPORTED_METHODS =
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD");
    private static final List<String> SUPPORTS_BODY = Arrays.asList("POST", "PUT", "DELETE", "PATCH");

    public OkHttp() {
        super(Client.OKHTTP, Language.JAVA);
    }

    public boolean isNotSupportedMethod(String method) {
        return SUPPORTED_METHODS.indexOf(method) == -1;
    }

    public boolean supportsBody(String method) {
        return SUPPORTS_BODY.indexOf(method) >= 0;
    }

    @Override
    protected String generateCode(final HarRequest harRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

        code.push("OkHttpClient client = new OkHttpClient();").blank();

        HarPostData postData = harRequest.getPostData();
        boolean hasText = hasText(postData);

        if (hasText) {
            String mimeType = this.getMimeType(postData);
            code.push("MediaType mediaType = MediaType.parse(\"%s\");", mimeType);
            code.push(
                    "RequestBody body = RequestBody.create(mediaType, %s);", toJson(postData.getText()));
        }

        code.push("Request request = new Request.Builder()");
        code.push(1, ".url(\"%s\")", harRequest.getUrl());

        String method = harRequest.getMethod().toUpperCase();

        if (isNotSupportedMethod(method)) {
            if (hasText) {
                code.push(1, ".method(\"%s\", body)", method);
            } else {
                code.push(1, ".method(\"%s\", null)", method);
            }
        } else if (supportsBody(method)) {
            if (hasText) {
                code.push(1, ".%s(body)", harRequest.getMethod().toLowerCase());
            } else {
                code.push(1, ".%s(null)", harRequest.getMethod().toLowerCase());
            }
        } else {
            code.push(1, ".%s()", harRequest.getMethod().toLowerCase());
        }

        List<HarHeader> headers = harRequest.getHeaders();

        // construct headers
        if (ObjectUtils.isNotEmpty(headers)) {
            headers.forEach(
                    harHeader -> {
                        code.push(1, ".addHeader(\"%s\", \"%s\")", harHeader.getName(), harHeader.getValue());
                    });
        }

        List<HarCookie> cookies = harRequest.getCookies();

        // construct cookies
        if (ObjectUtils.isNotEmpty(headers)) {
            code.push(1, ".addHeader(\"%s\", \"%s\")", HttpHeaders.COOKIE, asCookies(cookies));
        }

        code.push(1, ".build();")
                .blank()
                .push("Response response = client.newCall(request).execute();");

        return code.join();
    }
}
