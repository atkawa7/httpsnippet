var settings = {
  "async": true,
  "crossDomain": true,
  "url": "http://mockbin.com/har",
  "method": "POST",
  "headers": {
    "Cookie": "foo=bar; bar=baz"
  }
}

$.ajax(settings).done(function (response) {
  console.log(response);
});
