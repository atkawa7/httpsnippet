var fs = require("fs");
var request = require("request");

var options = {
  "method": "POST",
  "url": "http://mockbin.com/har",
  "headers": {
    "content-type": "multipart/form-data"
  },
  "formData": {
    "foo": {
      "options": {
        "filename": "hello.txt",
        "contentType": "text/plain"
      },
      "value": fs.createReadStream("hello.txt")
    }
  }
};

request(options, function (error, response, body) {
  if (error) throw new Error(error);

  console.log(body);
});
