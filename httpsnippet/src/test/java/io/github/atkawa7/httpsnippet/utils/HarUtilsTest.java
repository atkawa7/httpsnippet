package io.github.atkawa7.httpsnippet.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HarUtilsTest {
  @Test
  void testToJsonString() {
    Map<String, String> h = new HashMap<>();
    h.put("foo", "foo");
    String result = assertDoesNotThrow(() -> HarUtils.toJsonString(h));
    assertEquals("{\"foo\":\"foo\"}", result);
  }

  @Test
  void testFromJsonString() {

    Map<String, Object> h = new HashMap<>();
    h.put("foo", "foo");
    Map<String, Object> result =
            assertDoesNotThrow(() -> HarUtils.fromJsonString("{\"foo\":\"foo\"}"));
    assertEquals(h, result);
  }

  @Test
  void testValidateJSON() {
    Exception exception = assertThrows(Exception.class, () -> HarUtils.validateJSON(""));
    assertEquals("JSON validation failed", exception.getMessage());
    exception = assertThrows(Exception.class, () -> HarUtils.validateJSON("{"));
    assertEquals("JSON validation failed", exception.getMessage());
    assertDoesNotThrow(() -> HarUtils.validateJSON("{}"));
  }

  @Test
  void testDefaultIfNull() {
    Object nullInteger = null;
    String result = HarUtils.defaultIfNull(nullInteger, "10");
    assertEquals("10", result);

    result = HarUtils.defaultIfNull(20, "10");
    assertEquals("20", result);

    List<String> strings = null;
    List<String> rList = HarUtils.defaultIfNull(strings);
    assertNotNull(rList);
    assertEquals(rList.size(), 0);

    List<String> iList = new ArrayList<>();
    iList.add("foo");
    rList = HarUtils.defaultIfNull(iList);
    assertNotNull(rList);
    assertEquals(iList, rList);
  }
}
