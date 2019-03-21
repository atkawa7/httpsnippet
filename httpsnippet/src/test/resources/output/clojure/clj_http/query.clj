(require '[clj-http.client :as client])

(client/get "http://mockbin.com/har?key=value" {query-params {foo ["bar" "baz" ] 
                                                  :baz ["abc" ] 
                                                  } 
                                               })