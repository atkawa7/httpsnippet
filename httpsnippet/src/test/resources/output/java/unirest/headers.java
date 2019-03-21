HttpResponse<String> response = Unirest.get("http://mockbin.com/har")
  .header("x-foo", "Bar")
  .header("accept", "application/json")
  .asString();