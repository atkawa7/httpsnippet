package io.github.atkawa7.httpsnippet.fixtures;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.atkawa7.httpsnippet.generators.HttpSnippetCodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.HttpSnippet;
import io.github.atkawa7.httpsnippet.models.Language;

@Slf4j
public class FixtureTest {

  private static FixtureBuilder fixtureBuilder = FixtureBuilder.builder();

  public static Stream<Fixture> fixtureStream() throws Exception {
    List<Fixture> fixtures =
        fixtureBuilder
            .applicationFormEncoded()
            .applicationJson()
            .cookies()
            .customMethod()
            .fullRequest()
            .shortRequest()
            .headers()
            //			.multipartData()
            //			.multipartFile()
            //			.multipartFormData()
            .query()
            .textPlain()
            .jsonObjectNull()
            .https()
            .build();
    return fixtures.stream();
  }

  @DisplayName("Testing  Fixtures")
  @ParameterizedTest(name = "Running - {0}")
  @MethodSource("fixtureStream")
  void testFixtures(Fixture fixture) throws Exception {
    HttpSnippetCodeGenerator codeGenerator = new HttpSnippetCodeGenerator();
    List<HttpSnippet> snippets = codeGenerator.snippets(fixture.getHarRequest());
    for (HttpSnippet snippet : snippets) {
      logger.info(
          "Generation HttpSnippet for lang {} using {}",
          snippet.getLanguage().getTitle(),
          snippet.getClient().getTitle());

      Client client = snippet.getClient();
      Language language = snippet.getLanguage();
      String code = snippet.getCode().replaceAll("\r\n", "\n").trim();
      Path codeDir =
          Paths.get(
              fixtureBuilder.getOutput().toString(),
              language.getKey().toLowerCase().replaceAll("[^a-zA-Z0-9-_\\.]", ""),
              client.getKey().toLowerCase().replaceAll("[^a-zA-Z0-9-_\\.]", ""));
      Path codePath =
          Paths.get(
              codeDir.toString(),
              String.format("%s%s", fixture.getFixtureType().getName(), language.getExtname()));

      String expected =
          FileUtils.readFileToString(codePath.toFile()).replaceAll("\r\n", "\n").trim();

      assertEquals(expected, code);
    }
  }
}
