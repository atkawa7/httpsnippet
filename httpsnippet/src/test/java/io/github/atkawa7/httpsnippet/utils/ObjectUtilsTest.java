package io.github.atkawa7.httpsnippet.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ObjectUtilsTest {

    static Stream<Object> streamOfEmptyObjects(){
        return Stream.of(null, "", new HashMap<>(), new ArrayList<>(), Optional.empty());
    }

    static Stream<Object> streamOfObjects(){
        Map<String, String> h = new HashMap<>();
        h.put("foo", "foo");
        Optional<String> o  = Optional.of("foo");
        List<String> l  = new ArrayList<>();
        l.add("foo");
        return Stream.of(h, o, l, "foo", new Integer(10));
    }

    @Test
    void toJsonString() {
    }

    @Test
    void fromJsonString() {
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
    }

    @Test
    void isNull() {
    }

    @Test
    void defaultIfNull() {
    }

    @ParameterizedTest
    @MethodSource("streamOfEmptyObjects")
    void isEmpty1(Object obj) {
        assertTrue(ObjectUtils.isEmpty(obj));
    }

    @ParameterizedTest
    @MethodSource("streamOfObjects")
    void isEmpty2(Object obj) {
        assertFalse(ObjectUtils.isEmpty(obj));
    }

    @Test
    void isNotEmpty() {
    }
}