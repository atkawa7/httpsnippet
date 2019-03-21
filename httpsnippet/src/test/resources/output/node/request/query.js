var request = require("request");

var options = {"qs":{"foo":["bar","baz"],"baz":["abc"]},"method":"GET","url":"http://mockbin.com/har?key=value"};

request(options, function (error, response, body) {
  if (error) throw new Error(error);

  console.log(body);
});
