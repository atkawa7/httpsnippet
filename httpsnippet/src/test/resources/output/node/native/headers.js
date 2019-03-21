var http = require("http");

var options = {"path":"/har","headers":{"x-foo":"Bar","accept":"application/json"},"hostname":"mockbin.com","method":"GET","port":80};

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

req.end();