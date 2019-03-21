#import <Foundation/Foundation.h>

NSDictionary *headers = @{ @"Cookie": @"foo=bar;bar=baz",
   @"content-type": @"application/x-www-form-urlencoded",
   @"accept": @"application/json" };

NSMutableData *postData = [[NSMutableData alloc] initWithData:[@"foo=bar" dataUsingEncoding:NSUTF8StringEncoding]];

NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:@"http://mockbin.com/har?key=value"]
                                                       cachePolicy:NSURLRequestUseProtocolCachePolicy
                                                   timeoutInterval:10];
[request setHTTPMethod:@"POST"];
[request setAllHTTPHeaderFields:headers];
[request setHTTPBody:postData];

NSURLSession *session = [NSURLSession sharedSession];
NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request
                                            completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
                                              if (error) {
                                                NSLog(@"%@", error);
                                              } else {
                                                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
                                                NSLog(@"%@", httpResponse);
                                              }
                                            }];
[dataTask resume];