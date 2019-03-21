(require '[clj-http.client :as client])

(client/post "http://mockbin.com/har" {headers {content-type "text/plain" 
                                         } 
                                      :body "Hello World" 
                                      })