package io.github.atkawa7.httpsnippet.demo.console;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import io.github.atkawa7.har.HarHeader;
import io.github.atkawa7.har.HarPostData;
import io.github.atkawa7.har.HarQueryString;
import io.github.atkawa7.har.HarRequest;
import lombok.Data;

import org.reflections.Reflections;

import com.github.javafaker.Faker;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.generators.HttpSnippetCodeGenerator;
import io.github.atkawa7.httpsnippet.generators.java.OkHttp;
import io.github.atkawa7.httpsnippet.http.HttpMethod;
import io.github.atkawa7.httpsnippet.http.HttpVersion;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.HttpSnippet;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.utils.HarUtils;

public class ConsoleApp {

  public static List<HarQueryString> queryString(String queryString) throws UnsupportedEncodingException {
    final String[] parameters = Objects.requireNonNull(queryString).split("&");
    List<HarQueryString> queryStrings = new ArrayList<>();
    for (String parameter : parameters) {
      final int idx = parameter.indexOf("=");
      final String key = idx > 0 ? URLDecoder.decode(parameter.substring(0, idx), "UTF-8") : parameter;
      final String value = idx > 0 && parameter.length() > idx + 1 ? URLDecoder.decode(parameter.substring(idx + 1), "UTF-8") : "";
      if (!key.isEmpty()) {
        queryStrings.add(new HarQueryString(key, value, ""));
      }
    }
    return queryStrings;
  }


  public static void main(String[] args) throws Exception {
    List<HarHeader> headers = new ArrayList<>();
    List<HarQueryString> queryStrings = new ArrayList<>();

    User user = new User();
    Faker faker = new Faker();
    user.setFirstName(faker.name().firstName());
    user.setLastName(faker.name().lastName());

    HarPostData harPostData =
        new HarPostData()
            .withMimeType(MediaType.APPLICATION_JSON)
            .withText(HarUtils.toJsonString(user));

    HarRequest harRequest =
        new HarRequest()
            .withMethod(HttpMethod.GET.toString())
            .withUrl("http://localhost:5000/users")
            .withHeaders(headers)
            .withQueryString(queryStrings)
            .withHttpVersion(HttpVersion.HTTP_1_1.toString())
            .withPostData(harPostData);

    // Using default client
    HttpSnippet httpSnippet = new HttpSnippetCodeGenerator().snippet(harRequest, Language.JAVA);
    System.out.println(httpSnippet.getCode());

    // Or directly using
    String code = new OkHttp().code(harRequest);
    System.out.println(code);

    Reflections reflections = new Reflections("io.github.atkawa7");

    Set<Class<? extends CodeGenerator>> subTypes = reflections.getSubTypesOf(CodeGenerator.class);
    List<String> values = new ArrayList<>();
    int value = 0;
    for (Class<? extends CodeGenerator> subType : subTypes) {
      ++value;
      values.add(String.format(format(value), subType.getSimpleName()));
    }
    Collections.sort(
        values,
        (o1, o2) -> {
          String[] o1Tokens = o1.split(" ");
          String[] o2Tokens = o2.split(" ");
          return o1Tokens[1].compareTo(o2Tokens[1]);
        });
    System.out.print(String.join("\n", values));
  }

  private static String format(int value) {
    switch (value % 4) {
      case 1:
        return "CodeGenerator <|--- %s";
      case 2:
        return "%s ---|> CodeGenerator";
      case 3:
        return "%s ---|> CodeGenerator ";
      default:
        return "CodeGenerator <|--- %s";
    }
  }

  @Data
  static class User {
    private String firstName;
    private String lastName;
  }
}
