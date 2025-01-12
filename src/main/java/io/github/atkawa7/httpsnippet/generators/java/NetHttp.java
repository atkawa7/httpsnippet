package io.github.atkawa7.httpsnippet.generators.java;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

public class NetHttp extends CodeGenerator {
  public NetHttp() {
    super(Client.NET_HTTP, Language.JAVA);
  }

  @Override
  protected String generateCode(CodeRequest codeRequest) throws Exception {
    final CodeBuilder builder = new CodeBuilder("  ");
    builder.push("HttpRequest request = HttpRequest.newBuilder()");
    builder.push(2, ".uri(URI.create(\"%s\"))", codeRequest.getFullUrl());

    if( codeRequest.hasHeadersAndCookies()){
      codeRequest
              .allHeadersAsMap()
              .forEach((k, v) -> builder.push(1, ".header(\"%s\", \"%s\")", k, v));
    }

    if (codeRequest.hasBody()) {
      builder.push(2, ".method(\"%s\", HttpRequest.BodyPublishers.ofString(%s))", codeRequest.getMethod().toUpperCase(),  codeRequest.toJsonString());
    } else {
      builder.push(2, ".method(\"%s\", HttpRequest.BodyPublishers.noBody())", codeRequest.getMethod().toUpperCase());
    }

    builder.push(2, ".build();");

    builder.push("HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());");
    builder.push("System.out.println(response.body());");

    return builder.join();
  }
}
