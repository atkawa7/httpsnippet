package io.github.atkawa7.httpsnippet.generators;

import com.smartbear.har.model.*;
import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public abstract class CodeGenerator {

    protected final Client client;
    protected final Language language;

    protected CodeGenerator(Client client, Language language) {
        this.client = client;
        this.language = language;
    }

    public String code(final HarRequest harRequest) throws Exception {
        Objects.requireNonNull(harRequest, "HarRequest cannot be null");
        return this.generateCode(harRequest);
    }

    protected abstract String generateCode(final HarRequest harRequest) throws Exception;

    protected String toJson(Object value) throws Exception {
        return ObjectUtils.writeValueAsString(value);
    }

    protected Optional<HarHeader> find(List<HarHeader> headers, String headerName) {
        return (StringUtils.isEmpty(headerName) || ObjectUtils.isEmpty(headers))
                ? Optional.empty()
                : headers.stream()
                .filter(harHeader -> harHeader.getName().equalsIgnoreCase(headerName))
                .findFirst();
    }

    protected String asCookies(List<HarCookie> cookies) {
        return ObjectUtils.isEmpty(cookies)
                ? StringUtils.EMPTY
                : cookies.stream()
                .map(e -> e.getName() + "=" + e.getValue())
                .collect(Collectors.joining(";"));
    }

    protected Map<String, String> asHeaders(List<HarHeader> headers) {
        return ObjectUtils.isNull(headers)
                ? new HashMap<>()
                : headers.stream().collect(Collectors.toMap(HarHeader::getName, HarHeader::getValue));
    }

    protected Map<String, String> asQueryStrings(List<HarQueryString> queryStrings) {
        return ObjectUtils.isNull(queryStrings)
                ? new HashMap<>()
                : queryStrings.stream()
                .collect(Collectors.toMap(HarQueryString::getName, HarQueryString::getValue));
    }

    protected Map<String, String> asParams(List<HarParam> params) {
        return ObjectUtils.isNull(params)
                ? new HashMap<>()
                : params.stream().collect(Collectors.toMap(HarParam::getName, HarParam::getValue));
    }

    protected Map<String, String> asHeaders(HarRequest harRequest) {
        Map<String, String> result = new HashMap<>();
        List<HarHeader> headers = harRequest.getHeaders();
        if (ObjectUtils.isNotNull(headers)) {
            for (HarHeader harHeader : headers) {
                result.put(harHeader.getName(), harHeader.getValue());
            }
        }
        List<HarCookie> cookies = harRequest.getCookies();
        if (ObjectUtils.isNotEmpty(cookies)) {
            result.put(HttpHeaders.COOKIE, asCookies(cookies));
        }

        return result;
    }

    protected String getMimeType(HarPostData harPostData) {
        if (ObjectUtils.isNotNull(harPostData)) {
            String mimeType = harPostData.getMimeType();
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
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    protected boolean hasText(HarPostData harPostData) {
        return ObjectUtils.isNotNull(harPostData) && StringUtils.isNotEmpty(harPostData.getText());
    }
}
