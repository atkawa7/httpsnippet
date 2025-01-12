var fs = require("fs")
var http = require("http");
var FormData = require("form-data");
var form = new FormData()
form.append("foo", fs.createReadStream("hello.txt"));
let headers = form.getHeaders();

var options = {
  "method": "POST",
  "hostname": "mockbin.com",
  "port": 80,
  "path": "/har",
  "headers": headers
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

form.pipe(req);
