#import <Foundation/Foundation.h>

NSDictionary *headers = @{ @"Cookie": @"foo=bar;bar=baz" };

NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:@"http://mockbin.com/har"]
                                                       cachePolicy:NSURLRequestUseProtocolCachePolicy
                                                   timeoutInterval:10];
[request setHTTPMethod:@"POST"];
[request setAllHTTPHeaderFields:headers];

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