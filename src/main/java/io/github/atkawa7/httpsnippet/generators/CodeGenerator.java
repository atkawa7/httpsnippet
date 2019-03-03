package io.github.atkawa7.httpsnippet.generators;

import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import com.smartbear.har.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public abstract class CodeGenerator {

    protected final Client client;
    protected final Language language;

    public CodeGenerator(Client client, Language language) {
        this.client = client;
        this.language = language;
    }

    public String toJson(Object value) throws Exception {
        return ObjectUtils.writeValueAsString(value);
    }

    public Optional<HarHeader> find(List<HarHeader> headers, String headerName) {
        return (StringUtils.isEmpty(headerName) || ObjectUtils.isEmpty(headers))
                ? Optional.empty()
                : headers.stream()
                .filter(harHeader -> harHeader.getName().equalsIgnoreCase(headerName))
                .findFirst();
    }

    public abstract String code(final HarRequest harRequest) throws Exception;

    public String asCookies(List<HarCookie> cookies) {
        return ObjectUtils.isNull(cookies)
                ? StringUtils.EMPTY
                : cookies.stream()
                .map(e -> e.getName() + "=" + e.getValue())
                .collect(Collectors.joining(";"));
    }

    public Map<String, String> asHeaders(List<HarHeader> headers) {
        return ObjectUtils.isNull(headers)
                ? new HashMap<>()
                : headers.stream().collect(Collectors.toMap(HarHeader::getName, HarHeader::getValue));
    }

    public Map<String, String> asQueryStrings(List<HarQueryString> queryStrings) {
        return ObjectUtils.isNull(queryStrings)
                ? new HashMap<>()
                : queryStrings.stream()
                .collect(Collectors.toMap(HarQueryString::getName, HarQueryString::getValue));
    }

    public Map<String, String> asParams(List<HarParam> params) {
        return ObjectUtils.isNull(params)
                ? new HashMap<>()
                : params.stream().collect(Collectors.toMap(HarParam::getName, HarParam::getValue));
    }

    public Map<String, String> asHeaders(HarRequest harRequest) {
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

    public boolean hasText(HarPostData harPostData) {
        return ObjectUtils.isNotNull(harPostData) && StringUtils.isNotEmpty(harPostData.getText());
    }

    public boolean hasParams(List<HarParam> params) {
        return ObjectUtils.isNotEmpty(params);
    }
}
