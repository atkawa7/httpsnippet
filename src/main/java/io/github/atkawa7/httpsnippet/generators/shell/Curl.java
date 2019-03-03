package io.github.atkawa7.httpsnippet.generators.shell;

import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.http.HttpVersion;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import com.smartbear.har.model.*;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

public class Curl extends CodeGenerator {
    public Curl() {
        super(Client.CURL, Language.SHELL);
    }

    public String quote(String value) {
        return String.format("'%s'", value.replace("'", "'\\''"));
    }

    @Override
    public String code(@NonNull final HarRequest harRequest) throws Exception {
        String indent = "  ";
        boolean _short = false, _binary = false;

        CodeBuilder code = new CodeBuilder(indent, "\\\n" + indent);

        code.push("curl %s %s", _short ? "-X" : "--request", harRequest.getMethod())
                .push(String.format("%s%s", _short ? "" : "--url ", quote(harRequest.getUrl())));

        if (HttpVersion.HTTP_1_0.equalsIgnoreCase(harRequest.getHttpVersion())) {
            code.push(_short ? "-0" : "--http1.0");
        }

        // construct headers

        List<HarHeader> headers = harRequest.getHeaders();
        if (ObjectUtils.isNotEmpty(headers)) {
            headers.forEach(
                    harHeader -> {
                        String header = String.format("%s: %s", harHeader.getName(), harHeader.getValue());
                        code.push("%s %s", _short ? "-H" : "--header", quote(header));
                    });
        }

        List<HarCookie> cookies = harRequest.getCookies();

        if (ObjectUtils.isNotEmpty(cookies)) {
            code.push("%s %s", _short ? "-b" : "--cookie", quote(asCookies(cookies)));
        }

        // construct post params

        HarPostData postData = harRequest.getPostData();
        if (hasText(postData)) {
            List<HarParam> params = postData.getParams();
            switch (postData.getMimeType()) {
                case HttpHeaders.MULTIPART_FORM_DATA:
                    if (ObjectUtils.isNotEmpty(params)) {
                        for (HarParam param : params) {
                            String post = String.format("%s=%s", param.getName(), param.getValue());

                            if (StringUtils.isNotEmpty(param.getFileName())
                                    && StringUtils.isEmpty(param.getValue())) {
                                post = String.format("%s=@%s", param.getName(), param.getFileName());
                            }

                            code.push("%s %s", _short ? "-F" : "--form", quote(post));
                        }
                    }
                    break;

                case HttpHeaders.APPLICATION_FORM_URLENCODED:
                    if (ObjectUtils.isNotEmpty(params)) {
                        for (HarParam param : params) {
                            code.push(
                                    "%s %s",
                                    _binary ? "--data-binary" : (_short ? "-d" : "--data"),
                                    quote(String.format("%s=%s", param.getName(), param.getValue())));
                        }
                    } else {
                        code.push(
                                "%s %s",
                                _binary ? "--data-binary" : (_short ? "-d" : "--data"),
                                StringEscapeUtils.escapeXSI(quote(postData.getText())));
                    }
                    break;

                default:
                    code.push(
                            "%s %s",
                            _binary ? "--data-binary" : (_short ? "-d" : "--data"),
                            StringEscapeUtils.escapeXSI(quote(postData.getText())));
            }
        }

        return code.join();
    }
}
