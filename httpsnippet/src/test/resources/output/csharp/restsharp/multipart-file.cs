var client = new RestClient("http://mockbin.com/har");
var request = new RestRequest(Method.POST);
request.AddHeader("content-type", "multipart/form-data");
IRestResponse response = client.Execute(request);