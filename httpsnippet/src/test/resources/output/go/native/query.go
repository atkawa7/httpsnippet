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

	req, _ := http.NewRequest("GET", url, nil)

	res, _ := client.Do(req)

	defer res.Body.Close()
	body, _ := ioutil.ReadAll(res.Body)

	fmt.Println(res)
	fmt.Println(string(body))

}