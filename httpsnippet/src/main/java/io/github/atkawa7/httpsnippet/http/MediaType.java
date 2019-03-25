package io.github.atkawa7.httpsnippet.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MediaType {
  public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
  public static final String APPLICATION_JSON = "application/json";
  public static final String TEXT_JSON = "text/json";
  public static final String TEXT_X_JSON = "text/x-json";
  public static final String APPLICATION_X_JSON = "application/x-json";
  public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
  public static final String MULTIPART_FORM_DATA = "multipart/form-data";
  public static final String MULTIPART_MIXED = "multipart/mixed";
  public static final String MULTIPART_RELATED = "multipart/related";
  public static final String MULTIPART_ALTERNATIVE = "multipart/alternative";
  public static final List<String> MULTIPART_MEDIA_TYPES =
      Collections.unmodifiableList(
          Arrays.asList(
              MULTIPART_FORM_DATA, MULTIPART_MIXED, MULTIPART_RELATED, MULTIPART_ALTERNATIVE));
  public static final List<String> JSON_MEDIA_TYPES =
      Collections.unmodifiableList(
          Arrays.asList(APPLICATION_JSON, APPLICATION_X_JSON, TEXT_JSON, TEXT_X_JSON));
  public static final String TEXT_PLAIN = "text/plain";

  public static final boolean isMultipartMediaType(String header) {
    return MULTIPART_MEDIA_TYPES.indexOf(header) != -1;
  }

  public static final boolean isJsonMediaType(String header) {
    return JSON_MEDIA_TYPES.indexOf(header) != -1;
  }
}
