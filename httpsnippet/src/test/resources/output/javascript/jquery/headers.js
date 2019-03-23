var settings = {
  "async": true,
  "crossDomain": true,
  "url": "http://mockbin.com/har",
  "method": "GET",
  "headers": {
    "x-foo": "Bar",
    "accept": "application/json"
  }
}

$.ajax(settings).done(function (response) {
  console.log(response);
});
