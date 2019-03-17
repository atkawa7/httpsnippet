package io.github.atkawa7.httpsnippet.generators.c;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class LibCurl extends CodeGenerator {

    public LibCurl() {
        super(Client.LIBCURL, Language.C);
    }

    @Override
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        CodeBuilder code = new CodeBuilder();

        code.push("CURL *hnd = curl_easy_init();")
                .blank()
                .push(
                        "curl_easy_setopt(hnd, CURLOPT_CUSTOMREQUEST, \"%s\");",
                        codeRequest.getMethod().toUpperCase())
                .push("curl_easy_setopt(hnd, CURLOPT_URL, \"%s\");", codeRequest.getUrl());

        if (codeRequest.hasHeaders()) {
            code.blank().push("struct curl_slist *headers = NULL;");

            codeRequest
                    .getHeaders()
                    .forEach(
                            harHeader -> {
                                code.push(
                                        "headers = curl_slist_append(headers, \"%s: %s\");",
                                        harHeader.getName(), harHeader.getValue());
                            });

            code.push("curl_easy_setopt(hnd, CURLOPT_HTTPHEADER, headers);");
        }

        if (codeRequest.hasCookies()) {
            code.blank()
                    .push("curl_easy_setopt(hnd, CURLOPT_COOKIE, \"%s\");", codeRequest.getCookieString());
        }

        if (codeRequest.hasText()) {
            code.blank()
                    .push("curl_easy_setopt(hnd, CURLOPT_POSTFIELDS, %s);", codeRequest.toJsonString());
        }

        code.blank().push("CURLcode ret = curl_easy_perform(hnd);");

        return code.join();
    }
}
