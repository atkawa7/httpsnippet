package io.github.atkawa7.httpsnippet.generators;

import java.util.*;

import lombok.NonNull;

import com.smartbear.har.model.HarRequest;

import io.github.atkawa7.httpsnippet.generators.c.LibCurl;
import io.github.atkawa7.httpsnippet.generators.clojure.CljHttp;
import io.github.atkawa7.httpsnippet.generators.csharp.RestSharp;
import io.github.atkawa7.httpsnippet.generators.go.GoNative;
import io.github.atkawa7.httpsnippet.generators.java.Jsoup;
import io.github.atkawa7.httpsnippet.generators.java.OkHttp;
import io.github.atkawa7.httpsnippet.generators.java.Unirest;
import io.github.atkawa7.httpsnippet.generators.javascript.Fetch;
import io.github.atkawa7.httpsnippet.generators.javascript.JQuery;
import io.github.atkawa7.httpsnippet.generators.javascript.XMLHttpRequest;
import io.github.atkawa7.httpsnippet.generators.node.NodeNative;
import io.github.atkawa7.httpsnippet.generators.node.NodeRequest;
import io.github.atkawa7.httpsnippet.generators.node.NodeUnirest;
import io.github.atkawa7.httpsnippet.generators.objc.ObjNSURLSession;
import io.github.atkawa7.httpsnippet.generators.powershell.PowerShell;
import io.github.atkawa7.httpsnippet.generators.python.Python3Native;
import io.github.atkawa7.httpsnippet.generators.python.PythonRequests;
import io.github.atkawa7.httpsnippet.generators.ruby.RubyNative;
import io.github.atkawa7.httpsnippet.generators.shell.Curl;
import io.github.atkawa7.httpsnippet.generators.swift.Swift;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.HttpSnippet;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;

/**
 * An http snippet the list of available codeGenerators class that relies on the popular <a
 * href="http://www.softwareishard.com/blog/har-12-spec/#request">Har</a> format to create code
 * snippets. This converter supports many languages and tools including: {@linkplain
 * Curl#code(HarRequest) cURL}, HTTPie, Javascript, Node, C, Java, PHP, Objective-C, Swift, Python,
 * Ruby, C#, Go, OCaml and more!
 */
public class HttpSnippetCodeGenerator {
  private final List<CodeGenerator> codeGenerators;
  private final List<Language> languages;

  public HttpSnippetCodeGenerator() {
    this(defaultGenerators());
  }

  public HttpSnippetCodeGenerator(@NonNull CodeGenerator... codeGenerators) {
    this(Arrays.asList(codeGenerators));
  }

  public HttpSnippetCodeGenerator(@NonNull final List<CodeGenerator> codeGenerators) {
    Collections.sort(codeGenerators, Comparator.comparing(o -> o.getDisplayName()));
    this.codeGenerators = Collections.unmodifiableList(codeGenerators);
    this.languages = Collections.unmodifiableList(Arrays.asList(Language.values()));
  }

  private static List<CodeGenerator> defaultGenerators() {
    List<CodeGenerator> codeGenerators = new ArrayList<>();
    codeGenerators.add(new OkHttp());
    codeGenerators.add(new Unirest());
    codeGenerators.add(new LibCurl());
    codeGenerators.add(new RestSharp());
    codeGenerators.add(new GoNative());
    codeGenerators.add(new JQuery());
    codeGenerators.add(new XMLHttpRequest());
    codeGenerators.add(new NodeNative());
    codeGenerators.add(new NodeRequest());
    codeGenerators.add(new NodeUnirest());
    codeGenerators.add(new Python3Native());
    codeGenerators.add(new PythonRequests());
    codeGenerators.add(new RubyNative());
    codeGenerators.add(new Curl());
    codeGenerators.add(new CljHttp());
    codeGenerators.add(new PowerShell());
    codeGenerators.add(new Jsoup());
    codeGenerators.add(new Fetch());
    codeGenerators.add(new Swift());
    codeGenerators.add(new ObjNSURLSession());
    return codeGenerators;
  }

  /**
   * @param harRequest The object contains detailed info about performed request.
   * @param language The target programming language
   * @param client The target client for the target programming language
   * @return The http snippet for a target. The target is searched from a list of available targets
   *     {@link #findGenerator(String, String) findGenerator} using language and client
   * @throws Exception throws exception
   */
  public HttpSnippet snippet(
      @NonNull final HarRequest harRequest,
      @NonNull final String language,
      @NonNull final String client)
      throws Exception {
    CodeGenerator codeGenerator = this.findGenerator(language, client);
    return this.convert(harRequest, codeGenerator);
  }

  /**
   * @param harRequest The object contains detailed info about performed request.
   * @param language The target programming language
   * @return The http snippet for a code generator. Finds {@link #findLanguage(String) language} and
   *     uses the {@link Language#getDefaultClient() default client} for a language to search
   *     through the list of available code generators.
   * @throws Exception throws Exception
   */
  public HttpSnippet snippet(@NonNull final HarRequest harRequest, @NonNull final String language)
      throws Exception {
    Language lang = this.findLanguage(language);
    return this.snippet(harRequest, lang);
  }

  /**
   * @param harRequest The object contains detailed info about performed request.
   * @param language The target programming language
   * @param client The target client for the target programming language
   * @return The http snippet for a target. The target is searched from a list of available targets
   *     {@link #findGenerator(String, String) findGenerator} using language and client
   * @throws Exception throws Exception
   */
  public HttpSnippet snippet(
      @NonNull final HarRequest harRequest,
      @NonNull final Language language,
      @NonNull final Client client)
      throws Exception {
    CodeGenerator codeGenerator = this.findGenerator(language, client);
    return this.convert(harRequest, codeGenerator);
  }

  /**
   * @param harRequest The object contains detailed info about performed request.
   * @param language The target programming language
   * @return The http snippet for a code generator. Uses the {@link Language#getDefaultClient()
   *     default client} for a language to search through the list of available code generators.
   * @throws Exception throws Exception
   */
  public HttpSnippet snippet(@NonNull final HarRequest harRequest, @NonNull final Language language)
      throws Exception {
    Client client = language.getDefaultClient();
    CodeGenerator codeGenerator = this.findGenerator(language, client);
    return this.convert(harRequest, codeGenerator);
  }

  /**
   * @param harRequest The object contains detailed info about performed request.
   * @return The list of http snippets using generators
   * @throws Exception when fails to convert post data to json
   */
  public List<HttpSnippet> snippets(@NonNull final HarRequest harRequest) throws Exception {
    CodeRequest codeRequest = CodeRequest.newCodeRequest(harRequest);

    List<HttpSnippet> httpSnippets = new ArrayList<>(codeGenerators.size());
    for (CodeGenerator codeGenerator : codeGenerators) {

      httpSnippets.add(this.convert(codeRequest, codeGenerator));
    }
    return httpSnippets;
  }

  private HttpSnippet convert(
      @NonNull final HarRequest harRequest, @NonNull final CodeGenerator codeGenerator)
      throws Exception {
    return this.convert(CodeRequest.newCodeRequest(harRequest), codeGenerator);
  }

  private HttpSnippet convert(
      @NonNull final CodeRequest codeRequest, @NonNull final CodeGenerator codeGenerator)
      throws Exception {
    return HttpSnippet.builder()
        .client(codeGenerator.getClient())
        .displayName(codeGenerator.getDisplayName())
        .language(codeGenerator.getLanguage())
        .code(codeGenerator.generateCode(codeRequest))
        .build();
  }

  private CodeGenerator findGenerator(
      @NonNull final Language language, @NonNull final Client client) throws Exception {
    return this.findGenerator(language.getTitle(), client.getTitle());
  }

  private CodeGenerator findGenerator(@NonNull final String language, @NonNull final String client)
      throws Exception {
    return this.codeGenerators.stream()
        .filter(
            t ->
                t.getClient().getTitle().equalsIgnoreCase(client)
                    && t.getLanguage().getTitle().equalsIgnoreCase(language))
        .findFirst()
        .orElseThrow(
            () ->
                new Exception(
                    String.format("CodeGenerator (%s, %s) not supported", client, language)));
  }

  private Language findLanguage(@NonNull String name) throws Exception {
    return this.languages.stream()
        .filter(l -> l.getTitle().equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(() -> new Exception(String.format("Language (%s) not supported", name)));
  }
}
