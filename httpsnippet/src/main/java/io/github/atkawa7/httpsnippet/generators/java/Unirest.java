package io.github.atkawa7.httpsnippet.generators.java;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

import java.util.Arrays;
import java.util.List;

public class Unirest extends CodeGenerator {
    private static final List<String> SUPPORTED_METHODS =
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

    public Unirest() {
        super(Client.UNIREST, Language.JAVA);
    }

    public boolean isNotSupported(String method) {
        return SUPPORTED_METHODS.indexOf(method.toUpperCase()) == -1;
    }

    @Override
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

        String method = codeRequest.getMethod();

        if (isNotSupported(method)) {
            code.push(
                    "HttpResponse<String> response = Unirest.customMethod(\"%s\",\"%s\")",
                    method.toUpperCase(), codeRequest.getUrl());
        } else {
            code.push(
                    "HttpResponse<String> response = Unirest.%s(\"%s\")",
                    method.toLowerCase(), codeRequest.getUrl());
        }

        if (codeRequest.hasHeadersAndCookies()) {
            codeRequest
                    .allHeadersAsMap()
                    .forEach(
                            (k, v) -> {
                                code.push(1, ".header(\"%s\", \"%s\")", k, v);
                            });
        }

        if (codeRequest.hasText()) {
            code.push(1, ".body(%s)", codeRequest.toJsonString());
        }

        code.push(1, ".asString();");

        return code.join();
    }
}
