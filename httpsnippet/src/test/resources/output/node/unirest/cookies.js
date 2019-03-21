var unirest = require("unirest");

var req = unirest("POST", "http://mockbin.com/har");

var CookieJar = unirest.jar();
CookieJar.add("foo=bar","null");
CookieJar.add("bar=baz","null");
req.jar(CookieJar);


req.end(function (res) {
  if (res.error) throw new Error(res.error);

  console.log(res.body);
});
