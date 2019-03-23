const fetchOptions = {
  "mode": "cors",
  "method": "GET",
  "headers": { }
}

fetch("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value", fetchOptions)
 .then(response => response.json())
 .then(data => console.log(data))
 .catch(error => console.log(error));