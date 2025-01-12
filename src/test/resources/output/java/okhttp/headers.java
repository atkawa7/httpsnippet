OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("http://mockbin.com/har")
  .get()
  .addHeader("x-foo", "Bar")
  .addHeader("accept", "application/json")
  .build();

Response response = client.newCall(request).execute();
