String response = Jsoup.connect("http://mockbin.com/har")
	.method(Method.GET)
	.execute().body();