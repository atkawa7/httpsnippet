$headers=@{}
$headers.Add("content-type", "application/x-www-form-urlencoded")
$response = Invoke-WebRequest -Uri 'http://mockbin.com/har' -Method POST -Headers $headers