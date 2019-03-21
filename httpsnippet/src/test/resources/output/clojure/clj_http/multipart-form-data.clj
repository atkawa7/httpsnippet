(require '[clj-http.client :as client])

(client/post "http://mockbin.com/har" {headers {content-type "multipart/form-data" 
                                         } 
                                      :multipart [{name "foo" 
                                           :content "bar" 
                                           } ] 
                                      })