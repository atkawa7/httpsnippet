import http.client

conn = http.client.HTTPSConnection("mockbin.com", "80")

conn.request("GET", "/har")

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))