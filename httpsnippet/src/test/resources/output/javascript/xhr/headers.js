var xhr = new XMLHttpRequest();
xhr.withCredentials = true;

xhr.addEventListener('readystatechange', function () {
  if (this.readyState === this.DONE) {
    console.log(this.responseText);
  }
});

xhr.open("GET", "http://mockbin.com/har");
xhr.setRequestHeader("x-foo", "Bar");
xhr.setRequestHeader("accept", "application/json");

xhr.send(data);