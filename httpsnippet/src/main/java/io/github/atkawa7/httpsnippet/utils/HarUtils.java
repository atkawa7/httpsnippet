package io.github.atkawa7.httpsnippet.utils;

import com.smartbear.har.model.*;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.internal.Tuple;
import io.github.atkawa7.httpsnippet.models.internal.Validation;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class HarUtils {

	public static Set<Tuple> parse(@NonNull URI uri) throws Exception {
		Set<Tuple> queryStrings = new HashSet<>();
		String query = uri.getQuery();
		if (StringUtils.isNotEmpty(query)) {
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				final int idx = pair.indexOf("=");
				final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
				final String value =
						idx > 0 && pair.length() > idx + 1
								? URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
								: "";
				queryStrings.add(new Tuple(key, value));
	}
	}
		return queryStrings;
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

	private static List<Tuple> toTuples(List<HarQueryString> harQueryStrings) {
		return harQueryStrings.stream()
				.map(h -> new Tuple(h.getName(), h.getValue()))
				.collect(Collectors.toList());
	}

	private static String toQuery(Set<Tuple> tupleSet) {
		return tupleSet.stream()
				.map(
						t ->
								String.format(
										"%s=%s", t.getKey(), ObjectUtils.isNull(t.getValue()) ? "" : t.getValue()))
				.collect(Collectors.joining("&"));
	}

	public static AbstractMap.SimpleEntry<URL, List<HarQueryString>> newTuple(
			final String uri, final List<HarQueryString> harQueryStrings) throws Exception {

		try {
			Set<Tuple> tupleSet = new TreeSet<>();

			URI oldUri = new URI(uri);
			Set<Tuple> parsedTupleSet = parse(oldUri);

			tupleSet.addAll(parsedTupleSet);
			tupleSet.addAll(toTuples(harQueryStrings));

			String newQuery = toQuery(tupleSet);
			List<HarQueryString> result =
					tupleSet.stream()
							.map(t -> new HarQueryString(t.getKey(), t.getValue(), null))
							.collect(Collectors.toList());

			URI newUri =
					new URI(
							oldUri.getScheme(),
							oldUri.getAuthority(),
							oldUri.getPath(),
							newQuery,
							oldUri.getFragment());
			return new AbstractMap.SimpleEntry<>(newUri.toURL(), result);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Malformed url");
		}
	}
}
