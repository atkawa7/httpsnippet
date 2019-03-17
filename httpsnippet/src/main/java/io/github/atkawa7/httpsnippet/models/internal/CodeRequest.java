package io.github.atkawa7.httpsnippet.models.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartbear.har.model.*;
import io.github.atkawa7.httpsnippet.http.*;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

// internal wrapper class around har request
public final class CodeRequest {

private final String method;
private final String url;
private final String httpVersion;
private final List<HarCookie> cookies;
private final List<HarHeader> headers;
private final List<HarQueryString> queryStrings;
private final List<HarParam> params;
private final String mimeType;
private final String text;

// precomputed values to speed up their reuse

private final URL _url;
private final String _cookieString;
private final boolean _hasText;
private final boolean _hasParams;
private final boolean _hasHeaders;
private final boolean _hasCookies;
private final boolean _hasQueryStrings;
private final boolean _hasAttachments;

private Map<String, String> _headers;
private Map<String, String> _allHeaders;
private Map<String, String> _queryStrings;
private Map<String, String> _params;
private Map<String, String> _cookies;

private CodeRequest(HarRequest harRequest) throws Exception {
	Objects.requireNonNull(harRequest, "Har Request cannot be null");
	this.method = StringUtils.defaultIfBlank(harRequest.getMethod(), HttpMethod.GET.name());
	this.url = harRequest.getUrl();
	this.headers = ObjectUtils.defaultIfNull(harRequest.getHeaders());
	this.cookies = ObjectUtils.defaultIfNull(harRequest.getCookies());
	this.httpVersion =
		StringUtils.defaultIfBlank(
			harRequest.getHttpVersion(), HttpVersion.HTTP_1_1.getProtocolName());
	this.queryStrings = ObjectUtils.defaultIfNull(harRequest.getQueryString());

	HarPostData harPostData = harRequest.getPostData();
	List<HarParam> harParams = ObjectUtils.isNotNull(harPostData) ? harPostData.getParams() : null;
	String mimeType = ObjectUtils.isNotNull(harPostData) ? harPostData.getMimeType() : null;
	String text = ObjectUtils.isNotNull(harPostData) ? harPostData.getText() : null;

	this.params = ObjectUtils.defaultIfNull(harParams);
	this.mimeType = defaultMimeType(mimeType);
	this.text = StringUtils.defaultIfEmpty(text, "");

	this._url = new URL(harRequest.getUrl());
	this._cookieString =
		cookies.stream()
			.map(e -> e.getName() + "=" + e.getValue())
			.collect(Collectors.joining(";"));
	this._hasText = StringUtils.isNotEmpty(text);
	this._hasParams = ObjectUtils.isNotEmpty(params);
	this._hasHeaders = ObjectUtils.isNotEmpty(headers);
	this._hasCookies = ObjectUtils.isNotEmpty(cookies);
	this._hasQueryStrings = ObjectUtils.isNotEmpty(queryStrings);
	this._hasAttachments =
		this.params.stream()
			.filter(h -> ObjectUtils.isNotNull(h) && StringUtils.isNotBlank(h.getFileName()))
			.findFirst()
			.isPresent();

	this._headers =
		headers.stream().collect(Collectors.toMap(HarHeader::getName, HarHeader::getValue));
	this._cookies = new HashMap<>();
	this._cookies.put(HttpHeaders.COOKIE, this._cookieString);
	this._params = params.stream().collect(Collectors.toMap(HarParam::getName, HarParam::getValue));
	this._queryStrings =
		queryStrings.stream()
			.collect(Collectors.toMap(HarQueryString::getName, HarQueryString::getValue));
	this._allHeaders = new HashMap<>();
	if(_hasCookies){
		this._allHeaders.putAll(_cookies);
	}
	this._allHeaders.putAll(_headers);
}

public Optional<HarHeader> find(String headerName) {
	return this.headers.stream()
		.filter(
			harHeader ->
				ObjectUtils.isNotNull(harHeader)
					&& ObjectUtils.isNotNull(harHeader.getName())
					&& harHeader.getName().equalsIgnoreCase(headerName))
		.findFirst();
}

public String getMethod() {
	return method;
}

public String getUrl() {
	return url;
}

public String toJsonString() throws Exception {
	return ObjectUtils.toJsonString(text);
}

public Map<String, Object> fromJsonString() throws Exception {
	return ObjectUtils.fromJsonString(text);
}

public String getHttpVersion() {
	return httpVersion;
}

public List<HarCookie> getCookies() {
	return cookies;
}

public List<HarHeader> getHeaders() {
	return headers;
}

public List<HarQueryString> getQueryStrings() {
	return queryStrings;
}

public List<HarParam> getParams() {
	return params;
}

public String getMimeType() {
	return mimeType;
}

public String getText() {
	return text;
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
	return _allHeaders;
}

public Map<String, String> queryStringsAsMap() {
	return _queryStrings;
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
	return ObjectUtils.toJsonString(_params);
}

public String queryStringsToJsonString() throws JsonProcessingException {
	return ObjectUtils.toJsonString(_queryStrings);
}

public String headersToJsonString() throws JsonProcessingException {
	return ObjectUtils.toJsonString(_headers);
}

public String allHeadersToJsonString() throws JsonProcessingException {
	return ObjectUtils.toJsonString(_allHeaders);
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

public String getHost() {
	return _url.getHost();
}

public int getPort() {
	return _url.getPort();
}

public String getProtocol() {
	return _url.getProtocol();
}

public String getPath() {
	return _url.getPath();
}

public boolean isSecure() {
	return HttpScheme.HTTPS.equalsIgnoreCase(this.getProtocol());
}

public static CodeRequest newCodeRequest(HarRequest harRequest) throws Exception {
	return new CodeRequest(harRequest);
}
}
