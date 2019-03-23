HttpResponse<String> response = Unirest.post("http://mockbin.com/har")
  .header("content-type", "multipart/form-data")
  .field("foo", new File("hello.txt"))
  .asString();
