var request = require("request");

var jar = request.jar();
jar.setCookie(request.cookie("foo=bar"), "null");
jar.setCookie(request.cookie("bar=baz"), "null");

var options = {"headers":{"content-type":"application/x-www-form-urlencoded","accept":"application/json"},"qs":{"foo":["bar","baz"],"baz":["abc"]},"method":"POST","jar":jar,"url":"http://mockbin.com/har?key=value","forms":{"foo":"bar"}};

request(options, function (error, response, body) {
  if (error) throw new Error(error);

  console.log(body);
});
