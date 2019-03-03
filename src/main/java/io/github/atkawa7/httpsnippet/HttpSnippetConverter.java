package io.github.atkawa7.httpsnippet;

import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.target.Target;
import io.github.atkawa7.httpsnippet.target.c.LibCurl;
import io.github.atkawa7.httpsnippet.target.csharp.RestSharp;
import io.github.atkawa7.httpsnippet.target.go.GoNative;
import io.github.atkawa7.httpsnippet.target.java.OkHttp;
import io.github.atkawa7.httpsnippet.target.java.Unirest;
import io.github.atkawa7.httpsnippet.target.javascript.JQuery;
import io.github.atkawa7.httpsnippet.target.javascript.XMLHttpRequest;
import io.github.atkawa7.httpsnippet.target.node.NodeNative;
import io.github.atkawa7.httpsnippet.target.node.NodeRequest;
import io.github.atkawa7.httpsnippet.target.node.NodeUnirest;
import io.github.atkawa7.httpsnippet.target.objc.ObjNSURLSession;
import io.github.atkawa7.httpsnippet.target.python.Python3Native;
import io.github.atkawa7.httpsnippet.target.python.PythonRequests;
import io.github.atkawa7.httpsnippet.target.ruby.RubyNative;
import io.github.atkawa7.httpsnippet.target.shell.Curl;
import lombok.NonNull;

import java.util.*;

/**
 * An http snippet converter class that relies on the popular
 * <a href="http://www.softwareishard.com/blog/har-12-spec/#request">Har</a> format to create code snippets.
 * This converter supports many languages and tools including: {@linkplain Curl#code(HarRequest) cURL},
 * HTTPie, Javascript, Node, C, Java, PHP,
 * Objective-C, Swift, Python, Ruby, C#, Go, OCaml and more!
 */

public class HttpSnippetConverter {
    private final List<Target> targets;
    private final List<Language> languages;

    public HttpSnippetConverter() {
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

        Collections.sort(targets, Comparator.comparing(o -> o.getClient().getTitle()));
    }

    public HttpSnippetConverter(@NonNull final List<Target> targets) {
        this.targets = targets;
        this.languages = Collections.unmodifiableList(Arrays.asList(Language.values()));
    }

    public final boolean add(@NonNull final Target target) {
        return this.targets.add(target);
    }

    public List<HttpSnippet> snippets(@NonNull final HarRequest harRequest) throws Exception {
        return this.snippets(harRequest, this.targets);
    }

    public List<HttpSnippet> snippets(
            @NonNull final HarRequest harRequest, @NonNull final List<Target> targets) throws Exception {
        List<HttpSnippet> httpSnippets = new ArrayList<>(targets.size());

        for (Target target : targets) {
            String snippet = target.code(harRequest);
            HttpSnippet httpSnippet =
                    HttpSnippet.builder()
                            .client(target.getClient())
                            .language(target.getLanguage())
                            .code(snippet)
                            .build();
            httpSnippets.add(httpSnippet);
        }
        return httpSnippets;
    }

    public Target findTarget(@NonNull final Language language, @NonNull final Client client)
            throws Exception {
        return this.findTarget(language.getTitle(), client.getTitle());
    }

    public Target findTarget(@NonNull final String language, @NonNull final String client)
            throws Exception {
        return this.targets.stream()
                .filter(
                        t ->
                                t.getClient().getTitle().equalsIgnoreCase(client)
                                        && t.getLanguage().getTitle().equalsIgnoreCase(language))
                .findFirst()
                .orElseThrow(
                        () -> new Exception(String.format("Target (%s, %s) not supported", client, language)));
    }

    public Language findLanguage(@NonNull String name) throws Exception {
        return this.languages.stream()
                .filter(l -> l.getTitle().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new Exception(String.format("Language (%s) not supported", name)));
    }

    /**
     * @param harRequest  The object contains detailed info about performed request.
     * @param language The target programming language
     * @param client The target client for the target programming language
     * @return The http snippet for a  target. The target is searched from a list of available targets
     * {@link #findTarget(String, String) findTarget} using language and client
     * @throws Exception throws exception
     */
    public HttpSnippet snippet(
            @NonNull final HarRequest harRequest,
            @NonNull final String language,
            @NonNull final String client)
            throws Exception {
        Target target = this.findTarget(language, client);
        return this.snippet(harRequest, target);
    }

    /**
     * @param harRequest The object contains detailed info about performed request.
     * @param language   The target programming language
     * @param client     The target client for the target programming language
     * @return The http snippet for a  target. The target is searched from a list of available targets
     * {@link #findTarget(String, String) findTarget} using language and client
     * @throws Exception throws Exception
     */
    public HttpSnippet snippet(@NonNull final HarRequest harRequest,
                               @NonNull final Language language,
                               @NonNull final Client client)
            throws Exception {
        Target target = this.findTarget(language, client);
        return this.snippet(harRequest, target);
    }

    /**
     * @param harRequest The object contains detailed info about performed request.
     * @param language   The target programming language
     * @return The http snippet for a target. Finds {@link #findLanguage(String) language}  and
     * uses the {@link Language#getDefaultClient() default client} for a language to
     * search through the list of available targets.
     * @throws Exception throws Exception
     */
    public HttpSnippet snippet(@NonNull final HarRequest harRequest, @NonNull final String language)
            throws Exception {
        Language lang = this.findLanguage(language);
        return this.snippet(harRequest, lang);
    }

    /**
     *
     * @param harRequest The object contains detailed info about performed request.
     * @param language The target programming language
     * @return The http snippet for a target. Uses the {@link Language#getDefaultClient() default client}
     * for a language to search through the list of available targets.
     * @throws Exception throws Exception
     */

    public HttpSnippet snippet(@NonNull final HarRequest harRequest, @NonNull final Language language)
            throws Exception {
        Client client = language.getDefaultClient();
        Target target = this.findTarget(language, client);
        return this.snippet(harRequest, target);
    }

    /**
     *
     * @param harRequest The object contains detailed info about performed request.
     * @param target The target that processes the request and creates {@link Target#code(HarRequest) code}
     * @return The http snippet for a given target.
     * @throws Exception throws Exception
     */

    public HttpSnippet snippet(@NonNull final HarRequest harRequest, @NonNull final Target target)
            throws Exception {
        return HttpSnippet.builder()
                .client(target.getClient())
                .language(target.getLanguage())
                .code(target.code(harRequest))
                .build();
    }
}
