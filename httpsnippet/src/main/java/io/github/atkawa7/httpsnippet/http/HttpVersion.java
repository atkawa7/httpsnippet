package io.github.atkawa7.httpsnippet.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpVersion {
  public static final String HTTP = "HTTP";

  public static final HttpVersion HTTP_0_9 = new HttpVersion(0, 9);
  public static final HttpVersion HTTP_1_0 = new HttpVersion(1, 0);
  public static final HttpVersion HTTP_1_1 = new HttpVersion(1, 1);
  public static final HttpVersion HTTP_2_0 = new HttpVersion(2, 0);

  private final int major;
  private final int minor;
  private final String protocolName;

  private HttpVersion(int major, int minor) {
    this(major, minor, String.format("%s/%s.%s", HTTP, major, minor));
  }

  public boolean equalsIgnoreCase(String httpVersion) {
    return this.protocolName.equalsIgnoreCase(httpVersion);
  }

  private static final Map<String, HttpVersion> mappings = new HashMap<>(18);

  static {
    for (HttpVersion httpVersion : Arrays.asList(HTTP_0_9, HTTP_1_0, HTTP_1_1, HTTP_2_0)) {
      mappings.put(httpVersion.getProtocolName(), httpVersion);
    }
  }

  public static HttpVersion resolve(String method) {
    return mappings.containsKey(method) ? mappings.get(method) : HttpVersion.HTTP_1_1;
  }

  @Override
  public String toString() {
    return protocolName;
  }
}
