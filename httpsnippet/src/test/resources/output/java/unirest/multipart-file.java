HttpResponse<String> response = Unirest.post("http://mockbin.com/har")
  .header("content-type", "multipart/form-data")
  .asString();