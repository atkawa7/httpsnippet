var http = require("http");

var options = {
  "method": "POST",
  "hostname": "mockbin.com",
  "port": 80,
  "path": "/har",
  "headers": {
    "content-type": "application/json"
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

req.write(JSON.stringify({"foo":null}));
req.end();
