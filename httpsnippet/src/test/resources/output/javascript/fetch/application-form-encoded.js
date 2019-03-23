const details ={"foo":"bar","hello":"world"};
const form = Object.entries(details)
    .map(([key, value]) => encodeURIComponent(key) + '=' + encodeURIComponent(value))
    .join('&')
const fetchOptions = {
  "mode": "cors",
  "method": "POST",
  "headers": {
    "content-type": "application/x-www-form-urlencoded"
  },
  "body": form
}

fetch("http://mockbin.com/har", fetchOptions)
 .then(response => response.json())
 .then(data => console.log(data))
 .catch(error => console.log(error));