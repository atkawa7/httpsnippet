HttpResponse<String> response = Unirest.post("http://mockbin.com/har")
  .header("Cookie", "foo=bar;bar=baz")
  .asString();