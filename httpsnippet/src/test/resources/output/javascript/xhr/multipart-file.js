var data = new FormData();
data.append("hello.txt", "hello.txt");

var xhr = new XMLHttpRequest();
xhr.withCredentials = true;

xhr.addEventListener('readystatechange', function () {
  if (this.readyState === this.DONE) {
    console.log(this.responseText);
  }
});

xhr.open("POST", "http://mockbin.com/har");
xhr.setRequestHeader("content-type", "multipart/form-data");

xhr.send(data);