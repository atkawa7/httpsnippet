package io.github.atkawa7.httpsnippet.generators.python;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.generators.python.helpers.PythonHelper;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

import java.util.Map;

public class PythonRequests extends CodeGenerator implements PythonHelper {
    public PythonRequests() {
        super(Client.PYTHON_REQUESTS, Language.PYTHON);
    }

    @Override
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        // Start code
        CodeBuilder code = new CodeBuilder();

        // Import requests
        code.push("import requests").blank();

        // Set URL
        code.push("url = \"%s\"", codeRequest.getUrl()).blank();

        if (codeRequest.hasQueryStrings()) {
            code.push("querystring = %s", toJson(codeRequest.queryStringsAsMap())).blank();
        }

        this.pushPayLoad(code, codeRequest);

        Map<String, String> allHeaders = codeRequest.allHeadersAsMap();

        this.pushHeaders(code, allHeaders);

        String method = codeRequest.getMethod();
        String request = String.format("response = requests.request(\"%s\", url", method);

        if (codeRequest.hasBody()) {
            request += ", data=payload";
        }

        if (allHeaders.size() > 0) {
            request += ", headers=headers";
        }

        if (codeRequest.hasQueryStrings()) {
            request += ", params=querystring";
        }

        request += ")";

        code.push(request).blank().push("print(response.text)");

        return code.join();
    }
}
