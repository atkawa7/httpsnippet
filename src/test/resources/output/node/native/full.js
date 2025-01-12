var qs = require("querystring");
var http = require("http");

var options = {
  "method": "POST",
  "hostname": "mockbin.com",
  "port": 80,
  "path": "/har?baz=abc&foo=bar&foo=baz&key=value",
  "headers": {
    "Cookie": "foo=bar; bar=baz",
    "content-type": "application/x-www-form-urlencoded",
    "accept": "application/json"
  }
};

var req = http.request(options, function (res) {
  var chunks = [];

  res.on("data", function (chunk) {
    chunks.push(chunk);
  });

  res.on("end", function () {
    var body = Buffer.concat(chunks);
    console.log(body.toString());
  });
});

req.write(qs.stringify({"foo":"bar"}));
req.end();
