var client = new RestClient("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value");
var request = new RestRequest(Method.GET);
IRestResponse response = client.Execute(request);
