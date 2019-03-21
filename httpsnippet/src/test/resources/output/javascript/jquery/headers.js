var settings = {"async":true,"headers":{"x-foo":"Bar","accept":"application/json"},"method":"GET","crossDomain":true,"url":"http://mockbin.com/har"}

$.ajax(settings).done(function (response) {
  console.log(response);
});