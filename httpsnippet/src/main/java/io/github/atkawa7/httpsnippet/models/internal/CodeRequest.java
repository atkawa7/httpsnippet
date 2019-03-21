package io.github.atkawa7.httpsnippet.models.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartbear.har.model.*;
import io.github.atkawa7.httpsnippet.http.*;
import io.github.atkawa7.httpsnippet.utils.HarUtils;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.atkawa7.httpsnippet.utils.ObjectUtils.newURL;

// internal wrapper class around har request
public final class CodeRequest {

	public static final int DEFAULT_PORT = 80;
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
private Map<String, List<String>> _queryStrings;
private Map<String, String> _params;
private Map<String, String> _cookies;

private CodeRequest(HarRequest harRequest) throws Exception {
	Objects.requireNonNull(harRequest, "Har Request cannot be null");

	this.method = HttpMethod.resolve(harRequest.getMethod()).name();
	this.url = harRequest.getUrl();
	this.headers = HarUtils.processHeaders(harRequest);
	this.cookies = HarUtils.processCookies(harRequest);
	this.queryStrings = HarUtils.processQueryStrings(harRequest);
	this.httpVersion = HttpVersion.resolve(harRequest.getHttpVersion()).getProtocolName();

	HarPostData harPostData = harRequest.getPostData();
	String mimeType = ObjectUtils.isNotNull(harPostData) ? harPostData.getMimeType() : null;
	String text = ObjectUtils.isNotNull(harPostData) ? harPostData.getText() : null;

	this.params = HarUtils.processParams(harPostData);
	this.mimeType = HarUtils.defaultMimeType(mimeType);
	this.text = StringUtils.defaultIfEmpty(text, "");

	this._url = newURL(harRequest.getUrl());

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
			ObjectUtils.validateJSON(this.text);
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

public Map<String, List<String>> queryStringsToMap(){
	Map<String, List<String>>  map = new HashMap<>();
	for(HarQueryString queryString: queryStrings){
		String key  = queryString.getName();
		if(!map.containsKey(key)){
			map.put(key, new ArrayList<>());
		}
		map.get(key).add(queryString.getValue());
	}
	return  map;
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

public Map<String, List<String>> queryStringsAsMap() {
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

public String getHost() {
	return _url.getHost();
}

public int getPort() {
	return _url.getPort() == -1 ? DEFAULT_PORT : _url.getPort();
}

public String getProtocol() {
	return _url.getProtocol();
}

public String getPath() {
	return StringUtils.isNotBlank(_url.getPath()) ? _url.getPath() : "/";
}

public boolean isSecure() {
	return HttpScheme.HTTPS.equalsIgnoreCase(this.getProtocol());
}

public static CodeRequest newCodeRequest(HarRequest harRequest) throws Exception {
	return new CodeRequest(harRequest);
}
}
