package io.github.atkawa7.httpsnippet.http;

public enum HttpScheme {
HTTP("http"),
HTTPS("https");
private String scheme;

HttpScheme(String scheme) {
	this.scheme = scheme;
}

public boolean equalsIgnoreCase(String scheme) {
	return this.getScheme().equalsIgnoreCase(scheme);
}

public String getScheme() {
	return scheme;
}
}
