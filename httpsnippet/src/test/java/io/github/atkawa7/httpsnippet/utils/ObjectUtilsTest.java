package io.github.atkawa7.httpsnippet.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ObjectUtilsTest {

    static Stream<Object> streamOfEmptyObjects(){
        return Stream.of(null, "", new HashMap<>(), new ArrayList<>(), Optional.empty(), new int[]{});
    }

    static Stream<Object> streamOfObjects(){
        Map<String, String> h = new HashMap<>();
        h.put("foo", "foo");
        Optional<String> o  = Optional.of("foo");
        List<String> l  = new ArrayList<>();
        l.add("foo");
        return Stream.of(h, o, l, "foo", new Integer(10), new String[]{"foo", "bar"});
    }

    @Test
    void testToJsonString() {
        Map<String, String> h = new HashMap<>();
        h.put("foo", "foo");
        String result = assertDoesNotThrow(()->ObjectUtils.toJsonString(h));
        assertEquals("{\"foo\":\"foo\"}", result);
    }

    @Test
    void fromJsonString() {

        Map<String, Object> h = new HashMap<>();
        h.put("foo", "foo");
        Map<String, Object> result = assertDoesNotThrow(()->ObjectUtils.fromJsonString("{\"foo\":\"foo\"}"));
        assertEquals(h, result);
    }

    @Test
    void testValidateJSON() {
        Exception exception = assertThrows(Exception.class, ()->ObjectUtils.validateJSON(""));
        assertEquals("JSON validation failed", exception.getMessage());
        exception = assertThrows(Exception.class, ()->ObjectUtils.validateJSON("{"));
        assertEquals("JSON validation failed", exception.getMessage());
        assertDoesNotThrow(()->ObjectUtils.validateJSON("{}"));
}

    @Test
    void testNewURL() {
        Exception exception = assertThrows(Exception.class, ()->ObjectUtils.newURL("localhost"));
        assertEquals( "Malformed url", exception.getMessage());
        assertDoesNotThrow(()->ObjectUtils.newURL("http://localhost"));
    }

    @Test
    void isNotNull() {
        String nonNull = "foo";
        assertTrue(ObjectUtils.isNotNull(nonNull));
    }

    @Test
    void isNull() {
        String nullStr = null;
        assertTrue(ObjectUtils.isNull(nullStr));
    }

    @Test
    void defaultIfNull() {
        Object nullInteger  = null;
        String result  = ObjectUtils.defaultIfNull(nullInteger, "10");
        assertEquals("10", result);

        result  = ObjectUtils.defaultIfNull(20, "10");
        assertEquals("20", result);

        List<String> strings = null;
        List<String> rList  = ObjectUtils.defaultIfNull(strings);
        assertNotNull(rList);
        assertEquals(rList.size(), 0);

        List<String> iList  = new ArrayList<>();
        iList.add("foo");
        rList  = ObjectUtils.defaultIfNull(iList);
        assertNotNull(rList);
        assertEquals(iList, rList);
    }

    @ParameterizedTest
    @MethodSource("streamOfEmptyObjects")
    void isEmpty(Object obj) {
        assertTrue(ObjectUtils.isEmpty(obj));
        assertFalse(ObjectUtils.isNotEmpty(obj));
    }

    @ParameterizedTest
    @MethodSource("streamOfObjects")
    void isNotEmpty(Object obj) {
        assertTrue(ObjectUtils.isNotEmpty(obj));
        assertFalse(ObjectUtils.isEmpty(obj));
    }
}