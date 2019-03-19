package io.github.atkawa7.httpsnippet.generators.javascript;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class Fetch extends CodeGenerator {
protected Fetch() {
	super(Client.FETCH, Language.JAVASCRIPT);
}

@Override
protected String generateCode(CodeRequest codeRequest) throws Exception {
	CodeBuilder code = new CodeBuilder();

	String url = codeRequest.getUrl();
	Map<String, Object> fetchOptions = new HashMap<>();
	fetchOptions.put("mode", "cors");
	fetchOptions.put("method", codeRequest.getMethod());
	fetchOptions.put("headers", codeRequest.allHeadersAsMap());

	switch (codeRequest.getMimeType()) {
	case MediaType.APPLICATION_FORM_URLENCODED:
		if (codeRequest.hasParams()) {
		code.push("var details =%s;", codeRequest.paramsToJSONString());
		code.push("var form = [];");
		code.push("for (var property in details) {");
		code.push(1, "var encodedKey = encodeURIComponent(property);");
		code.push(1, "var encodedValue = encodeURIComponent(details[property]);");
		code.push(1, "form.push(encodedKey + \"=\" + encodedValue);");
		code.push("}");
		code.push("form = formBody.join(\"&\");");
		fetchOptions.put("body", "[form]");
		}
		break;

	case MediaType.APPLICATION_JSON:
		if (codeRequest.hasText()) {
		fetchOptions.put("body", codeRequest.getText());
		}
		break;

	case MediaType.MULTIPART_FORM_DATA:
		if (codeRequest.hasParams()) {
		code.push("let form = new FormData();");
		codeRequest
			.getParams()
			.forEach(
				(param) -> {
					String value =
						StringUtils.isNotBlank(param.getFileName())
							? param.getFileName()
							: param.getValue();
					code.push("form.append(\"%s\", \"%s\");", param.getName(), value);
				});
		fetchOptions.put("body", "[form]");

		code.blank();
		}

		break;

	default:
		if (codeRequest.hasText()) {
		fetchOptions.put("body", codeRequest.getText());
		}
	}

	code.push("const fetchOptions = " + toJson(fetchOptions).replace("\"[form]\"", "form"))
		.blank()
		.push("fetch(\"" + url + "\", fetchOptions)")
		.push(1, ".then(response => response.json())")
		.push(1, ".then(data => console.log(data))")
		.push(1, ".catch(error => console.log(error));");

	return code.join();
}
}
