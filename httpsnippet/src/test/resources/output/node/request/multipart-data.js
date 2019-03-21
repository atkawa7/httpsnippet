var fs = require("fs");
var request = require("request");

var options = {"headers":{"content-type":"multipart/form-data"},"method":"POST","formData":{"foo":{"options":{"filename":"hello.txt","contentType":"text/plain"},"value":"fs.createReadStream(\"hello.txt\")"}},"url":"http://mockbin.com/har"};

request(options, function (error, response, body) {
  if (error) throw new Error(error);

  console.log(body);
});
