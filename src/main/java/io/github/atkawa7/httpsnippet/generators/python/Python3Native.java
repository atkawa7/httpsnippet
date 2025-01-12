package io.github.atkawa7.httpsnippet.generators.python;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class Python3Native extends CodeGenerator {
  public Python3Native() {
    super(Client.PYTHON3, Language.PYTHON);
  }

  @Override
  protected String generateCode(final CodeRequest codeRequest) throws Exception {
    CodeBuilder code = new CodeBuilder();

    code.push("import http.client").blank();

    String connection = codeRequest.isSecure() ? "HTTPSConnection" : "HTTPConnection";
    if (codeRequest.isDefaultPort()) {
      code.push("conn = http.client.%s(\"%s\")", connection, codeRequest.getHost()).blank();
    } else {
      code.push(
              "conn = http.client.%s(\"%s\", \"%s\")",
              connection, codeRequest.getHost(), Integer.toString(codeRequest.getPort()))
          .blank();
    }

    if (codeRequest.hasBody()) {
      if (codeRequest.hasText()) {
        code.push("payload = %s", codeRequest.toJsonString()).blank();
      } else {
        code.push("payload = \"%s\"", codeRequest.paramsToString()).blank();
      }
    }
    if (codeRequest.hasHeadersAndCookies()) {
      code.push("headers = %s", codeRequest.allHeadersToJsonString()).blank();
    }
    String method = codeRequest.getMethod().toUpperCase();
    String path = codeRequest.getFullPath();

    if (codeRequest.hasBody() && codeRequest.hasHeadersAndCookies()) {
      code.push("conn.request(\"%s\", \"%s\", payload, headers)", method, path);
    } else if (codeRequest.hasBody()) {
      code.push("conn.request(\"%s\", \"%s\", payload)", method, path);
    } else if (codeRequest.hasHeadersAndCookies()) {
      code.push("conn.request(\"%s\", \"%s\", headers=headers)", method, path);
    } else {
      code.push("conn.request(\"%s\", \"%s\")", method, path);
    }

    // Get Response
    code.blank()
        .push("res = conn.getresponse()")
        .push("data = res.read()")
        .blank()
        .push("print(data.decode(\"utf-8\"))")
        .blank();

    return code.join();
  }
}
