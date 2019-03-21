String response = Jsoup.connect("http://mockbin.com/har")
	.method(Method.POST)
	.header("Cookie", "foo=bar;bar=baz")
	.execute().body();