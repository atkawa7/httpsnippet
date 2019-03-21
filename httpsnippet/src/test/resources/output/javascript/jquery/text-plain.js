var settings = {"async":true,"headers":{"content-type":"text/plain"},"method":"POST","data":"Hello World","crossDomain":true,"url":"http://mockbin.com/har"}

$.ajax(settings).done(function (response) {
  console.log(response);
});