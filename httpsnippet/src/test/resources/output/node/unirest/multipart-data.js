var fs = require("fs");
var unirest = require("unirest");

var req = unirest("POST", "http://mockbin.com/har");

req.headers({
  "content-type": "multipart/form-data"
});

req.multipart([
  {
    "content-type": "text/plain",
    "body": fs.createReadStream("hello.txt")
  }
]);

req.end(function (res) {
  if (res.error) throw new Error(res.error);

  console.log(res.body);
});
