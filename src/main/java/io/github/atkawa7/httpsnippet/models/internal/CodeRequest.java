package io.github.atkawa7.httpsnippet.models.internal;

import java.util.*;
import java.util.stream.Collectors;

import io.github.atkawa7.har.*;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.atkawa7.httpsnippet.http.*;
import io.github.atkawa7.httpsnippet.utils.HarUtils;

// internal wrapper class around har request
public final class CodeRequest {

  public static final int HTTP_PORT = 80;
  public static final int HTTPS_PORT = 443;
  private final String method;
  @Getter
  private final String httpVersion;
  @Getter
  private final List<HarCookie> cookies;
  @Getter
  private final List<HarHeader> headers;
  @Getter
  private final List<HarQueryString> queryStrings;
  @Getter
  private final List<HarParam> params;
  @Getter
  private final String mimeType;
  @Getter
  private final String text;

  // precomputed values to speed up their reuse

  private final String _cookieString;
  private final boolean _hasText;
  private final boolean _hasParams;
  private final boolean _hasHeaders;
  private final boolean _hasCookies;
  private final boolean _hasQueryStrings;
  private final boolean _hasAttachments;

  private Map<String, String> _headers;
  private Map<String, String> _allHeaders;
  private Map<String, List<String>> _queryStrings;
  private Map<String, String> _params;
  private Map<String, String> _cookies;
  private URLWrapper urlWrapper;

  private CodeRequest(HarRequest harRequest) throws Exception {
    Objects.requireNonNull(harRequest, "Har Request cannot be null");

    this.urlWrapper = new URLWrapper(harRequest);
    this.method = HttpMethod.resolve(harRequest.getMethod()).name();

    this.headers = HarUtils.processHeaders(harRequest);
    this.cookies = HarUtils.processCookies(harRequest);
    this.queryStrings = HarUtils.processQueryStrings(urlWrapper.getQueryStrings());
    this.httpVersion = HttpVersion.resolve(harRequest.getHttpVersion()).getProtocolName();

    HarPostData harPostData = harRequest.getPostData();
    String mimeType = Objects.nonNull(harPostData) ? harPostData.getMimeType() : null;
    String text = Objects.nonNull(harPostData) ? harPostData.getText() : null;

    this.params = HarUtils.processParams(harPostData);
    this.mimeType = HarUtils.defaultMimeType(mimeType);
    this.text = StringUtils.defaultIfEmpty(text, "");

    this._cookieString =
        cookies.stream()
            .map(e -> e.getName() + "=" + e.getValue())
            .collect(Collectors.joining("; "));
    this._hasText = StringUtils.isNotEmpty(text);
    this._hasParams = ObjectUtils.isNotEmpty(params);
    this._hasHeaders = ObjectUtils.isNotEmpty(headers);
    this._hasCookies = ObjectUtils.isNotEmpty(cookies);
    this._hasQueryStrings = urlWrapper.hasQueryStrings();
    this._hasAttachments =
        this.params.stream()
            .filter(h -> StringUtils.isNotBlank(h.getFileName()))
            .findFirst()
            .isPresent();

    this._headers =
        headers.stream().collect(Collectors.toMap(HarHeader::getName, HarHeader::getValue));
    this._cookies = new HashMap<>();
    if (_hasCookies) {
      this._cookies.put(HttpHeaders.COOKIE, this._cookieString);
    }
    this._params =
        params.stream()
            .filter(h -> StringUtils.isNotBlank(h.getValue()))
            .collect(Collectors.toMap(HarParam::getName, HarParam::getValue));
    this._queryStrings = queryStringsToMap();
    this._allHeaders = new HashMap<>();
    this._allHeaders.putAll(_headers);
    this._allHeaders.putAll(_cookies);

    // validations
    this.validateMimeType();
  }

  private void validateMimeType() throws Exception {

    if (MediaType.APPLICATION_JSON.equalsIgnoreCase(mimeType)) {
      HarUtils.validateJSON(this.text);
    } else if (MediaType.APPLICATION_FORM_URLENCODED.equalsIgnoreCase(mimeType)) {
      if (!this._hasParams) {
        throw new Exception("Params cannot be empty");
      }
    } else if (MediaType.MULTIPART_FORM_DATA.equalsIgnoreCase(mimeType)) {
      if (!this._hasParams) {
        throw new Exception("Params cannot be empty");
      }
      //			if (!this._hasAttachments) {
      //				throw new Exception("Params must have attachments");
      //			}
    }
  }

  public Map<String, List<String>> queryStringsToMap() {
    Map<String, List<String>> map = new HashMap<>();
    for (HarQueryString queryString : queryStrings) {
      String key = queryString.getName();
      if (!map.containsKey(key)) {
        map.put(key, new ArrayList<>());
      }
      map.get(key).add(queryString.getValue());
    }
    return map;
  }

  public Optional<HarHeader> find(String headerName) {
    return this.headers.stream()
        .filter(
            harHeader ->
                Objects.nonNull(harHeader)
                    && Objects.nonNull(harHeader.getName())
                    && harHeader.getName().equalsIgnoreCase(headerName))
        .findFirst();
  }

  public String getMethod() {
    return method;
  }

  public String toJsonString() throws Exception {
    return HarUtils.toJsonString(text);
  }

  public String toPrettyJsonString() throws Exception {
    return HarUtils.toPrettyJsonString(fromJsonString());
  }

  public Map<String, Object> fromJsonString() throws Exception {
    return HarUtils.fromJsonString(text);
  }

  public boolean hasText() {
    return _hasText;
  }

  public boolean hasParams() {
    return _hasParams;
  }

  public boolean hasHeaders() {
    return _hasHeaders;
  }

  public boolean hasCookies() {
    return _hasCookies;
  }

  public boolean hasHeadersAndCookies() {
    return _hasHeaders || _hasCookies;
  }

  public boolean hasQueryStrings() {
    return _hasQueryStrings;
  }

  public boolean hasAttachments() {
    return _hasAttachments;
  }

  public boolean hasBody() {
    return _hasText || _hasParams;
  }

  public Map<String, String> headersAsMap() {
    return _headers;
  }

  public Map<String, String> allHeadersAsMap() {
    return newMap(_allHeaders);
  }

  public Map<String, List<String>> queryStringsAsMap() {
    Map<String, List<String>> newMap = new HashMap<>(_queryStrings);
    return newMap;
  }

  public Map<String, String> paramsAsMap() {
    return _params;
  }

  public Map<String, String> cookiesAsMap() {
    return _cookies;
  }

  public String getCookieString() {
    return _cookieString;
  }

  public String paramsToJSONString() throws JsonProcessingException {
    return HarUtils.toJsonString(_params);
  }

  public String paramsToPrettyJSONString() throws JsonProcessingException {
    return HarUtils.toPrettyJsonString(_params);
  }

  public String paramsToString() {
    return params.stream()
        .map(e -> e.getName() + "=" + e.getValue())
        .collect(Collectors.joining("&"));
  }

  public Map<String, Object> unwrapQueryStrings() {
    Map<String, Object> result = new LinkedHashMap<>();
    for (Map.Entry<String, List<String>> entry : _queryStrings.entrySet()) {
      if (entry.getValue().size() == 1) {
        result.put(entry.getKey(), entry.getValue().get(0));
      } else {
        result.put(entry.getKey(), entry.getValue());
      }
    }
    return result;
  }

  public String queryStringsToJsonString() throws JsonProcessingException {
    return HarUtils.toPrettyJsonString(this.unwrapQueryStrings());
  }

  public String headersToJsonString(boolean pretty) throws JsonProcessingException {
    return (pretty) ? HarUtils.toPrettyJsonString(_headers) : HarUtils.toJsonString(headers);
  }

  public String headersToJsonString() throws JsonProcessingException {
    return this.headersToJsonString(true);
  }

  public String allHeadersToJsonString(boolean pretty) throws JsonProcessingException {
    return (pretty) ? HarUtils.toPrettyJsonString(_allHeaders) : HarUtils.toJsonString(_allHeaders);
  }

  public String allHeadersToJsonString() throws JsonProcessingException {
    return allHeadersToJsonString(false);
  }

  public Map<String, String> newMap(Map<String, String> map) {
    Map<String, String> newMap = new HashMap<>(map);
    return newMap;
  }

  public String getHost() {
    return urlWrapper.url().getHost();
  }

  public int getPort() {
    return urlWrapper.getPort();
  }

  public String getProtocol() {
    return urlWrapper.url().getProtocol();
  }

  public String getPath() {
    return urlWrapper.getPath();
  }

  public String getFullPath() {
    return urlWrapper.getFullPath();
  }

  public String getUrl() {
    return urlWrapper.getUrl();
  }

  public String getFullUrl() {
    return urlWrapper.getFullUrl();
  }

  public boolean isSecure() {
    return urlWrapper.isSecure();
  }

  public static CodeRequest newCodeRequest(HarRequest harRequest) throws Exception {
    return new CodeRequest(harRequest);
  }

  public boolean isDefaultPort() {
    return urlWrapper.isDefaultPort();
  }
}
