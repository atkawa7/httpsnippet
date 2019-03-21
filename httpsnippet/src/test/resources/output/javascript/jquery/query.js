var settings = {"async":true,"headers":{},"method":"GET","crossDomain":true,"url":"http://mockbin.com/har?key=value"}

$.ajax(settings).done(function (response) {
  console.log(response);
});