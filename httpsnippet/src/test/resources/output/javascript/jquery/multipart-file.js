var form = new FormData();
form.append("foo", "hello.txt");

var settings = {"async":true,"headers":{"content-type":"multipart/form-data"},"processData":false,"method":"POST","data":form,"crossDomain":true,"mimeType":"multipart/form-data","contentType":false,"url":"http://mockbin.com/har"}

$.ajax(settings).done(function (response) {
  console.log(response);
});