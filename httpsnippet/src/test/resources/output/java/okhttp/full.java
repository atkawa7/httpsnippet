OkHttpClient client = new OkHttpClient();

RequestBody body = new FormBody.Builder()
        .add("foo", "bar")
        .build();
Request request = new Request.Builder()
  .url("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value")
  .post(body)
  .addHeader("Cookie", "foo=bar; bar=baz")
  .addHeader("content-type", "application/x-www-form-urlencoded")
  .addHeader("accept", "application/json")
  .build();

Response response = client.newCall(request).execute();
