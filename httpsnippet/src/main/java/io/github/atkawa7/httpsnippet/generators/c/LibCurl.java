package io.github.atkawa7.httpsnippet.generators.c;

import com.smartbear.har.model.HarCookie;
import com.smartbear.har.model.HarHeader;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class LibCurl extends CodeGenerator {

    public LibCurl() {
        super(Client.LIBCURL, Language.C);
    }

    @Override
    protected String generateCode(final HarRequest harRequest) throws Exception {
        CodeBuilder code = new CodeBuilder();

        code.push("CURL *hnd = curl_easy_init();")
                .blank()
                .push(
                        "curl_easy_setopt(hnd, CURLOPT_CUSTOMREQUEST, \"%s\");",
                        harRequest.getMethod().toUpperCase())
                .push("curl_easy_setopt(hnd, CURLOPT_URL, \"%s\");", harRequest.getUrl());

        List<HarHeader> headers = harRequest.getHeaders();

        if (ObjectUtils.isNotEmpty(headers)) {
            code.blank().push("struct curl_slist *headers = NULL;");

            headers.forEach(
                    harHeader -> {
                        code.push(
                                "headers = curl_slist_append(headers, \"%s: %s\");",
                                harHeader.getName(), harHeader.getValue());
                    });

            code.push("curl_easy_setopt(hnd, CURLOPT_HTTPHEADER, headers);");
        }

        List<HarCookie> cookies = harRequest.getCookies();
        if (ObjectUtils.isNotEmpty(cookies)) {
            code.blank().push("curl_easy_setopt(hnd, CURLOPT_COOKIE, \"%s\");", asCookies(cookies));
        }

        HarPostData postData = harRequest.getPostData();

        if (ObjectUtils.isNotNull(postData) && StringUtils.isNotEmpty(postData.getText())) {
            code.blank()
                    .push("curl_easy_setopt(hnd, CURLOPT_POSTFIELDS, %s);", toJson(postData.getText()));
        }

        code.blank().push("CURLcode ret = curl_easy_perform(hnd);");

        return code.join();
    }
}
