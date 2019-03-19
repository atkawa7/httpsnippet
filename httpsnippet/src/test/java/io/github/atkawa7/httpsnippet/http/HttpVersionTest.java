package io.github.atkawa7.httpsnippet.http;

import static org.junit.jupiter.api.Assertions.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class HttpVersionTest {

static Stream<AbstractMap.SimpleEntry<String, HttpVersion>> entries() {
	List<AbstractMap.SimpleEntry<String, HttpVersion>> entries = new ArrayList<>();
	entries.add(new AbstractMap.SimpleEntry("HTTP/0.9", HttpVersion.HTTP_0_9));
	entries.add(new AbstractMap.SimpleEntry("HTTP/1.0", HttpVersion.HTTP_1_0));
	entries.add(new AbstractMap.SimpleEntry("HTTP/1.1", HttpVersion.HTTP_1_1));
	entries.add(new AbstractMap.SimpleEntry("HTTP/2.0", HttpVersion.HTTP_2_0));
	return entries.stream();
}

static Stream<AbstractMap.SimpleEntry<Integer, HttpVersion>> majorEntries() {
	List<AbstractMap.SimpleEntry<Integer, HttpVersion>> entries = new ArrayList<>();
	entries.add(new AbstractMap.SimpleEntry(0, HttpVersion.HTTP_0_9));
	entries.add(new AbstractMap.SimpleEntry(1, HttpVersion.HTTP_1_0));
	entries.add(new AbstractMap.SimpleEntry(1, HttpVersion.HTTP_1_1));
	entries.add(new AbstractMap.SimpleEntry(2, HttpVersion.HTTP_2_0));
	return entries.stream();
}

static Stream<AbstractMap.SimpleEntry<Integer, HttpVersion>> minorEntries() {
	List<AbstractMap.SimpleEntry<Integer, HttpVersion>> entries = new ArrayList<>();
	entries.add(new AbstractMap.SimpleEntry(9, HttpVersion.HTTP_0_9));
	entries.add(new AbstractMap.SimpleEntry(0, HttpVersion.HTTP_1_0));
	entries.add(new AbstractMap.SimpleEntry(1, HttpVersion.HTTP_1_1));
	entries.add(new AbstractMap.SimpleEntry(0, HttpVersion.HTTP_2_0));
	return entries.stream();
}

@ParameterizedTest
@MethodSource("entries")
void testEqualsIgnoreCase(AbstractMap.SimpleEntry<String, HttpVersion> simpleEntry) {
	assertTrue(simpleEntry.getValue().equalsIgnoreCase(simpleEntry.getKey()));
	assertFalse(simpleEntry.getValue().equalsIgnoreCase("foo"));
}

@ParameterizedTest
@MethodSource("entries")
void testResolve(AbstractMap.SimpleEntry<String, HttpVersion> simpleEntry) {
	assertEquals(simpleEntry.getValue(), HttpVersion.resolve(simpleEntry.getKey()));
}

@ParameterizedTest
@MethodSource("majorEntries")
void getMajor(AbstractMap.SimpleEntry<Integer, HttpVersion> simpleEntry) {
	assertEquals(simpleEntry.getKey(), simpleEntry.getValue().getMajor());
}

@ParameterizedTest
@MethodSource("minorEntries")
void getMinor(AbstractMap.SimpleEntry<Integer, HttpVersion> simpleEntry) {
	assertEquals(simpleEntry.getKey(), simpleEntry.getValue().getMinor());
}
}
