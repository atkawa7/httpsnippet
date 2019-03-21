String response = Jsoup.connect("http://mockbin.com/har")
	.method(Method.POST)
	.header("content-type", "application/json")
	.requestBody("{\"foo\":null}")