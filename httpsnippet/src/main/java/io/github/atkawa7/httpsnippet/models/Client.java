package io.github.atkawa7.httpsnippet.models;

public enum Client {
    CJ_HTTP(
            "clj_http",
            "Clj-http",
            "https://github.com/dakrone/clj-http",
            "An idiomatic clojure http client wrapping the apache client."),
    GO(
            "native",
            "NewRequest",
            "http://golang.org/pkg/net/http/#NewRequest",
            "Golang HTTP client request"),
    JQUERY(
            "jquery",
            "JQuery",
            "http://api.jquery.com/jquery.ajax/",
            "Perform an asynchronous HTTP (Ajax) requests with JQuery"),
    XHR(
            "xhr",
            "XMLHttpRequest",
            "https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest",
            "W3C Standard API that provides scripted client functionality"),
    NODE_UNIREST(
            "unirest",
            "Unirest",
            "http://unirest.io/nodejs.html",
            "Lightweight HTTP Request Client Library"),
    NODE(
            "native",
            "HTTP",
            "http://nodejs.org/api/http.html#http_http_request_options_callback",
            "Node.js native HTTP interface"),
    NODE_REQUEST(
            "request", "Request", "https://github.com/request/request", "Simplified HTTP request client"),
    OBJECTIVE_C(
            "nsurlsession",
            "NSURLSession",
            "https://developer.apple.com/library/mac/documentation/Foundation/Reference/NSURLSession_class/index.html",
            "Foundation's NSURLSession request"),
    COHTTP(
            "cohttp",
            "CoHTTP",
            "https://github.com/mirage/ocaml-cohttp",
            "Cohttp is a very lightweight HTTP server using Lwt or Async for OCaml"),
    PHP_HTTP2(
            "http2", "HTTP v2", "http://devel-m6w6.rhcloud.com/mdref/http", "PHP with pecl/http v2"),
    PHP_HTTP1("http1", "HTTP v1", "http://php.net/manual/en/book.http.php", "PHP with pecl/http v1"),
    PHP_CURL("curl", "cURL", "http://php.net/manual/en/book.curl.php", "PHP with ext-curl"),
    PYTHON_REQUESTS(
            "requests",
            "Requests",
            "http://docs.python-requests.org/en/latest/api/#requests.request",
            "Requests HTTP library"),
    PYTHON3(
            "python3",
            "http.client",
            "https://docs.python.org/3/library/http.client.html",
            "Python3 HTTP Client"),
    CURL(
            "curl",
            "cURL",
            "http://curl.haxx.se/",
            "cURL is a command line tool and library for transferring data with URL syntax"),
    HTTPIE("httpie", "HTTPie", "http://httpie.org/", "a CLI, cURL-like tool for humans"),
    WGET(
            "wget",
            "Wget",
            "https://www.gnu.org/software/wget/",
            "a free software package for retrieving files using HTTP, HTTPS"),
    SWIFT(
            "nsurlsession",
            "NSURLSession",
            "https://developer.apple.com/library/mac/documentation/Foundation/Reference/NSURLSession_class/index.html",
            "Foundation's NSURLSession request"),
    RUBY(
            "net::http",
            "Net::http",
            "http://ruby-doc.org/stdlib-2.2.1/libdoc/net/http/rdoc/Net/HTTP.html",
            "Ruby HTTP client"),
    UNIREST(
            "unirest",
            "Unirest",
            "http://unirest.io/java.html",
            "Lightweight HTTP Request Client Library"),
    RESTSHARP(
            "restsharp",
            "RestSharp",
            "http://restsharp.org/",
            "Simple REST and HTTP API Client for .NET"),
    LIBCURL(
            "libcurl",
            "Libcurl",
            "http://curl.haxx.se/libcurl/",
            "Simple REST and HTTP API Client for C"),
    OKHTTP("okhttp", "OkHttp", "http://square.github.io/okhttp/", "An HTTP Request Client Library"),
    WEBREQUEST(
            "webrequest",
            "Invoke-WebRequest",
            "https://docs.microsoft.com/en-us/powershell/module/Microsoft.PowerShell.Utility/Invoke-WebRequest",
            "Powershell Invoke-WebRequest client"),
    JSOUP(
            "jsoup",
            "JSoup",
            "http://jsoup.org/",
            "JSoup Java HTML Parser, with best of DOM, CSS, and jquery");

    private final String key;
    private final String title;
    private final String link;
    private final String description;

    Client(String key, String title, String link, String description) {
        this.key = key;
        this.title = title;
        this.link = link;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }
}
