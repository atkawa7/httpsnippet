HttpResponse<String> response = Unirest.get("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value")
  .asString();
