String response = Jsoup.connect("http://mockbin.com/har")
	.method(Method.GET)
	.header("x-foo", "Bar")
	.header("accept", "application/json")
	.execute().body();