import http.client

conn = http.client.HTTPConnection("mockbin.com")

conn.request("GET", "/har?baz=abc&foo=bar&foo=baz&key=value")

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))
