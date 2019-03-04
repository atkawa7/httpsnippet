package io.github.atkawa7.httpsnippet;

import com.github.javafaker.Faker;
import com.smartbear.har.builder.HarPostDataBuilder;
import com.smartbear.har.builder.HarRequestBuilder;
import com.smartbear.har.model.HarHeader;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarQueryString;
import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.generators.java.OkHttp;
import io.github.atkawa7.httpsnippet.http.HttpMethod;
import io.github.atkawa7.httpsnippet.http.HttpVersion;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        List<HarHeader> headers = new ArrayList<>();
        List<HarQueryString> queryStrings = new ArrayList<>();

        User user = new User();
        Faker faker = new Faker();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());

        HarPostData harPostData =
                new HarPostDataBuilder()
                        .withMimeType(MediaType.APPLICATION_JSON)
                        .withText(ObjectUtils.writeValueAsString(user))
                        .build();

        HarRequest harRequest =
                new HarRequestBuilder()
                        .withMethod(HttpMethod.GET.toString())
                        .withUrl("http://localhost:5000/users")
                        .withHeaders(headers)
                        .withQueryString(queryStrings)
                        .withHttpVersion(HttpVersion.HTTP_1_1.toString())
                        .withPostData(harPostData)
                        .build();

        // Using default client
        HttpSnippet httpSnippet = new HttpSnippetCodeGenerator().snippet(harRequest, Language.JAVA);
        System.out.println(httpSnippet.getCode());

        // Or directly using
        String code = new OkHttp().code(harRequest);
        System.out.println(code);
    }

    @Data
    static class User {
        private String firstName;
        private String lastName;
    }
}
