package io.github.atkawa7.httpsnippet.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

@UtilityClass
public class ObjectUtils {

    private static final ObjectMapper prettyObjectMapper = new ObjectMapper();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        prettyObjectMapper.setDefaultPrettyPrinter(new HttpSnippetPrettyPrinter());
        prettyObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    }

    public static String toPrettyJsonString(Object value) throws JsonProcessingException {
        return prettyObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
    }

    public static String toJsonString(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    static class HttpSnippetPrettyPrinter extends DefaultPrettyPrinter {

        public HttpSnippetPrettyPrinter(DefaultPrettyPrinter base) {
            super(base);
        }

        public HttpSnippetPrettyPrinter() {
            super();
            _arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new HttpSnippetPrettyPrinter(this);
        }

        @Override
        public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
            g.writeRaw(": ");
        }
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
