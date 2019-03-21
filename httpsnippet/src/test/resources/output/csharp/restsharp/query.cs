var client = new RestClient("http://mockbin.com/har?key=value");
var request = new RestRequest(Method.GET);
IRestResponse response = client.Execute(request);