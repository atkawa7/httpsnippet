package io.github.atkawa7.httpsnippet.target.ruby;

import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.target.Target;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import lombok.NonNull;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RubyNative extends Target {
    public RubyNative() {
        super(Client.RUBY, Language.RUBY);
    }

    @Override
    public String code(@NonNull final HarRequest harRequest) throws Exception {
        CodeBuilder code = new CodeBuilder();

        code.push("require 'uri'").push("require 'net/http'").blank();

        // To support custom methods we check for the supported methods
        // and if doesn't exist then we build a custom class for it
        String method = harRequest.getMethod().toUpperCase();

        List<String> methods =
                Arrays.asList(
                        "GET", "POST", "HEAD", "DELETE", "PATCH", "PUT", "OPTIONS", "COPY", "LOCK", "UNLOCK",
                        "MOVE", "TRACE");
        String capMethod = method.charAt(0) + method.substring(1).toLowerCase();
        HarPostData postData = harRequest.getPostData();
        if (methods.indexOf(method) < 0) {
            code.push("class Net::HTTP::%s < Net::HTTPRequest", capMethod)
                    .push("  METHOD = '%s'", method.toUpperCase())
                    .push("  REQUEST_HAS_BODY = \'%s\'", hasText(postData) ? "true" : "false")
                    .push("  RESPONSE_HAS_BODY = true")
                    .push("end")
                    .blank();
        }

        code.push("url = URI(\"%s\")", harRequest.getUrl())
                .blank()
                .push("http = Net::HTTP.new(url.host, url.port)");

        URL url = new URL(harRequest.getUrl());
        if (CodeBuilder.HTTPS.equalsIgnoreCase(url.getProtocol())) {
            code.push("http.use_ssl = true").push("http.verify_mode = OpenSSL::SSL::VERIFY_NONE");
        }

        code.blank().push("request = Net::HTTP::%s.new(url)", capMethod);

        Map<String, String> headers = asHeaders(harRequest);

        if (headers.size() > 0) {
            headers.forEach((k, v) -> code.push("request[\"%s\"] = \"%s\"", k, v));
        }

        if (hasText(postData)) {
            code.push("request.body = %s", toJson(postData.getText()));
        }

        code.blank().push("response = http.request(request)").push("puts response.read_body");

        return code.join();
    }
}
