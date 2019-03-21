var settings = {"async":true,"headers":{"content-type":"application/json"},"processData":false,"method":"POST","data":"{\"foo\":null}","crossDomain":true,"url":"http://mockbin.com/har"}

$.ajax(settings).done(function (response) {
  console.log(response);
});