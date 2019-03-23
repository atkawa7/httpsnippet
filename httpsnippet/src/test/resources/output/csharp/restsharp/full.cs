var client = new RestClient("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value");
var request = new RestRequest(Method.POST);
request.AddHeader("accept", "application/json");
request.AddHeader("content-type", "application/x-www-form-urlencoded");
request.AddCookie("foo", "bar");
request.AddCookie("bar", "baz");
request.AddParameter("application/x-www-form-urlencoded", "foo=bar", ParameterType.RequestBody);
IRestResponse response = client.Execute(request);
