package io.github.atkawa7.httpsnippet.generators.java;

import com.smartbear.har.model.HarHeader;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public class Unirest extends CodeGenerator {
    private static final List<String> SUPPORTED_METHODS =
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");

    public Unirest() {
        super(Client.UNIREST, Language.JAVA);
    }

    public boolean isNotSupported(String method) {
        return SUPPORTED_METHODS.indexOf(method.toUpperCase()) == -1;
    }

    @Override
    protected String generateCode(final HarRequest harRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

        String method = harRequest.getMethod();

        if (isNotSupported(method)) {
            code.push(
                    "HttpResponse<String> response = Unirest.customMethod(\"%s\",\"%s\")",
                    method.toUpperCase(), harRequest.getUrl());
        } else {
            code.push(
                    "HttpResponse<String> response = Unirest.%s(\"%s\")",
                    method.toLowerCase(), harRequest.getUrl());
        }

        List<HarHeader> headers = harRequest.getHeaders();

        // construct headers
        if (ObjectUtils.isNotEmpty(headers)) {
            headers.forEach(
                    harHeader -> {
                        code.push(1, ".header(\"%s\", \"%s\")", harHeader.getName(), harHeader.getValue());
                    });
        }

        HarPostData postData = harRequest.getPostData();

        if (hasText(postData)) {
            code.push(1, ".body(%s)", toJson(postData.getText()));
        }

        code.push(1, ".asString();");

        return code.join();
    }
}
