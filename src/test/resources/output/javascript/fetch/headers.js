const fetchOptions = {
  "mode": "cors",
  "method": "GET",
  "headers": {
    "x-foo": "Bar",
    "accept": "application/json"
  }
}

fetch("http://mockbin.com/har", fetchOptions)
 .then(response => response.json())
 .then(data => console.log(data))
 .catch(error => console.log(error));