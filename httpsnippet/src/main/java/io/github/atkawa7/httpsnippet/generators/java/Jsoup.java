package io.github.atkawa7.httpsnippet.generators.java;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

import java.util.Arrays;
import java.util.List;

public class Jsoup extends CodeGenerator {
public Jsoup() {
	super(Client.JSOUP, Language.JAVA);
}

@Override
protected String generateCode(CodeRequest codeRequest) throws Exception {

	CodeBuilder code = new CodeBuilder();

	List<String> methods =
		Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE");

	code.push("String response = Jsoup.connect(\"%s\")", codeRequest.getFullUrl());

	String method = codeRequest.getMethod().toUpperCase();

	if (methods.indexOf(method) == -1) {
	code.push(1, ".method(Method.valueOf(\"%s\"))", method);
	} else {
	code.push(1, ".method(Method.%s)", method);
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
	code.push(1, ".requestBody(%s)", codeRequest.toJsonString());
	} else {
	code.push(1, ".execute().body();");
	}

	return code.join();
}
}
