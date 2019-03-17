package io.github.atkawa7.httpsnippet.generators.csharp;

import com.smartbear.har.model.HarHeader;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RestSharp extends CodeGenerator {
    private final List<String> SUPPORTED_METHODS =
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

    public RestSharp() {
        super(Client.RESTSHARP, Language.CSHARP);
    }

    public boolean isNotSupported(String method) {
        return SUPPORTED_METHODS.indexOf(method.toUpperCase()) == -1;
    }

    @Override
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        if (isNotSupported(codeRequest.getMethod())) {
            throw new Exception("Method not supported");
        }

        CodeBuilder code = new CodeBuilder();
        code.push("var client = new RestClient(\"%s\");", codeRequest.getUrl());
        code.push("var request = new RestRequest(Method.%s);", codeRequest.getMethod().toUpperCase());

        if (codeRequest.hasHeaders()) {
            codeRequest
                    .getHeaders()
                    .forEach(
                            harHeader ->
                                    code.push(
                                            "request.AddHeader(\"%s\", \"%s\");",
                                            harHeader.getName(), harHeader.getValue()));
        }

        if (codeRequest.hasCookies()) {
            codeRequest
                    .getCookies()
                    .forEach(
                            cookie ->
                                    code.push(
                                            "request.AddCookie(\"%s\", \"%s\");", cookie.getName(), cookie.getValue()));
        }

        if (codeRequest.hasText() && codeRequest.hasHeaders()) {
            Optional<HarHeader> optionalHarHeader = codeRequest.find(HttpHeaders.CONTENT_TYPE);
            if (optionalHarHeader.isPresent()) {
                HarHeader harHeader = optionalHarHeader.get();
                code.push(
                        "request.AddParameter(\"%s\", %s, ParameterType.RequestBody);",
                        harHeader.getValue(), codeRequest.toJsonString());
            }
        }

        code.push("IRestResponse response = client.Execute(request);");
        return code.join();
    }
}
