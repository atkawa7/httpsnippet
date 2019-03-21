(require '[clj-http.client :as client])

(client/post "http://mockbin.com/har" {headers {Cookie "foo=bar;bar=baz" 
                                         } 
                                      })