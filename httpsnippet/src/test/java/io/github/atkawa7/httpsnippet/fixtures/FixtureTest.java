package io.github.atkawa7.httpsnippet.fixtures;

import io.github.atkawa7.httpsnippet.generators.HttpSnippetCodeGenerator;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.HttpSnippet;
import io.github.atkawa7.httpsnippet.models.Language;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.net.FileNameMap;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class FixtureTest {

    public  static  Stream<Fixture> fixtureStream() throws Exception{
        List<Fixture> fixtures = FixtureBuilder.builder()
                .applicationFormEncoded()
                .applicationJson()
                .cookies()
                .customMethod()
                .fullRequest()
                .shortRequest()
                .headers()
                .multipartData()
                .multipartFile()
                .multipartFormData()
                .query()
                .textPlain()
                .jsonObjectNull()
                .https()
                .build();
        return  fixtures.stream();
    }

    @ParameterizedTest
    @MethodSource("fixtureStream")
    void testFixtures(Fixture fixture) throws Exception{
        Path currentPath  = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        Path output = Paths.get(currentPath.toString(), "output");

            HttpSnippetCodeGenerator codeGenerator = new HttpSnippetCodeGenerator();
            List<HttpSnippet> snippets = codeGenerator.snippets(fixture.getHarRequest());
            for(HttpSnippet snippet: snippets){

                logger.info("{}");
                Client client = snippet.getClient();
                Language language = snippet.getLanguage();
                String  code = snippet.getCode();
                Path codeDir = Paths.get(output.toString(),
                        language.getKey().toLowerCase().replaceAll("[^a-zA-Z0-9-_\\.]", ""),
                        client.getKey().toLowerCase().replaceAll("[^a-zA-Z0-9-_\\.]", ""));
                Path codePath =Paths.get(codeDir.toString(), String.format("%s%s", fixture.getFixtureType().getName(), language.getExtname()));

                assertEquals(FileUtils.readFileToString(codePath.toFile()), code);
            }


    }
}
