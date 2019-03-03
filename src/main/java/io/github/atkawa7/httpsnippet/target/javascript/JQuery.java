package io.github.atkawa7.httpsnippet.target.javascript;

import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.target.Target;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import com.smartbear.har.model.HarHeader;
import com.smartbear.har.model.HarParam;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class JQuery extends Target {

    private final Boolean async;
    private final Boolean crossDomain;

    public JQuery() {
        super(Client.JQUERY, Language.JAVASCRIPT);
        this.async = TRUE;
        this.crossDomain = TRUE;
    }

    @Override
    public String code(@NonNull final HarRequest harRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

        List<HarHeader> headers = harRequest.getHeaders();

        Map<String, Object> settings = new HashMap<>();
        settings.put("async", async);
        settings.put("crossDomain", crossDomain);
        settings.put("url", harRequest.getUrl());
        settings.put("method", harRequest.getMethod());
        settings.put("headers", asHeaders(headers));

        HarPostData postData = harRequest.getPostData();

        if (ObjectUtils.isNotNull(postData)) {
            List<HarParam> params = postData.getParams();
            switch (postData.getMimeType()) {
                case HttpHeaders.APPLICATION_FORM_URLENCODED: {
                    settings.put("body", hasParams(params) ? asParams(params) : postData.getText());
                }
                break;

                case HttpHeaders.APPLICATION_JSON: {
                    settings.put("processData", FALSE);
                    settings.put("data", postData.getText());
                }
                break;

                case HttpHeaders.MULTIPART_FORM_DATA: {
                    code.push("var form = new FormData();");

                    if (hasParams(params)) {
                        for (HarParam harParam : params) {
                            String value =
                                    StringUtils.firstNonEmpty(harParam.getValue(), harParam.getFileName(), CodeBuilder.SPACE);
                            code.push("form.append(%s, %s);", toJson(harParam.getName()), toJson(value));
                        }
                    }

                    settings.put("processData", FALSE);
                    settings.put("contentType", FALSE);
                    settings.put("mimeType", HttpHeaders.MULTIPART_FORM_DATA);
                    settings.put("data", "[form]");
                    code.blank();
                }
                break;

                default:
                    if (hasText(postData)) {
                        settings.put("data", postData.getText());
                    }
            }
        }

        code.push("var settings = " + toJson(settings).replace("\"[form]\"", "form"))
                .blank()
                .push("$.ajax(settings).done(function (response) {")
                .push(1, "console.log(response);")
                .push("});");

        return code.join();
    }
}
