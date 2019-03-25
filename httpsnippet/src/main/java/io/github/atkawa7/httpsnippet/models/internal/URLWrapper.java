package io.github.atkawa7.httpsnippet.models.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;

import com.smartbear.har.model.HarQueryString;
import com.smartbear.har.model.HarRequest;

import io.github.atkawa7.httpsnippet.http.HttpScheme;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;

final class URLWrapper {
  public static final int HTTP_PORT = 80;
  public static final int HTTPS_PORT = 443;
  public static final String FORWARD_SLASH = "/";

  private final String fullUrl;
  private final String url;
  private final int _port;
  private final boolean _hasQueryStrings;
  private final boolean _isSecure;
  private final List<HarQueryString> queryStrings;
  private final URL _url;
  private final URL _fullUrl;
  private final String _path;
  private final String _fullPath;

  public URLWrapper(final HarRequest harRequest) throws Exception {
    AbstractMap.SimpleEntry<URL, List<HarQueryString>> entry = newTuple(harRequest);
    this._fullUrl = entry.getKey();
    this._url =
        new URL(_fullUrl.getProtocol(), _fullUrl.getHost(), _fullUrl.getPort(), _fullUrl.getPath());
    this.queryStrings = entry.getValue();
    this._hasQueryStrings = ObjectUtils.isNotEmpty(queryStrings);

    this.url = _url.toString();
    this.fullUrl = _fullUrl.toString();
    this._isSecure = HttpScheme.HTTPS.equalsIgnoreCase(_fullUrl.getProtocol());
    this._port = this.newPort();
    this._fullPath =
        StringUtils.isNotBlank(_fullUrl.getFile()) ? _fullUrl.getFile() : FORWARD_SLASH;
    this._path = StringUtils.isNotBlank(_url.getFile()) ? _url.getFile() : FORWARD_SLASH;
  }

  public String getFullUrl() {
    return _hasQueryStrings ? fullUrl : url;
  }

  public String getUrl() {
    return url;
  }

  public int getPort() {
    return _port;
  }

  public boolean hasQueryStrings() {
    return _hasQueryStrings;
  }

  public boolean isSecure() {
    return _isSecure;
  }

  public List<HarQueryString> getQueryStrings() {
    return queryStrings;
  }

  public URL url() {
    return _url;
  }

  public URL fullUrl() {
    return _fullUrl;
  }

  public String getPath() {
    return _path;
  }

  public String getFullPath() {
    return _hasQueryStrings ? _fullPath : _path;
  }

  private int newPort() {
    if (_fullUrl.getPort() <= 0) {
      return _isSecure ? HTTPS_PORT : HTTP_PORT;
    }
    return _fullUrl.getPort();
  }

  private static Set<Tuple> parse(@NonNull URL url) throws Exception {
    Set<Tuple> queryStrings = new HashSet<>();
    String query = url.getQuery();
    if (StringUtils.isNotEmpty(query)) {
      String[] pairs = query.split("&");
      for (String pair : pairs) {
        final int index = pair.indexOf("=");
        final String key = index > 0 ? URLDecoder.decode(pair.substring(0, index), "UTF-8") : pair;
        final String value =
            index > 0 && pair.length() > index + 1
                ? URLDecoder.decode(pair.substring(index + 1), "UTF-8")
                : "";
        queryStrings.add(new Tuple(key, value));
      }
    }
    return queryStrings;
  }

  private static List<URLWrapper.Tuple> toTuples(List<HarQueryString> harQueryStrings) {
    return harQueryStrings.stream()
        .map(h -> new URLWrapper.Tuple(h.getName(), h.getValue()))
        .collect(Collectors.toList());
  }

  private static String toQuery(Set<URLWrapper.Tuple> tupleSet) {
    return tupleSet.stream()
        .map(
            t ->
                String.format(
                    "%s=%s", t.getKey(), ObjectUtils.isNull(t.getValue()) ? "" : t.getValue()))
        .collect(Collectors.joining("&"));
  }

  private static AbstractMap.SimpleEntry<URL, List<HarQueryString>> newTuple(HarRequest harRequest)
      throws Exception {
    List<HarQueryString> harQueryStrings = ObjectUtils.defaultIfNull(harRequest.getQueryString());
    String url = harRequest.getUrl();

    try {
      Set<URLWrapper.Tuple> tupleSet = new TreeSet<>();
      URL oldUrl = new URL(url);

      Set<URLWrapper.Tuple> parsedTupleSet = parse(oldUrl);

      tupleSet.addAll(parsedTupleSet);
      tupleSet.addAll(toTuples(harQueryStrings));

      String newQuery = toQuery(tupleSet);
      List<HarQueryString> result =
          tupleSet.stream()
              .map(t -> new HarQueryString(t.getKey(), t.getValue(), null))
              .collect(Collectors.toList());

      String file = String.format("%s?%s", oldUrl.getPath(), newQuery);
      URL newUrl = new URL(oldUrl.getProtocol(), oldUrl.getHost(), oldUrl.getPort(), file);

      return new AbstractMap.SimpleEntry<>(newUrl, result);
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
      throw new Exception("Malformed url");
    }
  }

  public boolean isDefaultPort() {
    return _port == HTTP_PORT || _port == HTTPS_PORT;
  }

  static class Tuple extends AbstractMap.SimpleEntry<String, String> implements Comparable<Tuple> {
    private Tuple(String key, String value) {
      super(key, value);
    }

    @Override
    public int compareTo(Tuple o) {
      int r = this.getKey().compareTo(o.getKey());
      return (r == 0) ? this.getValue().compareTo(o.getValue()) : r;
    }
  }
}
