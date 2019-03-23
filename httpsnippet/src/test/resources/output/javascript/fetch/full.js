const details ={"foo":"bar"};
const form = Object.entries(details)
    .map(([key, value]) => encodeURIComponent(key) + '=' + encodeURIComponent(value))
    .join('&')
const fetchOptions = {
  "mode": "cors",
  "method": "POST",
  "headers": {
    "Cookie": "foo=bar; bar=baz",
    "content-type": "application/x-www-form-urlencoded",
    "accept": "application/json"
  },
  "body": form
}

fetch("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value", fetchOptions)
 .then(response => response.json())
 .then(data => console.log(data))
 .catch(error => console.log(error));