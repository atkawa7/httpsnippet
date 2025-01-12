let form = new FormData();
form.append("foo", "hello.txt");

const fetchOptions = {
  "mode": "cors",
  "method": "POST",
  "headers": {
    "content-type": "multipart/form-data"
  },
  "body": form
}

fetch("http://mockbin.com/har", fetchOptions)
 .then(response => response.json())
 .then(data => console.log(data))
 .catch(error => console.log(error));