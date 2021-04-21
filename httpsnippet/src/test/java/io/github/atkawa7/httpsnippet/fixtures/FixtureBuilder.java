package io.github.atkawa7.httpsnippet.fixtures;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.github.atkawa7.har.HarRequest;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.SystemUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class FixtureBuilder {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final List<FixtureType> fixtureTypes = new ArrayList<>();

  private final Path input;
  private final Path output;

  private FixtureBuilder() {
    String currentPath =
        this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    currentPath = SystemUtils.IS_OS_WINDOWS ? currentPath.substring(1) : currentPath;
    this.input = Paths.get(currentPath, "fixtures");
    this.output = Paths.get(currentPath, "output");
  }

  public static FixtureBuilder builder() {
    return new FixtureBuilder();
  }

  public Path getInput() {
    return input;
  }

  public Path getOutput() {
    return output;
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
      Path filePath = Paths.get(input.toString(), String.format("%s.json", fixtureType.getName()));
      HarRequest harRequest = objectMapper.readValue(filePath.toFile(), HarRequest.class);
      fixtures.add(new Fixture(fixtureType, harRequest));
    }
    return fixtures;
  }
}
