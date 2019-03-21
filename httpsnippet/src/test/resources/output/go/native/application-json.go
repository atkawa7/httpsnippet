package main

import (
	"fmt"
	"time"
	"strings"
	"net/http"
	"io/ioutil"
)

func main() {

	client := http.Client{
		Timeout: time.Duration(10 * time.Second),
	}

	url := "http://mockbin.com/har"

	payload := strings.NewReader("{\"number\":1,\"string\":\"f\\\"oo\",\"arr\":[1,2,3],\"nested\":{\"a\":\"b\"},\"arr_mix\":[1,\"a\",{\"arr_mix_nested\":{}}],\"boolean\":false}")

	req, _ := http.NewRequest("POST", url, payload)

	req.Header.Add("content-type", "application/json")

	res, _ := client.Do(req)

	defer res.Body.Close()
	body, _ := ioutil.ReadAll(res.Body)

	fmt.Println(res)
	fmt.Println(string(body))

}