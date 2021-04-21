package io.github.atkawa7.httpsnippet.utils;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import io.github.atkawa7.har.*;
import lombok.experimental.UtilityClass;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.internal.Validation;

@UtilityClass
public class HarUtils {

  private static final ObjectMapper prettyObjectMapper = new ObjectMapper();

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    prettyObjectMapper.setDefaultPrettyPrinter(new HttpSnippetPrettyPrinter());
    prettyObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
  }

  public static String toPrettyJsonString(Object value) throws JsonProcessingException {
    return prettyObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
  }

  public static String toJsonString(Object value) throws JsonProcessingException {
    return objectMapper.writeValueAsString(value);
  }

  public static Map<String, Object> fromJsonString(String json) throws IOException {
    return objectMapper.readValue(json, Map.class);
  }

  public static <T> boolean isNotNull(T object) {
    return Objects.nonNull(object);
  }

  public static <T> boolean isNull(T object) {
    return Objects.isNull(object);
  }

  public static <T> String defaultIfNull(T obj, String str) {
    return isNull(obj) ? str : obj.toString();
  }

  public static <T> List<T> defaultIfNull(List<T> obj) {
    return isNull(obj) ? new ArrayList<>() : obj;
  }

  public static void validateJSON(String jsonInString) throws Exception {
    if (StringUtils.isNotBlank(jsonInString)) {
      try {
        objectMapper.readTree(jsonInString);
      } catch (Exception ex) {
        throw new Exception("JSON validation failed");
      }
    } else {
      throw new Exception("JSON validation failed");
    }
  }

  public List<HarQueryString> processQueryStrings(List<HarQueryString> harQueryStrings)
      throws Exception {
    return Validation.validate(
        harQueryStrings,
        queryString -> {
          if (StringUtils.isBlank(queryString.getName())) {
            throw new Exception("QueryString cannot be null");
          }
        });
  }

  public List<HarCookie> processCookies(HarRequest harRequest) throws Exception {
    return Validation.validate(
        harRequest.getCookies(),
        cookie -> {
          if (StringUtils.isBlank(cookie.getName())) {
            throw new Exception("Cookie name must not be null");
          }
          if (StringUtils.isBlank(cookie.getValue())) {
            throw new Exception("Cookie value must not be null");
          }
        });
  }

  public List<HarParam> processParams(HarPostData postData) throws Exception {
    if (Objects.nonNull(postData)) {
      return Validation.validate(
          postData.getParams(),
          param -> {
            if (StringUtils.isBlank(param.getName())) {
              throw new Exception("Param name must not be blank");
            }
            if (StringUtils.isNotBlank(param.getFileName())
                && StringUtils.isBlank(param.getContentType())) {
              throw new Exception("Content type must no be blank when param has file name");
            }
          });
    }
    return new ArrayList<>();
  }

  public List<HarHeader> processHeaders(HarRequest harRequest) throws Exception {
    return Validation.validate(
        harRequest.getHeaders(),
        header -> {
          if (StringUtils.isBlank(header.getName())) {
            throw new Exception("Header name must not be null");
          }
          if (StringUtils.isBlank(header.getValue())) {
            throw new Exception("Header value must not be null");
          }
        });
  }

  public String defaultMimeType(final String mimeType) {
    if (StringUtils.isBlank(mimeType)) {
      return MediaType.APPLICATION_OCTET_STREAM;
    } else if (MediaType.isMultipartMediaType(mimeType)) {
      return MediaType.MULTIPART_FORM_DATA;
    } else if (MediaType.isJsonMediaType(mimeType)) {
      return MediaType.APPLICATION_JSON;
    } else if (MediaType.APPLICATION_FORM_URLENCODED.equalsIgnoreCase(mimeType)) {
      return MediaType.APPLICATION_FORM_URLENCODED;
    } else {
      return mimeType;
    }
  }

  static class HttpSnippetPrettyPrinter extends DefaultPrettyPrinter {

    public HttpSnippetPrettyPrinter(DefaultPrettyPrinter base) {
      super(base);
    }

    public HttpSnippetPrettyPrinter() {
      super();
      _arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
    }

    @Override
    public DefaultPrettyPrinter createInstance() {
      return new HttpSnippetPrettyPrinter(this);
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
      g.writeRaw(": ");
    }
  }
}
