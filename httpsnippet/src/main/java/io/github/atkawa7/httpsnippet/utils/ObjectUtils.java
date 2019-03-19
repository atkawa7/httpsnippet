package io.github.atkawa7.httpsnippet.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

@UtilityClass
public class ObjectUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJsonString(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    public static Map<String, Object> fromJsonString(String json) throws IOException {
        return objectMapper.readValue(json, Map.class);
    }

    public static void validateJSON(String jsonInString) throws Exception {
        if (StringUtils.isNotBlank(jsonInString)) {
            try {
                objectMapper.readTree(jsonInString);
            } catch (Exception ex) {
                throw new Exception("JSON validation failed");
            }
        } else {
            throw new Exception("JSON validation failed");
        }
    }

    public static URL newURL(String url) throws Exception {
        try {
            return new URL(url);
        } catch (Exception ex) {
            throw new Exception("Malformed url");
        }
    }

    public static <T> boolean isNotNull(T object) {
        return Objects.nonNull(object);
    }

    public static <T> boolean isNull(T object) {
        return Objects.isNull(object);
    }

    public static <T> String defaultIfNull(T obj, String str) {
        return isNull(obj) ? str : obj.toString();
    }

    public static <T> List<T> defaultIfNull(List<T> obj) {
        return isNull(obj) ? new ArrayList<>() : obj;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (Objects.isNull(obj)) {
            return true;
        }

        if (obj instanceof Optional) {
            return !((Optional) obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }

        // else
        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}
