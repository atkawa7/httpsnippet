const fetchOptions = {
  "mode": "cors",
  "method": "POST",
  "headers": {
    "content-type": "text/plain"
  },
  "body": "Hello World"
}

fetch("http://mockbin.com/har", fetchOptions)
 .then(response => response.json())
 .then(data => console.log(data))
 .catch(error => console.log(error));