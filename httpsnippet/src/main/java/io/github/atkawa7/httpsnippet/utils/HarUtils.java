package io.github.atkawa7.httpsnippet.utils;

import java.util.*;

import lombok.experimental.UtilityClass;

import org.apache.commons.lang3.StringUtils;

import com.smartbear.har.model.*;

import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.internal.Validation;

@UtilityClass
public class HarUtils {

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
    if (ObjectUtils.isNotNull(postData)) {
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
}
