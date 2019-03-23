OkHttpClient client = new OkHttpClient();

RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("foo", "hello.txt",
                RequestBody.create(MediaType.parse("text/plain"), new File("hello.txt")))
        .build()
Request request = new Request.Builder()
  .url("http://mockbin.com/har")
  .post(body)
  .addHeader("content-type", "multipart/form-data")
  .build();

Response response = client.newCall(request).execute();
