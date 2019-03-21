(require '[clj-http.client :as client])

(client/post "http://mockbin.com/har" {headers {content-type "application/x-www-form-urlencoded" 
                                         } 
                                      :form-params {foo "bar" 
                                          :hello "world" 
                                          } 
                                      })