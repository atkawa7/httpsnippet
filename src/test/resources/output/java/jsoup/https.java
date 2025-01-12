String response = Jsoup.connect("https://mockbin.com/har")
	.method(Method.GET)
	.execute().body();