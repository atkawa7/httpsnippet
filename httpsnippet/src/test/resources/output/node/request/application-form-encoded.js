var request = require("request");

var options = {"headers":{"content-type":"application/x-www-form-urlencoded"},"method":"POST","url":"http://mockbin.com/har","forms":{"foo":"bar","hello":"world"}};

request(options, function (error, response, body) {
  if (error) throw new Error(error);

  console.log(body);
});
