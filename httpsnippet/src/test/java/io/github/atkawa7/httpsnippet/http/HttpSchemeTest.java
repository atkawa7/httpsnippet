package io.github.atkawa7.httpsnippet.http;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HttpSchemeTest {

  @ParameterizedTest
  @EnumSource(HttpScheme.class)
  void testEqualsIgnoreCase(HttpScheme scheme) {
    assertTrue(scheme.equalsIgnoreCase(scheme.name()));
    assertFalse(scheme.equalsIgnoreCase("foo"));
  }
}
