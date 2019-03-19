package io.github.atkawa7.httpsnippet.models;

public enum Language {
    RUBY("ruby", "Ruby", ".rb", Client.RUBY),
    PYTHON("python", "Python", ".py", Client.PYTHON3),
    PHP("php", "PHP", ".php", Client.PHP_CURL),
    OCAML("ocaml", "OCaml", ".ml", Client.COHTTP),
    OBJECTIVE_C("objc", "Objective-C", ".m", Client.OBJECTIVE_C),
    NODE("node", "Node.js", ".js", Client.NODE),
    JAVASCRIPT("javascript", "JavaScript", ".js", Client.XHR),
    GO("go", "Go", ".go", Client.GO),
    CSHARP("csharp", "C#", ".cs", Client.RESTSHARP),
    CLOJURE("clojure", "Clojure", ".clj", Client.CJ_HTTP),
    C("c", "C", ".c", Client.LIBCURL),
    JAVA("java", "Java", ".java", Client.UNIREST),
    SHELL("shell", "Shell", ".sh", Client.CURL),
    POWERSHELL("powershell", "Powershell", ".ps1", Client.WEBREQUEST),
    SWIFT("swift", "Swift", ".swift", Client.SWIFT);
    private final String key;
    private final String title;
    private final String extname;
    private final Client defaultClient;

    Language(String key, String title, String extname, Client defaultClient) {
        this.key = key;
        this.title = title;
        this.extname = extname;
        this.defaultClient = defaultClient;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getExtname() {
        return extname;
    }

    public Client getDefaultClient() {
        return defaultClient;
    }
}
