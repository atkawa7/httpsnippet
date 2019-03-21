String response = Jsoup.connect("http://mockbin.com/har?key=value")
	.method(Method.GET)
	.execute().body();