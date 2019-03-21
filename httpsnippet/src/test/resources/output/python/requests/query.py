import requests

url = "http://mockbin.com/har?key=value"

querystring = {"foo":["bar","baz"],"baz":["abc"]}

response = requests.request("GET", url, params=querystring)

print(response.text)