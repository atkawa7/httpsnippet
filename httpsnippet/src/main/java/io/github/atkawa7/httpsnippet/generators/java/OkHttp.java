package io.github.atkawa7.httpsnippet.generators.java;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;
import org.apache.commons.lang3.StringUtils;

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
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

        code.push("OkHttpClient client = new OkHttpClient();").blank();

        if (codeRequest.hasBody()) {
            switch (codeRequest.getMimeType()) {
                case MediaType.APPLICATION_JSON:
                    if (codeRequest.hasText()) {
                        code.push("MediaType mediaType = MediaType.parse(\"%s\");", codeRequest.getMimeType());
                        code.push(
                                "RequestBody body = RequestBody.create(mediaType, %s);",
                                codeRequest.toJsonString());
                    }
                    break;
                case MediaType.APPLICATION_FORM_URLENCODED:
                    if (codeRequest.hasParams()) {
                        code.push("RequestBody body = new FormBody.Builder()");
                        codeRequest
                                .getParams()
                                .forEach(p -> code.push(4, ".add(\"%s\", \"%s\")", p.getName(), p.getValue()));
                        code.push(4, ".build();");
                    }
                    break;
                case MediaType.MULTIPART_FORM_DATA:
                    if (codeRequest.hasParams()) {
                        code.push("RequestBody body = new MultipartBody.Builder()");
                        code.push(4, ".setType(MultipartBody.FORM)");

                        codeRequest
                                .getParams()
                                .forEach(
                                        p -> {
                                            if (StringUtils.isNotBlank(p.getFileName())) {
                                                code.push(
                                                        4, ".addFormDataPart(\"%s\", \"%s\",", p.getName(), p.getFileName());
                                                code.push(
                                                        8,
                                                        "RequestBody.create(MediaType.parse(\"%s\"), new File(\"%s\")))",
                                                        p.getContentType(),
                                                        p.getFileName());
                                            } else {
                                                code.push(
                                                        4, " .addFormDataPart(\"%s\", \"%s\")", p.getName(), p.getValue());
                                            }
                                        });
                        code.push(4, ".build()");
                    }
                    break;
                default: {
                    if (codeRequest.hasText()) {
                        code.push(
                                "MediaType mediaType = MediaType.parse(\"%s\");", codeRequest.getMimeType());
                        code.push(
                                "RequestBody body = RequestBody.create(mediaType, \"%s\");",
                                codeRequest.getText());
                    }
                }
            }
        }

        code.push("Request request = new Request.Builder()");
        code.push(1, ".url(\"%s\")", codeRequest.getFullUrl());

        String method = codeRequest.getMethod().toUpperCase();

        if (isNotSupportedMethod(method)) {
            if (codeRequest.hasBody()) {
                code.push(1, ".method(\"%s\", body)", method);
            } else {
                code.push(1, ".method(\"%s\", null)", method);
            }
        } else if (supportsBody(method)) {
            if (codeRequest.hasBody()) {
                code.push(1, ".%s(body)", codeRequest.getMethod().toLowerCase());
            } else {
                code.push(1, ".%s(null)", codeRequest.getMethod().toLowerCase());
            }
        } else {
            code.push(1, ".%s()", codeRequest.getMethod().toLowerCase());
        }

        if (codeRequest.hasHeadersAndCookies()) {
            codeRequest
                    .allHeadersAsMap()
                    .forEach(
                            (k, v) -> {
                                code.push(1, ".addHeader(\"%s\", \"%s\")", k, v);
                            });
        }

        code.push(1, ".build();")
                .blank()
                .push("Response response = client.newCall(request).execute();")
                .blank();

        return code.join();
    }
}
