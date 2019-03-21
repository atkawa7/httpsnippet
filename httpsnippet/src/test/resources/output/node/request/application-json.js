var request = require("request");

var options = {"headers":{"content-type":"application/json"},"method":"POST","json":true,"body":{"number":1,"string":"f\"oo","arr":[1,2,3],"nested":{"a":"b"},"arr_mix":[1,"a",{"arr_mix_nested":{}}],"boolean":false},"url":"http://mockbin.com/har"};

request(options, function (error, response, body) {
  if (error) throw new Error(error);

  console.log(body);
});
