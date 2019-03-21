package main

import (
	"fmt"
	"time"
	"net/http"
	"io/ioutil"
)

func main() {

	client := http.Client{
		Timeout: time.Duration(10 * time.Second),
	}

	url := "http://mockbin.com/har?key=value"

	req, _ := http.NewRequest("POST", url, nil)

	req.Header.Add("Cookie", "foo=bar;bar=baz")
	req.Header.Add("content-type", "application/x-www-form-urlencoded")
	req.Header.Add("accept", "application/json")

	res, _ := client.Do(req)

	defer res.Body.Close()
	body, _ := ioutil.ReadAll(res.Body)

	fmt.Println(res)
	fmt.Println(string(body))

}