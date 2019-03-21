var settings = {"async":true,"headers":{},"method":"GET","crossDomain":true,"url":"https://mockbin.com/har"}

$.ajax(settings).done(function (response) {
  console.log(response);
});