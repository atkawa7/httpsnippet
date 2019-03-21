var settings = {"async":true,"headers":{"content-type":"application/x-www-form-urlencoded"},"method":"POST","crossDomain":true,"body":{"foo":"bar","hello":"world"},"url":"http://mockbin.com/har"}

$.ajax(settings).done(function (response) {
  console.log(response);
});