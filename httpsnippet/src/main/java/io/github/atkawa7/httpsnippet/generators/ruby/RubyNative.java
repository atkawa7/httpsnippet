package io.github.atkawa7.httpsnippet.generators.ruby;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RubyNative extends CodeGenerator {
    public RubyNative() {
        super(Client.RUBY, Language.RUBY);
    }

    @Override
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        CodeBuilder code = new CodeBuilder();

        code.push("require 'uri'").push("require 'net/http'");

        if (codeRequest.isSecure()) {
            code.push("require 'openssl'");
        }

        code.blank();

        // To support custom methods we check for the supported methods
        // and if doesn't exist then we build a custom class for it
        String method = codeRequest.getMethod().toUpperCase();

        List<String> methods =
                Arrays.asList(
                        "GET", "POST", "HEAD", "DELETE", "PATCH", "PUT", "OPTIONS", "COPY", "LOCK", "UNLOCK",
                        "MOVE", "TRACE");
        String capMethod = method.charAt(0) + method.substring(1).toLowerCase();

        if (methods.indexOf(method) < 0) {
            code.push("class Net::HTTP::%s < Net::HTTPRequest", capMethod)
                    .push("  METHOD = '%s'", method.toUpperCase())
                    .push("  REQUEST_HAS_BODY = \'%s\'", codeRequest.hasText() ? "true" : "false")
                    .push("  RESPONSE_HAS_BODY = true")
                    .push("end")
                    .blank();
        }

        code.push("url = URI(\"%s\")", codeRequest.getFullUrl())
                .blank()
                .push("http = Net::HTTP.new(url.host, url.port)");

        if (codeRequest.isSecure()) {
            code.push("http.use_ssl = true").push("http.verify_mode = OpenSSL::SSL::VERIFY_NONE");
        }

        code.blank().push("request = Net::HTTP::%s.new(url)", capMethod);

        Map<String, String> headers = codeRequest.allHeadersAsMap();

        if (headers.size() > 0) {
            headers.forEach((k, v) -> code.push("request[\"%s\"] = \"%s\"", k, v));
        }

        if (codeRequest.hasBody()) {
            if (codeRequest.hasText()) {
                code.push("request.body = %s", codeRequest.toJsonString());
            }
            if (codeRequest.hasParams()) {
                code.push("request.body = \"%s\"", codeRequest.paramsToString());
            }
        }

        code.blank().push("response = http.request(request)").push("puts response.read_body").blank();

        return code.join();
    }
}
