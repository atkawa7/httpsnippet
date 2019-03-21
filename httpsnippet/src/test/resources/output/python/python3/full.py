import http.client

conn = http.client.HTTPConnection("mockbin.com", "80")

payload = {"foo":"bar"}

headers = {
	"Cookie": "foo=bar;bar=baz","content-type": "application/x-www-form-urlencoded","accept": "application/json"
}

conn.request("POST", "/har", payload, headers)

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))