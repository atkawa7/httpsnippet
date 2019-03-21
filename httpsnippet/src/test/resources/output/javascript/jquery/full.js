var settings = {"async":true,"headers":{"Cookie":"foo=bar;bar=baz","content-type":"application/x-www-form-urlencoded","accept":"application/json"},"method":"POST","crossDomain":true,"body":{"foo":"bar"},"url":"http://mockbin.com/har?key=value"}

$.ajax(settings).done(function (response) {
  console.log(response);
});