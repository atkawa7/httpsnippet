package io.github.atkawa7.httpsnippet.generators.python.helpers;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PythonHelper {
default void pushHeaders(CodeBuilder code, Map<String, String> allHeaders) {
	if (allHeaders.size() > 0) {
	code.push("headers = {");
	List<String> headers = new ArrayList<>();
	for (Map.Entry<String, String> header : allHeaders.entrySet()) {
		headers.add(String.format("\"%s\": \"%s\"", header.getKey(), header.getValue()));
	}
	code.push(1, String.join(",", headers));
	code.push("}").blank();
	}
}

default void pushPayLoad(CodeBuilder code, CodeRequest codeRequest) throws Exception {
	if (codeRequest.hasBody()) {
	if (codeRequest.hasText()) {
		code.push("payload = %s", codeRequest.toJsonString()).blank();
	} else {
		code.push("payload = %s", codeRequest.paramsToJSONString()).blank();
	}
	}
}
}
