use serde_json::json;
use reqwest;

#[tokio::main]
pub async fn main() {
    let url = "http://mockbin.com/har";

    let payload = "Hello World";

    let mut headers = reqwest::header::HeaderMap::new();
    headers.insert("content-type", "text/plain".parse().unwrap());

    let client = reqwest::Client::new();
    let response = client.post(url)
        .headers(headers)
        .body(payload)
        .send()
        .await;

    let results = response.unwrap()
        .json::<serde_json::Value>()
        .await
        .unwrap();

    dbg!(results);
}
