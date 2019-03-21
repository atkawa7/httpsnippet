OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://mockbin.com/har")
  .post(null)
  .addHeader("Cookie", "foo=bar;bar=baz")
  .build();

Response response = client.newCall(request).execute();