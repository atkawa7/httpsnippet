var settings = {
  "async": true,
  "crossDomain": true,
  "url": "http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value",
  "method": "GET",
  "headers": { }
}

$.ajax(settings).done(function (response) {
  console.log(response);
});
