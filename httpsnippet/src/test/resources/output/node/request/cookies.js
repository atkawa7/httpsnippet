var request = require("request");

var jar = request.jar();
jar.setCookie(request.cookie("foo=bar"), "null");
jar.setCookie(request.cookie("bar=baz"), "null");

var options = {"method":"POST","jar":jar,"url":"http://mockbin.com/har"};

request(options, function (error, response, body) {
  if (error) throw new Error(error);

  console.log(body);
});
