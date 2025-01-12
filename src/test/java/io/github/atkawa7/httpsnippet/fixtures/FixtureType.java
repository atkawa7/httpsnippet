package io.github.atkawa7.httpsnippet.fixtures;

public enum FixtureType {
  MULTIPART_FILE("multipart-file"),
  APPLICATION_JSON("application-json"),
  QUERY("query"),
  FULL_REQUEST("full"),
  HEADERS("headers"),
  MULTIPART_FORM_DATA("multipart-form-data"),
  HTTPS("https"),
  COOKIES("cookies"),
  SHORT_REQUEST("short"),
  CUSTOM_METHOD("custom-method"),
  APPLICATION_FORM_ENCODED("application-form-encoded"),
  MULTIPART_DATA("multipart-data"),
  TEXT_PLAIN("text-plain"),
  JSON_OBJECT_NULL("jsonObj-null-value");
  private String name;

  FixtureType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "FixtureType{" + "name='" + name + '\'' + '}';
  }
}
