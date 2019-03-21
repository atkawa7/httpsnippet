var http = require("http");

var options = {"path":"/har","headers":{"Cookie":"foo=bar;bar=baz","content-type":"application/x-www-form-urlencoded","accept":"application/json"},"hostname":"mockbin.com","method":"POST","port":80};

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

var qs = require("querystring");
req.write(qs.stringify({"foo":"bar"}));
req.end();