var unirest = require("unirest");

var req = unirest("POST", "http://mockbin.com/har?key=value");

var CookieJar = unirest.jar();
CookieJar.add("foo=bar","null");
CookieJar.add("bar=baz","null");
req.jar(CookieJar);

req.query({"foo":["bar","baz"],"baz":["abc"]});

req.headers({"content-type":"application/x-www-form-urlencoded","accept":"application/json"});

req.form({"foo":"bar"});

req.end(function (res) {
  if (res.error) throw new Error(res.error);

  console.log(res.body);
});
