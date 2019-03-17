package io.github.atkawa7.httpsnippet.generators.powershell;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PowerShell extends CodeGenerator {
public PowerShell() {
	super(Client.WEBREQUEST, Language.POWERSHELL);
}

@Override
protected String generateCode(CodeRequest codeRequest) throws Exception {
	CodeBuilder code = new CodeBuilder();
	List<String> methods =
		Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

	if (methods.indexOf(codeRequest.getMethod()) == -1) {
	throw new Exception("Method not supported");
	}

	List<String> commandOptions = new ArrayList<>();

	if (codeRequest.hasHeaders()) {
	code.push("$headers=@{}");
	codeRequest
		.getHeaders()
		.forEach(
			(h) -> {
				if (!"connection".equalsIgnoreCase(h.getName())) {
				code.push("$headers.Add(\"%s\", \"%s\")", h.getName(), h.getValue());
				}
			});
	commandOptions.add("-Headers $headers");
	}

	// construct cookies
	if (codeRequest.hasCookies()) {
	code.push("$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession");

	codeRequest
		.getCookies()
		.forEach(
			cookie -> {
				code.push("$cookie = New-Object System.Net.Cookie");
				code.push("$cookie.Name = '%s'", cookie.getName());
				code.push("$cookie.Value = '%s'", cookie.getValue());
				code.push("$cookie.Domain = '%s'", cookie.getDomain());
				code.push("$session.Cookies.Add($cookie)");
			});
	commandOptions.add("-WebSession $session");
	}

	if (codeRequest.hasText()) {
	commandOptions.add("-ContentType '" + codeRequest.getMimeType() + "'");
	commandOptions.add("-Body '" + codeRequest.getText() + "'");
	}

	code.push(
		"$response = Invoke-WebRequest -Uri '%s' -Method %s %s",
		codeRequest.getUrl(), codeRequest.getMethod(), String.join(" ", commandOptions));
	return code.join();
}
}
