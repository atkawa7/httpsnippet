HttpResponse<String> response = Unirest.post("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value")
  .header("Cookie", "foo=bar; bar=baz")
  .header("content-type", "application/x-www-form-urlencoded")
  .header("accept", "application/json")
  .field("foo","bar")
  .asString();
