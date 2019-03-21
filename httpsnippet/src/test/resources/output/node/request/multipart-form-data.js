var request = require("request");

var options = {"headers":{"content-type":"multipart/form-data"},"method":"POST","formData":{"foo":"bar"},"url":"http://mockbin.com/har"};

request(options, function (error, response, body) {
  if (error) throw new Error(error);

  console.log(body);
});
