OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://mockbin.com/har?key=value")
  .post(null)
  .addHeader("Cookie", "foo=bar;bar=baz")
  .addHeader("content-type", "application/x-www-form-urlencoded")
  .addHeader("accept", "application/json")
  .build();

Response response = client.newCall(request).execute();