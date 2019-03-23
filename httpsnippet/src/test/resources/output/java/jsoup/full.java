String response = Jsoup.connect("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value")
	.method(Method.POST)
	.header("Cookie", "foo=bar; bar=baz")
	.header("content-type", "application/x-www-form-urlencoded")
	.header("accept", "application/json")
	.execute().body();