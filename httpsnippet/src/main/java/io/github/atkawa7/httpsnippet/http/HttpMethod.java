package io.github.atkawa7.httpsnippet.http;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
  GET,
  POST,
  PUT,
  HEAD,
  DELETE,
  PATCH,
  OPTIONS,
  TRACE,
  CONNECTION;
  private static final Map<String, HttpMethod> mappings = new HashMap<>(18);

  static {
    for (HttpMethod httpMethod : values()) {
      mappings.put(httpMethod.name(), httpMethod);
    }
  }

  public static HttpMethod resolve(String method) {
    return mappings.containsKey(method) ? mappings.get(method) : GET;
  }
}
