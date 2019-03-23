package io.github.atkawa7.httpsnippet.models.internal;

import java.util.AbstractMap;

public class Tuple extends AbstractMap.SimpleEntry<String, String> implements Comparable<Tuple> {
public Tuple(String key, String value) {
	super(key, value);
}

@Override
public String getKey() {
	return super.getKey();
}

@Override
public String getValue() {
	return super.getValue();
}

@Override
public String setValue(String value) {
	return super.setValue(value);
}

@Override
public boolean equals(Object o) {
	return super.equals(o);
}

@Override
public int hashCode() {
	return super.hashCode();
}

@Override
public String toString() {
	return super.toString();
}

@Override
public int compareTo(Tuple o) {
	int r = this.getKey().compareTo(o.getKey());
	return (r == 0) ? this.getValue().compareTo(o.getValue()) : r;
}
}
