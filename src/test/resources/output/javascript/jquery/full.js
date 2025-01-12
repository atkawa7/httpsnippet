var settings = {
  "async": true,
  "crossDomain": true,
  "url": "http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value",
  "method": "POST",
  "headers": {
    "Cookie": "foo=bar; bar=baz",
    "content-type": "application/x-www-form-urlencoded",
    "accept": "application/json"
  },
  "data": {
    "foo": "bar"
  }
}

$.ajax(settings).done(function (response) {
  console.log(response);
});
