package io.github.atkawa7.httpsnippet;

import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.generators.c.LibCurl;
import io.github.atkawa7.httpsnippet.generators.csharp.RestSharp;
import io.github.atkawa7.httpsnippet.generators.go.GoNative;
import io.github.atkawa7.httpsnippet.generators.java.OkHttp;
import io.github.atkawa7.httpsnippet.generators.java.Unirest;
import io.github.atkawa7.httpsnippet.generators.javascript.JQuery;
import io.github.atkawa7.httpsnippet.generators.javascript.XMLHttpRequest;
import io.github.atkawa7.httpsnippet.generators.node.NodeNative;
import io.github.atkawa7.httpsnippet.generators.node.NodeRequest;
import io.github.atkawa7.httpsnippet.generators.node.NodeUnirest;
import io.github.atkawa7.httpsnippet.generators.objc.ObjNSURLSession;
import io.github.atkawa7.httpsnippet.generators.python.Python3Native;
import io.github.atkawa7.httpsnippet.generators.python.PythonRequests;
import io.github.atkawa7.httpsnippet.generators.ruby.RubyNative;
import io.github.atkawa7.httpsnippet.generators.shell.Curl;
import lombok.NonNull;

import java.util.*;

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
        this(new ArrayList<>());

        this.add(new OkHttp());
        this.add(new Unirest());

        this.add(new LibCurl());

        this.add(new RestSharp());

        this.add(new GoNative());

        this.add(new JQuery());
        this.add(new XMLHttpRequest());

        this.add(new NodeNative());
        this.add(new NodeRequest());
        this.add(new NodeUnirest());

        this.add(new ObjNSURLSession());

        this.add(new Python3Native());
        this.add(new PythonRequests());

        this.add(new RubyNative());

        this.add(new Curl());

        Collections.sort(codeGenerators, Comparator.comparing(o -> o.getClient().getTitle()));
    }

    public HttpSnippetCodeGenerator(@NonNull final List<CodeGenerator> codeGenerators) {
        this.codeGenerators = codeGenerators;
        this.languages = Collections.unmodifiableList(Arrays.asList(Language.values()));
    }

    public final boolean add(@NonNull final CodeGenerator codeGenerator) {
        return this.codeGenerators.add(codeGenerator);
    }

    public List<HttpSnippet> snippets(@NonNull final HarRequest harRequest) throws Exception {
        return this.snippets(harRequest, this.codeGenerators);
    }

    public List<HttpSnippet> snippets(
            @NonNull final HarRequest harRequest, @NonNull final List<CodeGenerator> codeGenerators)
            throws Exception {
        List<HttpSnippet> httpSnippets = new ArrayList<>(codeGenerators.size());

        for (CodeGenerator codeGenerator : codeGenerators) {
            String snippet = codeGenerator.code(harRequest);
            HttpSnippet httpSnippet =
                    HttpSnippet.builder()
                            .client(codeGenerator.getClient())
                            .language(codeGenerator.getLanguage())
                            .code(snippet)
                            .build();
            httpSnippets.add(httpSnippet);
        }
        return httpSnippets;
    }

    public CodeGenerator findGenerator(@NonNull final Language language, @NonNull final Client client)
            throws Exception {
        return this.findGenerator(language.getTitle(), client.getTitle());
    }

    public CodeGenerator findGenerator(@NonNull final String language, @NonNull final String client)
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

    public Language findLanguage(@NonNull String name) throws Exception {
        return this.languages.stream()
                .filter(l -> l.getTitle().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new Exception(String.format("Language (%s) not supported", name)));
    }

    /**
     * @param harRequest The object contains detailed info about performed request.
     * @param language   The target programming language
     * @param client     The target client for the target programming language
     * @return The http snippet for a target. The target is searched from a list of available targets
     * {@link #findGenerator(String, String) findGenerator} using language and client
     * @throws Exception throws exception
     */
    public HttpSnippet snippet(
            @NonNull final HarRequest harRequest,
            @NonNull final String language,
            @NonNull final String client)
            throws Exception {
        CodeGenerator codeGenerator = this.findGenerator(language, client);
        return this.snippet(harRequest, codeGenerator);
    }

    /**
     * @param harRequest The object contains detailed info about performed request.
     * @param language   The target programming language
     * @param client     The target client for the target programming language
     * @return The http snippet for a target. The target is searched from a list of available targets
     * {@link #findGenerator(String, String) findGenerator} using language and client
     * @throws Exception throws Exception
     */
    public HttpSnippet snippet(
            @NonNull final HarRequest harRequest,
            @NonNull final Language language,
            @NonNull final Client client)
            throws Exception {
        CodeGenerator codeGenerator = this.findGenerator(language, client);
        return this.snippet(harRequest, codeGenerator);
    }

    /**
     * @param harRequest The object contains detailed info about performed request.
     * @param language   The target programming language
     * @return The http snippet for a code generator. Finds {@link #findLanguage(String) language} and
     * uses the {@link Language#getDefaultClient() default client} for a language to search
     * through the list of available code generators.
     * @throws Exception throws Exception
     */
    public HttpSnippet snippet(@NonNull final HarRequest harRequest, @NonNull final String language)
            throws Exception {
        Language lang = this.findLanguage(language);
        return this.snippet(harRequest, lang);
    }

    /**
     * @param harRequest The object contains detailed info about performed request.
     * @param language   The target programming language
     * @return The http snippet for a code generator. Uses the {@link Language#getDefaultClient()
     * default client} for a language to search through the list of available code generators.
     * @throws Exception throws Exception
     */
    public HttpSnippet snippet(@NonNull final HarRequest harRequest, @NonNull final Language language)
            throws Exception {
        Client client = language.getDefaultClient();
        CodeGenerator codeGenerator = this.findGenerator(language, client);
        return this.snippet(harRequest, codeGenerator);
    }

    /**
     * @param harRequest    The object contains detailed info about performed request.
     * @param codeGenerator The codeGenerator that processes the request and creates {@link
     *                      CodeGenerator#code(HarRequest) code}
     * @return The http snippet for a given code generator.
     * @throws Exception throws Exception
     */
    public HttpSnippet snippet(
            @NonNull final HarRequest harRequest, @NonNull final CodeGenerator codeGenerator)
            throws Exception {
        return HttpSnippet.builder()
                .client(codeGenerator.getClient())
                .language(codeGenerator.getLanguage())
                .code(codeGenerator.code(harRequest))
                .build();
    }
}
