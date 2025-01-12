package io.github.atkawa7.httpsnippet.generators.python;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class PythonRequests extends CodeGenerator {
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
      code.push("querystring = %s", codeRequest.queryStringsToJsonString()).blank();
    }

    if (codeRequest.hasBody()) {
      if (codeRequest.hasText()) {
        code.push("payload = %s", codeRequest.toJsonString());
      } else {
        code.push("payload = \"%s\"", codeRequest.paramsToString());
      }
    }

    if (codeRequest.hasHeadersAndCookies()) {
      code.push("headers = %s", codeRequest.allHeadersToJsonString(false));
      code.blank();
    }

    String method = codeRequest.getMethod();
    String request = String.format("response = requests.request(\"%s\", url", method);

    if (codeRequest.hasBody()) {
      request += ", data=payload";
    }

    if (codeRequest.hasHeadersAndCookies()) {
      request += ", headers=headers";
    }

    if (codeRequest.hasQueryStrings()) {
      request += ", params=querystring";
    }

    request += ")";

    code.push(request).blank().push("print(response.text)").blank();

    return code.join();
  }
}
