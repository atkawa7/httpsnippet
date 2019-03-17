package io.github.atkawa7.httpsnippet.generators.python;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.generators.python.helpers.PythonHelper;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

import java.util.Map;

public class Python3Native extends CodeGenerator implements PythonHelper {
    public Python3Native() {
        super(Client.PYTHON3, Language.PYTHON);
    }

    @Override
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        CodeBuilder code = new CodeBuilder();

        code.push("import http.client").blank();

        String connection = codeRequest.isSecure() ? "HTTPSConnection" : "HTTPConnection";
        code.push(
                "conn = http.client.%s(\"%s\", \"%s\")",
                connection, codeRequest.getHost(), Integer.toString(codeRequest.getPort()))
                .blank();

        this.pushPayLoad(code, codeRequest);
        Map<String, String> allHeaders = codeRequest.allHeadersAsMap();
        this.pushHeaders(code, allHeaders);

        String method = codeRequest.getMethod().toUpperCase();
        String path = codeRequest.getPath();

        if (codeRequest.hasBody() && allHeaders.size() > 0) {
            code.push("conn.request(\"%s\", \"%s\", payload, headers)", method, path);
        } else if (codeRequest.hasBody()) {
            code.push("conn.request(\"%s\", \"%s\", payload)", method, path);
        } else if (allHeaders.size() > 0) {
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
