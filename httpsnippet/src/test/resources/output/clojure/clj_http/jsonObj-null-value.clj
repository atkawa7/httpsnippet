(require '[clj-http.client :as client])

(client/post "http://mockbin.com/har" {headers {content-type "application/json" 
                                         } 
                                      :content-type :json 
                                      :form-params "{\"foo\":null}" 
                                      })