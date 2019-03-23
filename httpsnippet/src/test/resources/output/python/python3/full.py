import http.client

conn = http.client.HTTPConnection("mockbin.com")

payload = "foo=bar"

headers = {"Cookie":"foo=bar; bar=baz","content-type":"application/x-www-form-urlencoded","accept":"application/json"}

conn.request("POST", "/har?baz=abc&foo=bar&foo=baz&key=value", payload, headers)

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))
