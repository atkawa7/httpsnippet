OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value")
  .get()
  .build();

Response response = client.newCall(request).execute();
