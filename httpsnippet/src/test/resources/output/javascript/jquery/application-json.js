var settings = {"async":true,"headers":{"content-type":"application/json"},"processData":false,"method":"POST","data":"{\"number\":1,\"string\":\"f\\\"oo\",\"arr\":[1,2,3],\"nested\":{\"a\":\"b\"},\"arr_mix\":[1,\"a\",{\"arr_mix_nested\":{}}],\"boolean\":false}","crossDomain":true,"url":"http://mockbin.com/har"}

$.ajax(settings).done(function (response) {
  console.log(response);
});