var http = require("http");

var options = {"path":"/har","headers":{"content-type":"application/json"},"hostname":"mockbin.com","method":"POST","port":80};

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

req.write("{\"foo\":null}");
req.end();