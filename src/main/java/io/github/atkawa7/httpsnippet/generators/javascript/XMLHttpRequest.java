package io.github.atkawa7.httpsnippet.generators.javascript;

import com.smartbear.har.model.*;
import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class XMLHttpRequest extends CodeGenerator {
    private boolean cors;

    public XMLHttpRequest() {
        super(Client.XHR, Language.JAVASCRIPT);
        this.cors = true;
    }

    @Override
    protected String generateCode(final HarRequest harRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

        HarPostData postData = harRequest.getPostData();

        if (hasText(postData)) {
            String mimeType = this.getMimeType(postData);
            switch (mimeType) {
                case MediaType.APPLICATION_JSON: {
                    code.push("var data = JSON.stringify(%s);", postData.getText()).push(CodeBuilder.EMPTY);
                }
                break;

                case MediaType.MULTIPART_FORM_DATA: {
                    code.push("var data = new FormData();");
                    List<HarParam> params = postData.getParams();
                    if (ObjectUtils.isNotEmpty(params)) {
                        for (HarParam harParam : params) {
                            String value =
                                    StringUtils.firstNonEmpty(
                                            harParam.getValue(), harParam.getFileName(), CodeBuilder.SPACE);
                            code.push("data.append(%s, %s);", toJson(value), toJson(value));
                        }
                    }
                    code.blank();
                }
                break;

                default: {
                    code.push("var data = %s;", toJson(postData.getText())).blank();
                }
            }
        }

        code.push("var xhr = new XMLHttpRequest();");

        if (cors) {
            code.push("xhr.withCredentials = true;");
        }

        code.blank()
                .push("xhr.addEventListener('readystatechange', function () {")
                .push(1, "if (this.readyState === this.DONE) {")
                .push(2, "console.log(this.responseText);")
                .push(1, "}")
                .push("});")
                .blank()
                .push("xhr.open(%s, %s);", toJson(harRequest.getMethod()), toJson(harRequest.getUrl()));

        List<HarHeader> headers = harRequest.getHeaders();

        if (ObjectUtils.isNotEmpty(headers)) {
            for (HarHeader harHeader : headers) {
                code.push(
                        "xhr.setRequestHeader(%s, %s);",
                        toJson(harHeader.getName()), toJson(harHeader.getValue()));
            }
        }

        List<HarCookie> cookies = harRequest.getCookies();

        if (ObjectUtils.isNotEmpty(cookies)) {
            code.push("xhr.setRequestHeader(\"%s\", %s);", HttpHeaders.COOKIE, asCookies(cookies));
        }

        code.blank().push("xhr.send(data);");

        return code.join();
    }
}
