(require '[clj-http.client :as client])

(client/post "http://mockbin.com/har?key=value" {headers {Cookie "foo=bar;bar=baz" 
                                                   :content-type "application/x-www-form-urlencoded" 
                                                   :accept "application/json" 
                                                   } 
                                                :form-params {foo "bar" 
                                                    } 
                                                :query-params {foo ["bar" "baz" ] 
                                                     :baz ["abc" ] 
                                                     } 
                                                :accept :json 
                                                })