package io.github.atkawa7.httpsnippet.generators.java;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class AsyncHttp extends CodeGenerator {
    public AsyncHttp() {
        super(Client.ASYNC_HTTP, Language.JAVA);
    }

    @Override
    protected String generateCode(CodeRequest codeRequest) throws Exception {
        final CodeBuilder code = new CodeBuilder("  ");
        code.push("AsyncHttpClient client = new DefaultAsyncHttpClient();");

        code.push("client.prepare(\"%s\", \"%s\")",codeRequest.getMethod().toUpperCase(),  codeRequest.getFullUrl());

        // Add headers, including the cookies

        if( codeRequest.hasHeadersAndCookies()){
            codeRequest
                    .allHeadersAsMap()
                    .forEach((k, v) -> code.push(1, ".setHeader(\"%s\", \"%s\")", k, v));
        }
        if (codeRequest.hasBody()) {
            code.push(1, String.format(".setBody(%s)`", codeRequest.toJsonString()));
        }

        code.push(1, ".execute()");
        code.push(1, ".toCompletableFuture()");
        code.push(1, ".thenAccept(System.out::println)");
        code.push(1, ".join();");
        code.blank();
        code.push("client.close();");

        return code.join();
    }
}
