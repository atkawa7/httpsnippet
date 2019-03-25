package io.github.atkawa7.httpsnippet.fixtures;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbear.har.model.HarRequest;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FixtureBuilder {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final List<FixtureType> fixtureTypes = new ArrayList<>();

  private static final String currentPath =
      Fixture.class.getProtectionDomain().getCodeSource().getLocation().getPath();
  public static final Path FIXTURES_INPUT_PATH = Paths.get(currentPath, "fixtures");
  public static final Path FIXTURES_OUTPUT_PATH = Paths.get(currentPath, "output");

  public static FixtureBuilder builder() {
    return new FixtureBuilder();
  }

  public FixtureBuilder applicationFormEncoded() {
    this.fixtureTypes.add(FixtureType.APPLICATION_FORM_ENCODED);
    return this;
  }

  public FixtureBuilder applicationJson() {
    this.fixtureTypes.add(FixtureType.APPLICATION_JSON);
    return this;
  }

  public FixtureBuilder cookies() {
    this.fixtureTypes.add(FixtureType.COOKIES);
    return this;
  }

  public FixtureBuilder customMethod() {
    this.fixtureTypes.add(FixtureType.CUSTOM_METHOD);
    return this;
  }

  public FixtureBuilder fullRequest() {
    this.fixtureTypes.add(FixtureType.FULL_REQUEST);
    return this;
  }

  public FixtureBuilder headers() {
    this.fixtureTypes.add(FixtureType.HEADERS);
    return this;
  }

  public FixtureBuilder https() {
    this.fixtureTypes.add(FixtureType.HTTPS);
    return this;
  }

  public FixtureBuilder jsonObjectNull() {
    this.fixtureTypes.add(FixtureType.JSON_OBJECT_NULL);
    return this;
  }

  public FixtureBuilder multipartData() {
    this.fixtureTypes.add(FixtureType.MULTIPART_DATA);
    return this;
  }

  public FixtureBuilder multipartFile() {
    this.fixtureTypes.add(FixtureType.MULTIPART_FILE);
    return this;
  }

  public FixtureBuilder multipartFormData() {
    this.fixtureTypes.add(FixtureType.MULTIPART_FORM_DATA);
    return this;
  }

  public FixtureBuilder query() {
    this.fixtureTypes.add(FixtureType.QUERY);
    return this;
  }

  public FixtureBuilder shortRequest() {
    this.fixtureTypes.add(FixtureType.SHORT_REQUEST);
    return this;
  }

  public FixtureBuilder textPlain() {
    this.fixtureTypes.add(FixtureType.TEXT_PLAIN);
    return this;
  }

  public List<Fixture> build() throws Exception {
    List<Fixture> fixtures = new ArrayList<>();
    for (FixtureType fixtureType : fixtureTypes) {
      Path filePath =
          Paths.get(
              FIXTURES_INPUT_PATH.toString(), String.format("%s.json", fixtureType.getName()));
      HarRequest harRequest = objectMapper.readValue(filePath.toFile(), HarRequest.class);
      fixtures.add(new Fixture(fixtureType, harRequest));
    }
    return fixtures;
  }
}
