const fetchOptions = {
  "mode": "cors",
  "method": "POST",
  "headers": {
    "Cookie": "foo=bar; bar=baz"
  }
}

fetch("http://mockbin.com/har", fetchOptions)
 .then(response => response.json())
 .then(data => console.log(data))
 .catch(error => console.log(error));