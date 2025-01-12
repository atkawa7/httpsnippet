var client = new RestClient("http://mockbin.com/har");
var request = new RestRequest(Method.POST);
request.AddHeader("content-type", "multipart/form-data");
request.AddFile("foo", "hello.txt");
IRestResponse response = client.Execute(request);
