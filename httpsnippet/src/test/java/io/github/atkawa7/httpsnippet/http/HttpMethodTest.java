package io.github.atkawa7.httpsnippet.http;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class HttpMethodTest {
static Stream<String> streamUnsupportedMethods() {
	return Stream.of("", "   ", null, "foo");
}

@ParameterizedTest
@MethodSource(value = "streamUnsupportedMethods")
void testResolveForUnsupportedMethods(final String value) {
	HttpMethod method = HttpMethod.resolve(value);
	assertEquals(HttpMethod.GET, method);
}

@ParameterizedTest
@EnumSource(value = HttpMethod.class)
void testResolveForSupportedMethods(final HttpMethod value) {
	HttpMethod method = HttpMethod.resolve(value.name());
	assertEquals(value, method);
}
}
