HttpResponse<String> response = Unirest.post("http://mockbin.com/har?key=value")
  .header("Cookie", "foo=bar;bar=baz")
  .header("content-type", "application/x-www-form-urlencoded")
  .header("accept", "application/json")
  .asString();