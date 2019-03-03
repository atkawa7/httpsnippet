package io.github.atkawa7.httpsnippet.target.csharp;

import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.target.Target;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import com.smartbear.har.model.HarCookie;
import com.smartbear.har.model.HarHeader;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RestSharp extends Target {
    private final List<String> SUPPORTED_METHODS =
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

    public RestSharp() {
        super(Client.RESTSHARP, Language.CSHARP);
    }

    public boolean isNotSupported(String method) {
        return SUPPORTED_METHODS.indexOf(method.toUpperCase()) == -1;
    }

    @Override
    public String code(@NonNull final HarRequest harRequest) throws Exception {
        if (isNotSupported(harRequest.getMethod())) {
            throw new Exception("Method not supported");
        }

        CodeBuilder code = new CodeBuilder();
        code.push("var client = new RestClient(\"%s\");", harRequest.getUrl());
        code.push("var request = new RestRequest(Method.%s);", harRequest.getMethod().toUpperCase());

        List<HarHeader> headers = harRequest.getHeaders();

        // construct headers
        if (ObjectUtils.isNotEmpty(headers)) {
            headers.forEach(
                    harHeader -> {
                        code.push(
                                "request.AddHeader(\"%s\", \"%s\");", harHeader.getName(), harHeader.getValue());
                    });
        }

        List<HarCookie> cookies = harRequest.getCookies();
        // construct cookies
        if (ObjectUtils.isNotEmpty(cookies)) {
            cookies.forEach(
                    cookie -> {
                        code.push("request.AddCookie(\"%s\", \"%s\");", cookie.getName(), cookie.getValue());
                    });
        }

        HarPostData postData = harRequest.getPostData();

        if (ObjectUtils.isNotNull(postData)) {
            String text = postData.getText();
            if (StringUtils.isNotEmpty(text) && ObjectUtils.isNotEmpty(headers)) {
                Optional<HarHeader> optionalHarHeader = find(headers, HttpHeaders.CONTENT_TYPE);
                if (optionalHarHeader.isPresent()) {
                    HarHeader harHeader = optionalHarHeader.get();
                    code.push(
                            "request.AddParameter(\"%s\", %s, ParameterType.RequestBody);",
                            harHeader.getValue(), toJson(text));
                }
            }
        }

        code.push("IRestResponse response = client.Execute(request);");
        return code.join();
    }
}
