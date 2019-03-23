package io.github.atkawa7.httpsnippet.generators.c;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;
import org.apache.commons.lang3.StringUtils;

public class LibCurl extends CodeGenerator {

    public LibCurl() {
        super(Client.LIBCURL, Language.C);
    }

    @Override
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        CodeBuilder code = new CodeBuilder();

        code.push("CURL *hnd = curl_easy_init();").blank();
        if (!codeRequest.getMimeType().equalsIgnoreCase(MediaType.MULTIPART_FORM_DATA)) {
            code.push(
                    "curl_easy_setopt(hnd, CURLOPT_CUSTOMREQUEST, \"%s\");",
                    codeRequest.getMethod().toUpperCase());
        }

        code.push("curl_easy_setopt(hnd, CURLOPT_URL, \"%s\");", codeRequest.getFullUrl());

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

        if (codeRequest.hasBody()) {
            switch (codeRequest.getMimeType()) {
                case MediaType.APPLICATION_JSON:
                    if (codeRequest.hasText()) {
                        code.blank()
                                .push("curl_easy_setopt(hnd, CURLOPT_POSTFIELDS, %s);", codeRequest.toJsonString());
                    }
                    break;
                case MediaType.APPLICATION_FORM_URLENCODED:
                    if (codeRequest.hasParams()) {
                        code.blank()
                                .push(
                                        "curl_easy_setopt(hnd, CURLOPT_POSTFIELDS, %s);",
                                        toJson(codeRequest.paramsToString()));
                    }
                    break;
                case MediaType.MULTIPART_FORM_DATA:
                    if (codeRequest.hasParams()) {
                        code.blank()
                                .push("struct curl_httppost* post = NULL;")
                                .push("struct curl_httppost* last = NULL;")
                                .blank();
                        codeRequest
                                .getParams()
                                .forEach(
                                        p -> {
                                            if (StringUtils.isNotBlank(p.getFileName())) {
                                                code.push(
                                                        "curl_formadd(&post, &last, CURLFORM_COPYNAME, \"%s\", CURLFORM_FILE, \"%s\", CURLFORM_END)",
                                                        p.getName(), p.getFileName());
                                            } else if (StringUtils.isNotBlank(p.getContentType())) {
                                                code.push(
                                                        "curl_formadd(&post, &last, CURLFORM_COPYNAME, \"%s\", CURLFORM_COPYCONTENTS, \"%s\", CURLFORM_CONTENTTYPE, \"%s\", CURLFORM_END);",
                                                        p.getName(), p.getValue(), p.getValue());

                                            } else {
                                                code.push(
                                                        "curl_formadd(&post, &last, CURLFORM_COPYNAME, \"%s\", CURLFORM_COPYCONTENTS, \"%s\", CURLFORM_END);",
                                                        p.getName(), p.getValue());
                                            }
                                        });
                        code.blank().push("curl_easy_setopt(hnd, CURLOPT_HTTPPOST, post)");
                    }

                    break;
                default:
                    if (codeRequest.hasText()) {
                        code.blank()
                                .push("curl_easy_setopt(hnd, CURLOPT_POSTFIELDS, \"%s\");", codeRequest.getText());
                    }
            }
        }

        code.blank().push("CURLcode ret = curl_easy_perform(hnd);").blank();

        return code.join();
    }
}
