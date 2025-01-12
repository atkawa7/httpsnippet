require 'uri'
require 'net/http'

url = URI("http://mockbin.com/har?baz=abc&foo=bar&foo=baz&key=value")

http = Net::HTTP.new(url.host, url.port)

request = Net::HTTP::Post.new(url)
request["Cookie"] = "foo=bar; bar=baz"
request["content-type"] = "application/x-www-form-urlencoded"
request["accept"] = "application/json"
request.body = "foo=bar"

response = http.request(request)
puts response.read_body
