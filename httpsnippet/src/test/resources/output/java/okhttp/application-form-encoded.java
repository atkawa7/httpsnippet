OkHttpClient client = new OkHttpClient();

RequestBody body = new FormBody.Builder()
        .add("foo", "bar")
        .add("hello", "world")
        .build();
Request request = new Request.Builder()
  .url("http://mockbin.com/har")
  .post(body)
  .addHeader("content-type", "application/x-www-form-urlencoded")
  .build();

Response response = client.newCall(request).execute();
