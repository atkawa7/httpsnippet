package io.github.atkawa7.httpsnippet.models.internal;

import static io.github.atkawa7.httpsnippet.models.internal.CodeRequest.newCodeRequest;
import static org.junit.jupiter.api.Assertions.*;

import com.smartbear.har.builder.*;
import com.smartbear.har.model.*;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.http.HttpVersion;
import io.github.atkawa7.httpsnippet.http.MediaType;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CodeRequestTest {

public static final String HTTP_URL = "http://localhost:8000/foo";
public static final String HTTPS_URL = "https://localhost:4443/foo";

public static List<HarCookie> cookies() {
	List<HarCookie> cookies = new ArrayList<>();
	cookies.add(new HarCookieBuilder().withName("foo").withValue("bar").build());
	cookies.add(new HarCookieBuilder().withName("bar").withValue("baz").build());
	return cookies;
}

public static List<HarHeader> jsonHeaders() {
	List<HarHeader> headers = new ArrayList<>();
	headers.add(
		new HarHeaderBuilder()
			.withName(HttpHeaders.ACCEPT)
			.withValue(MediaType.APPLICATION_JSON)
			.build());
	headers.add(
		new HarHeaderBuilder()
			.withName(HttpHeaders.CONTENT_TYPE)
			.withValue(MediaType.APPLICATION_JSON)
			.build());
	return headers;
}

public static List<HarHeader> multipartHeaders() {
	List<HarHeader> headers = new ArrayList<>();
	headers.add(
		new HarHeaderBuilder()
			.withName(HttpHeaders.ACCEPT)
			.withValue(MediaType.APPLICATION_JSON)
			.build());
	headers.add(
		new HarHeaderBuilder()
			.withName(HttpHeaders.CONTENT_TYPE)
			.withValue(MediaType.MULTIPART_FORM_DATA)
			.build());
	return headers;
}

public static List<HarHeader> formUrlencodedHeaders() {
	List<HarHeader> headers = new ArrayList<>();
	headers.add(
		new HarHeaderBuilder()
			.withName(HttpHeaders.ACCEPT)
			.withValue(MediaType.APPLICATION_JSON)
			.build());
	headers.add(
		new HarHeaderBuilder()
			.withName(HttpHeaders.CONTENT_TYPE)
			.withValue(MediaType.APPLICATION_FORM_URLENCODED)
			.build());
	return headers;
}

public static List<HarQueryString> queryStrings() {
	List<HarQueryString> queryStrings = new ArrayList<>();
	queryStrings.add(new HarQueryStringBuilder().withName("foo").withValue("baz").build());
	queryStrings.add(new HarQueryStringBuilder().withName("foo").withValue("bar").build());
	return queryStrings;
}

public static String postDataJson() {
	return "{\"foo\": \"bar\"}";
}

public static String postDataPlain() {
	return "name,value\nfoo,bar\nbar,baz";
}

@Test
void testExceptionRaisedWhenRequestIsNull() {
	Exception thrown =
		assertThrows(Exception.class, () -> newCodeRequest(null), "Expected exception thrown");
	assertEquals(thrown.getMessage(), "Har Request cannot be null");
}

@Test
void testExceptionRaisedWhenUrlIsMalformed() {
	HarRequest harRequest = new HarRequestBuilder().withUrl("localhost").build();
	Exception thrown =
		assertThrows(
			Exception.class, () -> newCodeRequest(harRequest), "Expected exception thrown");
	assertEquals("Malformed url", thrown.getMessage());
}

@Test
void testExceptionIsRaisedWhenContentTypeIsJsonAndPostDataTextIsNotJson() {
	HarRequest harRequest =
		new HarRequestBuilder()
			.withUrl(HTTP_URL)
			.withPostData(new HarPostDataBuilder().withMimeType(MediaType.APPLICATION_JSON).build())
			.build();
	Exception thrown =
		assertThrows(
			Exception.class, () -> newCodeRequest(harRequest), "Expected exception thrown");
	assertEquals("JSON validation failed", thrown.getMessage());
}

@Test
void testExceptionIsThrownWhenContentTypeIsMultiFormDataAndListOfParamsIsEmpty() {
	HarRequest harRequest =
		new HarRequestBuilder()
			.withUrl(HTTP_URL)
			.withPostData(
				new HarPostData(MediaType.MULTIPART_FORM_DATA, new ArrayList<>(), null, null))
			.build();
	Exception thrown =
		assertThrows(
			Exception.class, () -> newCodeRequest(harRequest), "Expected exception thrown");
	assertEquals("Params cannot be empty", thrown.getMessage());
}


@Test
void testExceptionIsRaisedWhenContentTypeIsFormUrlEncodedAndListOfParamsIsEmpty() {
	HarRequest harRequest =
		new HarRequestBuilder()
			.withUrl(HTTP_URL)
			.withPostData(
				new HarPostData(
					MediaType.APPLICATION_FORM_URLENCODED, new ArrayList<>(), null, null))
			.build();
	Exception thrown =
		assertThrows(
			Exception.class, () -> newCodeRequest(harRequest), "Expected exception thrown");
	assertEquals("Params cannot be empty", thrown.getMessage());
}

@Test
void testNoneBlankMimeTypeWhenPostDataHasText() {
	HarRequest harRequest =
		new HarRequestBuilder()
			.withUrl(HTTP_URL)
			.withPostData(
				new HarPostDataBuilder()
					.withText(postDataPlain())
					.withMimeType(MediaType.TEXT_PLAIN)
					.build())
			.build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
	assertEquals(MediaType.TEXT_PLAIN, codeRequest.getMimeType());
}

@Test
void testBlankMimeTypeWhenPostDataHasText() {
	HarRequest harRequest =
		new HarRequestBuilder()
			.withUrl(HTTP_URL)
			.withPostData(new HarPostDataBuilder().withText(postDataJson()).build())
			.build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
	assertEquals(MediaType.APPLICATION_OCTET_STREAM, codeRequest.getMimeType());
}

@Test
void testURL() {
	HarRequest request1 =
		new HarRequestBuilder()
			.withUrl(HTTPS_URL)
			.withPostData(
				new HarPostDataBuilder()
					.withText(postDataPlain())
					.withMimeType(MediaType.TEXT_PLAIN)
					.build())
			.build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
	assertTrue(codeRequest.isSecure());

	HarRequest request2 =
		new HarRequestBuilder()
			.withUrl("http://localhost:8000/foo")
			.withPostData(
				new HarPostDataBuilder()
					.withText(postDataPlain())
					.withMimeType(MediaType.TEXT_PLAIN)
					.build())
			.build();
	codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(request2), "Exception thrown but not expected");
	assertFalse(codeRequest.isSecure());
}

@Test
void testPort() {
	HarRequest request1 =
		new HarRequestBuilder()
			.withUrl("http://localhost/foo")
			.withPostData(
				new HarPostDataBuilder()
					.withText(postDataPlain())
					.withMimeType(MediaType.TEXT_PLAIN)
					.build())
			.build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
	assertEquals(80, codeRequest.getPort());

	HarRequest request2 =
		new HarRequestBuilder()
			.withUrl("http://localhost:8080/foo")
			.withPostData(
				new HarPostDataBuilder()
					.withText(postDataPlain())
					.withMimeType(MediaType.TEXT_PLAIN)
					.build())
			.build();
	codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(request2), "Exception thrown but not expected");
	assertEquals(8080, codeRequest.getPort());
}

@Test
void testPath() {
	HarRequest request1 =
		new HarRequestBuilder()
			.withUrl("http://localhost")
			.withPostData(
				new HarPostDataBuilder()
					.withText(postDataPlain())
					.withMimeType(MediaType.TEXT_PLAIN)
					.build())
			.build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
	assertEquals("/", codeRequest.getPath());

	HarRequest request2 =
		new HarRequestBuilder()
			.withUrl("http://localhost/foo")
			.withPostData(
				new HarPostDataBuilder()
					.withText(postDataPlain())
					.withMimeType(MediaType.TEXT_PLAIN)
					.build())
			.build();
	codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(request2), "Exception thrown but not expected");
	assertEquals("/foo", codeRequest.getPath());
}

@Test
void testHost() {
	HarRequest request1 =
		new HarRequestBuilder()
			.withUrl("http://localhost")
			.withPostData(
				new HarPostDataBuilder()
					.withText(postDataPlain())
					.withMimeType(MediaType.TEXT_PLAIN)
					.build())
			.build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
	assertEquals("localhost", codeRequest.getHost());
}

private static List<HarParam> formUrlencodedParams() {
	List<HarParam> params = new ArrayList<>();
	params.add(new HarParamsBuilder().withName("foo").withValue("bar").build());
	params.add(new HarParamsBuilder().withName("bar").withValue("baz").build());
	return params;
}

public static List<HarParam> multipartParams() {
	List<HarParam> params = new ArrayList<>();
	params.add(new HarParamsBuilder().withName("foo").withValue("bar").build());
	params.add(
		new HarParamsBuilder()
			.withName("bar")
			.withFileName("baz.txt")
			.withContentType(MediaType.TEXT_PLAIN)
			.build());
	return params;
}

@Test
void testFormUrlEncodedWithParams() {
	List<HarParam> params = formUrlencodedParams();

	HarRequest request1 =
		new HarRequestBuilder()
			.withUrl("http://localhost")
			.withPostData(
				new HarPostDataBuilder()
					.withParams(params)
					.withMimeType(MediaType.APPLICATION_FORM_URLENCODED)
					.build())
			.build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
	assertTrue(codeRequest.hasParams());
	assertEquals(2, codeRequest.getParams().size());
	assertFalse(codeRequest.hasAttachments());
	assertTrue(codeRequest.hasBody());
}

@Test
void testMultipartParams() {
	List<HarParam> params = multipartParams();

	HarRequest request1 =
		new HarRequestBuilder()
			.withUrl("http://localhost")
			.withPostData(
				new HarPostDataBuilder()
					.withParams(params)
					.withMimeType(MediaType.MULTIPART_FORM_DATA)
					.build())
			.build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
	assertTrue(codeRequest.hasParams());
	assertEquals(2, codeRequest.getParams().size());
	assertTrue(codeRequest.hasAttachments());
	assertTrue(codeRequest.hasBody());
}

@Test
void testPostDataHasText() {
	HarRequest harRequest =
		new HarRequestBuilder()
			.withUrl(HTTP_URL)
			.withPostData(
				new HarPostDataBuilder()
					.withText(postDataJson())
					.withMimeType(MediaType.APPLICATION_JSON)
					.build())
			.build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
	assertEquals(MediaType.APPLICATION_JSON, codeRequest.getMimeType());
	assertTrue(codeRequest.hasBody());
	assertFalse(codeRequest.hasHeadersAndCookies());
	assertTrue(codeRequest.hasText());
	assertEquals(postDataJson(), codeRequest.getText());
}

@Test
void testCookies() {
	HarRequest harRequest =
		new HarRequestBuilder().withUrl(HTTP_URL).withCookies(cookies()).withPostData(null).build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
	assertTrue(codeRequest.hasCookies());
	assertEquals(2, codeRequest.getCookies().size());
	assertFalse(codeRequest.hasBody());
	assertTrue(codeRequest.hasHeadersAndCookies());
}

	@Test
	void testHeaders() {
		HarRequest harRequest =
				new HarRequestBuilder().withUrl(HTTP_URL).withHeaders(jsonHeaders()).withPostData(null).build();
		CodeRequest codeRequest =
				assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
		assertTrue(codeRequest.hasHeaders());
		assertEquals(2, codeRequest.getHeaders().size());
		assertFalse(codeRequest.hasBody());
		assertTrue(codeRequest.hasHeadersAndCookies());
	}


	@Test
	void testQueryStrings() {
		HarRequest harRequest =
				new HarRequestBuilder().withUrl(HTTP_URL).withQueryString(queryStrings()).withPostData(null).build();
		CodeRequest codeRequest =
				assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
		assertTrue(codeRequest.hasQueryStrings());
		assertEquals(2, codeRequest.getQueryStrings().size());
		assertFalse(codeRequest.hasBody());
	}


	@Test
void testHttpVersion() {
	HarRequest harRequest = new HarRequestBuilder().withUrl(HTTP_URL).withPostData(null).build();
	CodeRequest codeRequest =
		assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
	assertEquals(HttpVersion.HTTP_1_1, HttpVersion.resolve(codeRequest.getHttpVersion()));
}

}
