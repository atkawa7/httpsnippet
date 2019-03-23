curl --request POST \
  --url 'http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value' \
  --header 'accept: application/json' \
  --header 'content-type: application/x-www-form-urlencoded' \
  --cookie 'foo=bar; bar=baz' \
  --data 'foo=bar'