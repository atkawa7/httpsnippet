package io.github.atkawa7.httpsnippet.generators.shell;

import com.smartbear.har.model.HarParam;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.HttpVersion;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

public class Curl extends CodeGenerator {
    public Curl() {
        super(Client.CURL, Language.SHELL);
    }

    public String quote(String value) {
        return String.format("'%s'", value.replace("'", "\"'\""));
    }

    @Override
    protected String generateCode(final CodeRequest codeRequest) throws Exception {
        String indent = "  ";
        boolean _short = false, _binary = false;

        CodeBuilder code = new CodeBuilder(indent, "\\\n" + indent);

        code.push("curl %s %s", _short ? "-X" : "--request", codeRequest.getMethod())
                .push(String.format("%s%s", _short ? "" : "--url ", quote(codeRequest.getUrl())));

        if (HttpVersion.HTTP_1_0.equalsIgnoreCase(codeRequest.getHttpVersion())) {
            code.push(_short ? "-0" : "--http1.0");
        }

        if (codeRequest.hasHeaders()) {
            codeRequest
                    .getHeaders()
                    .forEach(
                            harHeader -> {
                                String header = String.format("%s: %s", harHeader.getName(), harHeader.getValue());
                                code.push("%s %s", _short ? "-H" : "--header", quote(header));
                            });
        }

        if (codeRequest.hasCookies()) {
            code.push("%s %s", _short ? "-b" : "--cookie", quote(codeRequest.getCookieString()));
        }

        if (codeRequest.hasBody()) {
            switch (codeRequest.getMimeType()) {
                case MediaType.MULTIPART_FORM_DATA: {
                    if (codeRequest.hasParams()) {
                        for (HarParam param : codeRequest.getParams()) {
                            String post = StringUtils.isNotEmpty(param.getFileName()) ?
                                    String.format("%s=@%s", param.getName(), param.getFileName()) :
                                    String.format("%s=%s", param.getName(), param.getValue());
                            code.push("%s %s", _short ? "-F" : "--form", quote(toJson(post)));
                        }
                    }
                }

                break;

                case MediaType.APPLICATION_FORM_URLENCODED: {
                    if (codeRequest.hasParams()) {
                        for (HarParam param : codeRequest.getParams()) {
                            code.push(
                                    "%s %s",
                                    _binary ? "--data-binary" : (_short ? "-d" : "--data"),
                                    quote(String.format("%s=%s", param.getName(), param.getValue())));
                        }
                    } else {
                        code.push(
                                "%s %s",
                                _binary ? "--data-binary" : (_short ? "-d" : "--data"),
                                StringEscapeUtils.escapeXSI(quote(codeRequest.getText())));
                    }
                }

                break;

                default: {
                    code.push(
                            "%s %s",
                            _binary ? "--data-binary" : (_short ? "-d" : "--data"), quote(codeRequest.getText()));
                }
            }
        }

        return code.join();
    }
}
