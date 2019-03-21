var settings = {"async":true,"headers":{"Cookie":"foo=bar;bar=baz"},"method":"POST","crossDomain":true,"url":"http://mockbin.com/har"}

$.ajax(settings).done(function (response) {
  console.log(response);
});