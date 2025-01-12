CURL *hnd = curl_easy_init();

curl_easy_setopt(hnd, CURLOPT_URL, "http://mockbin.com/har");

struct curl_slist *headers = NULL;
headers = curl_slist_append(headers, "content-type: multipart/form-data");
curl_easy_setopt(hnd, CURLOPT_HTTPHEADER, headers);

struct curl_httppost* post = NULL;
struct curl_httppost* last = NULL;

curl_formadd(&post, &last, CURLFORM_COPYNAME, "foo", CURLFORM_FILE, "hello.txt", CURLFORM_END)

curl_easy_setopt(hnd, CURLOPT_HTTPPOST, post)

CURLcode ret = curl_easy_perform(hnd);
