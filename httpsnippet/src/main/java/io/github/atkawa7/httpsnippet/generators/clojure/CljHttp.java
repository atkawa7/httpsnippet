package io.github.atkawa7.httpsnippet.generators.clojure;

import java.util.*;

import io.atkawa7.har.HarHeader;
import io.atkawa7.har.HarParam;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;
import io.github.atkawa7.httpsnippet.utils.HarUtils;

public class CljHttp extends CodeGenerator {
  private static final List<String> SUPPORTED_METHODS =
      Arrays.asList("get", "post", "put", "delete", "patch", "head", "options");

  public CljHttp() {
    super(Client.CJ_HTTP, Language.CLOJURE);
  }

  private boolean isNotSupported(final String method) {
    return !SUPPORTED_METHODS.contains(method.toLowerCase());
  }

  private String padBlock(final int max, String input) {
    int len = max;
    StringBuilder padding = new StringBuilder();
    while (len > 0) {
      padding.append(" ");
      len--;
    }
    return input.replace("\n", "\n" + padding);
  }

  private <T> String literalRepresentation(T value) {
    if (Objects.isNull(value)) {
      return "nil";
    } else if (value instanceof String) {
      return "\"" + ((String) value).replace("\"", "\\\"") + "\"";
    } else if (value instanceof CljFile || value instanceof CljKeyword) {
      return value.toString();
    } else if (value instanceof List) {
      List list = (List) value;
      List<String> listBuilder = new ArrayList<>();
      for (Object obj : list) {
        listBuilder.add(this.literalRepresentation(obj));
      }
      return "[" + padBlock(1, String.join(" ", listBuilder)) + "]";
    } else if (value instanceof Map) {
      Map<Object, Object> map = (Map) value;
      List<String> listBuilder = new ArrayList<>();

      for (Map.Entry<Object, Object> entry : map.entrySet()) {
        int length = entry.getKey().toString().length();
        String val = padBlock(length + 2, literalRepresentation(entry.getValue()));
        String format = String.format(":%s %s", entry.getKey(), val);
        listBuilder.add(format);
      }
      return "{" + padBlock(1, String.join("\n ", listBuilder)) + "}";
    } else {
      return HarUtils.defaultIfNull(value, "");
    }
  }

  @Override
  protected String generateCode(CodeRequest codeRequest) throws Exception {
    if (isNotSupported(codeRequest.getMethod())) {
      throw new RuntimeException(String.format("Request method %s", codeRequest.getMethod()));
    }

    CodeBuilder code = new CodeBuilder();

    Map<String, Object> body = new LinkedHashMap<>();

    Optional<HarHeader> optionalHarHeader = codeRequest.find(HttpHeaders.ACCEPT);

    if (codeRequest.hasHeadersAndCookies()) {
      Map<String, String> map = codeRequest.allHeadersAsMap();
      map.remove(HttpHeaders.CONTENT_TYPE.toLowerCase());
      optionalHarHeader.ifPresent(
          harHeader -> {
            if (MediaType.APPLICATION_JSON.equalsIgnoreCase(harHeader.getValue())) {
              map.remove(HttpHeaders.ACCEPT.toLowerCase());
            }
          });

      Map lowercaseMap = new HashMap();

      map.forEach((k, v) -> lowercaseMap.put(k.toLowerCase(), v));

      if (lowercaseMap.size() > 0) {
        body.put("headers", lowercaseMap);
      }
    }

    if (codeRequest.hasQueryStrings()) {
      body.put("query-params", codeRequest.unwrapQueryStrings());
    }

    if (codeRequest.hasBody()) {
      switch (codeRequest.getMimeType()) {
        case MediaType.APPLICATION_JSON:
          if (codeRequest.hasText()) {

            body.put("content-type", new CljKeyword("json"));
            body.put("form-params", codeRequest.fromJsonString());
          }
          break;
        case MediaType.APPLICATION_FORM_URLENCODED:
          if (codeRequest.hasParams()) {
            body.put("form-params", codeRequest.paramsAsMap());
          }
          break;

        case MediaType.MULTIPART_FORM_DATA:
          if (codeRequest.hasParams()) {
            List<Object> multipart = new ArrayList<>();
            for (HarParam param : codeRequest.getParams()) {
              Map<String, Object> content = new HashMap<>();
              Object value =
                  (StringUtils.isNotBlank(param.getFileName())
                          && StringUtils.isBlank(param.getValue()))
                      ? new CljFile(param.getFileName())
                      : param.getValue();
              content.put("name", param.getName());
              content.put("content", value);
              multipart.add(content);
            }
            body.put("multipart", multipart);
          }
          break;
        default:
          {
            body.put("body", codeRequest.getText());
          }
      }
    }

    optionalHarHeader.ifPresent(
        harHeader -> {
          if (MediaType.APPLICATION_JSON.equalsIgnoreCase(harHeader.getValue())) {
            body.put("accept", new CljKeyword("json"));
          }
        });

    code.push("(require '[clj-http.client :as client])\n");

    if (ObjectUtils.isEmpty(body)) {
      code.push("(client/%s \"%s\")", codeRequest.getMethod().toLowerCase(), codeRequest.getUrl());
    } else {
      code.push(
          "(client/%s \"%s\" %s)",
          codeRequest.getMethod().toLowerCase(),
          codeRequest.getUrl(),
          padBlock(
              11 + codeRequest.getMethod().length() + codeRequest.getUrl().length(),
              literalRepresentation(body)));
    }
    code.blank();
    return code.join();
  }

  @AllArgsConstructor
  @Getter
  static
  class CljFile {
    private final String path;

    @Override
    public String toString() {
      return "(clojure.java.io/file \"" + this.path + "\")";
    }
  }

  @AllArgsConstructor
  @Getter
  static
  class CljKeyword {
    private final String name;

    public String toString() {
      return ':' + this.name;
    }
  }
}
