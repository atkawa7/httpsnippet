var request = require("request");

var options = {"headers":{"content-type":"application/json"},"method":"POST","json":true,"body":{"foo":null},"url":"http://mockbin.com/har"};

request(options, function (error, response, body) {
  if (error) throw new Error(error);

  console.log(body);
});
