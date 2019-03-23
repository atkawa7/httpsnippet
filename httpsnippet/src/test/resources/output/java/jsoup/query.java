String response = Jsoup.connect("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value")
	.method(Method.GET)
	.execute().body();