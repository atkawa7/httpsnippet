package io.github.atkawa7.httpsnippet.models.internal;

import static io.github.atkawa7.httpsnippet.models.internal.CodeRequest.newCodeRequest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import io.github.atkawa7.har.*;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;


import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.http.HttpVersion;
import io.github.atkawa7.httpsnippet.http.MediaType;

@Slf4j
class CodeRequestTest {

  public static final String HTTP_URL = "http://localhost:8000/foo";
  public static final String HTTPS_URL = "https://localhost:4443/foo";

  public static List<HarCookie> cookies() {
    List<HarCookie> cookies = new ArrayList<>();
    cookies.add(new HarCookie().withName("foo").withValue("bar"));
    cookies.add(new HarCookie().withName("bar").withValue("baz"));
    return cookies;
  }

  public static List<HarHeader> jsonHeaders() {
    List<HarHeader> headers = new ArrayList<>();
    headers.add(
        new HarHeader()
            .withName(HttpHeaders.ACCEPT)
            .withValue(MediaType.APPLICATION_JSON)
            );
    headers.add(
        new HarHeader()
            .withName(HttpHeaders.CONTENT_TYPE)
            .withValue(MediaType.APPLICATION_JSON)
            );
    return headers;
  }

  public static List<HarHeader> multipartHeaders() {
    List<HarHeader> headers = new ArrayList<>();
    headers.add(
        new HarHeader()
            .withName(HttpHeaders.ACCEPT)
            .withValue(MediaType.APPLICATION_JSON)
            );
    headers.add(
        new HarHeader()
            .withName(HttpHeaders.CONTENT_TYPE)
            .withValue(MediaType.MULTIPART_FORM_DATA)
            );
    return headers;
  }

  public static List<HarHeader> formUrlencodedHeaders() {
    List<HarHeader> headers = new ArrayList<>();
    headers.add(
        new HarHeader()
            .withName(HttpHeaders.ACCEPT)
            .withValue(MediaType.APPLICATION_JSON)
            );
    headers.add(
        new HarHeader()
            .withName(HttpHeaders.CONTENT_TYPE)
            .withValue(MediaType.APPLICATION_FORM_URLENCODED)
            );
    return headers;
  }

  public static List<HarQueryString> queryStrings() {
    List<HarQueryString> queryStrings = new ArrayList<>();
    queryStrings.add(new HarQueryString().withName("foo").withValue("baz"));
    queryStrings.add(new HarQueryString().withName("foo").withValue("bar"));
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
    HarRequest harRequest = new HarRequest().withUrl("localhost");
    Exception thrown =
        assertThrows(
            Exception.class, () -> newCodeRequest(harRequest), "Expected exception thrown");
    assertEquals("Malformed url", thrown.getMessage());
  }

  @Test
  void testExceptionIsRaisedWhenContentTypeIsJsonAndPostDataTextIsNotJson() {
    HarRequest harRequest =
        new HarRequest()
            .withUrl(HTTP_URL)
            .withPostData(new HarPostData().withMimeType(MediaType.APPLICATION_JSON))
            ;
    Exception thrown =
        assertThrows(
            Exception.class, () -> newCodeRequest(harRequest), "Expected exception thrown");
    assertEquals("JSON validation failed", thrown.getMessage());
  }

  @Test
  void testExceptionIsThrownWhenContentTypeIsMultiFormDataAndListOfParamsIsEmpty() {
    HarRequest harRequest =
        new HarRequest()
            .withUrl(HTTP_URL)
            .withPostData(
                new HarPostData(MediaType.MULTIPART_FORM_DATA, new ArrayList<>(), null, null))
            ;
    Exception thrown =
        assertThrows(
            Exception.class, () -> newCodeRequest(harRequest), "Expected exception thrown");
    assertEquals("Params cannot be empty", thrown.getMessage());
  }

  @Test
  void testExceptionIsRaisedWhenContentTypeIsFormUrlEncodedAndListOfParamsIsEmpty() {
    HarRequest harRequest =
        new HarRequest()
            .withUrl(HTTP_URL)
            .withPostData(
                new HarPostData(
                    MediaType.APPLICATION_FORM_URLENCODED, new ArrayList<>(), null, null))
            ;
    Exception thrown =
        assertThrows(
            Exception.class, () -> newCodeRequest(harRequest), "Expected exception thrown");
    assertEquals("Params cannot be empty", thrown.getMessage());
  }

  @Test
  void testNoneBlankMimeTypeWhenPostDataHasText() {
    HarRequest harRequest =
        new HarRequest()
            .withUrl(HTTP_URL)
            .withPostData(
                new HarPostData()
                    .withText(postDataPlain())
                    .withMimeType(MediaType.TEXT_PLAIN)
                    )
            ;
    CodeRequest codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
    assertEquals(MediaType.TEXT_PLAIN, codeRequest.getMimeType());
  }

  @Test
  void testBlankMimeTypeWhenPostDataHasText() {
    HarRequest harRequest =
        new HarRequest()
            .withUrl(HTTP_URL)
            .withPostData(new HarPostData().withText(postDataJson()))
            ;
    CodeRequest codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
    assertEquals(MediaType.APPLICATION_OCTET_STREAM, codeRequest.getMimeType());
  }

  @Test
  void testURL() {
    HarRequest request1 =
        new HarRequest()
            .withUrl(HTTPS_URL)
            .withPostData(
                new HarPostData()
                    .withText(postDataPlain())
                    .withMimeType(MediaType.TEXT_PLAIN)
                    )
            ;
    CodeRequest codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
    assertTrue(codeRequest.isSecure());

    HarRequest request2 =
        new HarRequest()
            .withUrl("http://localhost:8000/foo")
            .withPostData(
                new HarPostData()
                    .withText(postDataPlain())
                    .withMimeType(MediaType.TEXT_PLAIN)
                    )
            ;
    codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(request2), "Exception thrown but not expected");
    assertFalse(codeRequest.isSecure());
  }

  @Test
  void testPort() {
    HarRequest request1 =
        new HarRequest()
            .withUrl("http://localhost/foo")
            .withPostData(
                new HarPostData()
                    .withText(postDataPlain())
                    .withMimeType(MediaType.TEXT_PLAIN)
                    )
            ;
    CodeRequest codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
    assertEquals(80, codeRequest.getPort());

    HarRequest request2 =
        new HarRequest()
            .withUrl("http://localhost:8080/foo")
            .withPostData(
                new HarPostData()
                    .withText(postDataPlain())
                    .withMimeType(MediaType.TEXT_PLAIN)
                    )
            ;
    codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(request2), "Exception thrown but not expected");
    assertEquals(8080, codeRequest.getPort());
  }

  @Test
  void testPath() {
    HarRequest request1 =
        new HarRequest()
            .withUrl("http://localhost")
            .withPostData(
                new HarPostData()
                    .withText(postDataPlain())
                    .withMimeType(MediaType.TEXT_PLAIN)
                    )
            ;
    CodeRequest codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
    assertEquals("/", codeRequest.getPath());

    HarRequest request2 =
        new HarRequest()
            .withUrl("http://localhost/foo")
            .withPostData(
                new HarPostData()
                    .withText(postDataPlain())
                    .withMimeType(MediaType.TEXT_PLAIN)
                    )
            ;
    codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(request2), "Exception thrown but not expected");
    assertEquals("/foo", codeRequest.getPath());
  }

  @Test
  void testHost() {
    HarRequest request1 =
        new HarRequest()
            .withUrl("http://localhost")
            .withPostData(
                new HarPostData()
                    .withText(postDataPlain())
                    .withMimeType(MediaType.TEXT_PLAIN)
                    )
            ;
    CodeRequest codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(request1), "Exception thrown but not expected");
    assertEquals("localhost", codeRequest.getHost());
  }

  private static List<HarParam> formUrlencodedParams() {
    List<HarParam> params = new ArrayList<>();
    params.add(new HarParam().withName("foo").withValue("bar"));
    params.add(new HarParam().withName("bar").withValue("baz"));
    return params;
  }

  public static List<HarParam> multipartParams() {
    List<HarParam> params = new ArrayList<>();
    params.add(new HarParam().withName("foo").withValue("bar"));
    params.add(
        new HarParam()
            .withName("bar")
            .withFileName("baz.txt")
            .withContentType(MediaType.TEXT_PLAIN)
            );
    return params;
  }

  @Test
  void testFormUrlEncodedWithParams() {
    List<HarParam> params = formUrlencodedParams();

    HarRequest request1 =
        new HarRequest()
            .withUrl("http://localhost")
            .withPostData(
                new HarPostData()
                    .withParams(params)
                    .withMimeType(MediaType.APPLICATION_FORM_URLENCODED)
                    )
            ;
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
        new HarRequest()
            .withUrl("http://localhost")
            .withPostData(
                new HarPostData()
                    .withParams(params)
                    .withMimeType(MediaType.MULTIPART_FORM_DATA)
                    )
            ;
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
        new HarRequest()
            .withUrl(HTTP_URL)
            .withPostData(
                new HarPostData()
                    .withText(postDataJson())
                    .withMimeType(MediaType.APPLICATION_JSON)
                    )
            ;
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
        new HarRequest().withUrl(HTTP_URL).withCookies(cookies()).withPostData(null);
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
        new HarRequest()
            .withUrl(HTTP_URL)
            .withHeaders(jsonHeaders())
            .withPostData(null)
            ;
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
        new HarRequest()
            .withUrl(HTTP_URL)
            .withQueryString(queryStrings())
            .withPostData(null)
            ;
    CodeRequest codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
    assertTrue(codeRequest.hasQueryStrings());
    assertEquals(2, codeRequest.getQueryStrings().size());
    assertFalse(codeRequest.hasBody());
  }

  @Test
  void testHttpVersion() {
    HarRequest harRequest = new HarRequest().withUrl(HTTP_URL).withPostData(null);
    CodeRequest codeRequest =
        assertDoesNotThrow(() -> newCodeRequest(harRequest), "Exception thrown but not expected");
    assertEquals(HttpVersion.HTTP_1_1, HttpVersion.resolve(codeRequest.getHttpVersion()));
  }
}
