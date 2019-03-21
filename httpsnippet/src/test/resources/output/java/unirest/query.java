HttpResponse<String> response = Unirest.get("http://mockbin.com/har?key=value")
  .asString();