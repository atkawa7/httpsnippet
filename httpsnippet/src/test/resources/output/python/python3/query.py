import http.client

conn = http.client.HTTPConnection("mockbin.com", "80")

conn.request("GET", "/har")

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))