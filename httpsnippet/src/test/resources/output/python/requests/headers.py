import requests

url = "http://mockbin.com/har"

headers = {
	"x-foo": "Bar","accept": "application/json"
}

response = requests.request("GET", url, headers=headers)

print(response.text)