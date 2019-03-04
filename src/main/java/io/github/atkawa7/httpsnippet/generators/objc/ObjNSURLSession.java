package io.github.atkawa7.httpsnippet.generators.objc;

import com.smartbear.har.model.HarParam;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjNSURLSession extends CodeGenerator {
    private int timeout;

    public ObjNSURLSession() {
        super(Client.OBJECTIVE_C, Language.OBJECTIVE_C);
        this.timeout = 10;
    }

    public String blankString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(CodeBuilder.EMPTY);
        }

        return builder.toString();
    }

    public String nsDeclaration(String nsClass, String name, Object parameters, Integer indent) {
        String opening = nsClass + " *" + name + " = ";
        String literal =
                this.literalRepresentation(parameters, indent != null ? opening.length() : null);
        return opening + literal + ";";
    }

    public <T> String literalRepresentation(T value, Integer indentation) {
        String join = indentation == null ? ", " : ",\n   " + this.blankString(indentation);
        if (value instanceof Number) {
            return '@' + ((Number) value).toString();
        } else if (value instanceof List) {
            List list = (List) value;
            List<String> listBuilder = new ArrayList<>();
            for (Object obj : list) {
                listBuilder.add(this.literalRepresentation(obj, null));
            }
            return "@[ " + String.join(join, listBuilder) + " ]";
        } else if (value instanceof Map) {
            Map<Object, Object> map = (Map) value;
            List<String> listBuilder = new ArrayList<>();

            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                String format =
                        String.format(
                                "@\"%s\": %s", entry.getKey(), this.literalRepresentation(entry.getValue(), null));
                listBuilder.add(format);
            }

            return "@{ " + String.join(join, listBuilder) + " }";

        } else if (value instanceof Boolean) {
            Boolean bool = (Boolean) value;
            return bool ? "@YES" : "@NO";
        } else {
            return String.format("@\"%s\"", ObjectUtils.defaultIfNull(value, ""));
        }
    }

    @Override
    protected String generateCode(final HarRequest harRequest) throws Exception {

        CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

        boolean hasHeaders = false;
        boolean hasBody = false;

        // We just want to make sure people understand that is the only dependency
        code.push("#import <Foundation/Foundation.h>");

        Map<String, String> allHeaders = asHeaders(harRequest);

        if (ObjectUtils.isNotEmpty(allHeaders)) {
            hasHeaders = true;
            code.blank().push(nsDeclaration("NSDictionary", "headers", asHeaders(harRequest), 1));
        }

        HarPostData postData = harRequest.getPostData();
        if (hasText(postData)) {
            hasBody = true;
            List<HarParam> params = new ArrayList<>();
            String mimeType = this.getMimeType(postData);
            switch (mimeType) {
                case MediaType.APPLICATION_FORM_URLENCODED:
                    if (ObjectUtils.isNotEmpty(params)) {
                        code.blank()
                                .push(
                                        "NSMutableData *postData = [[NSMutableData alloc] initWithData:[@\"%s=%s\" dataUsingEncoding:NSUTF8StringEncoding]];",
                                        params.get(0).getName(), params.get(0).getValue());
                        for (int i = 1; i < params.size(); i++) {
                            code.push(
                                    "[postData appendData:[@\"&%s=%s\" dataUsingEncoding:NSUTF8StringEncoding]];",
                                    params.get(0).getName(), params.get(0).getValue());
                        }
                    }
                    break;

                case MediaType.APPLICATION_JSON:
                    if (hasText(postData)) {
                        code.push(nsDeclaration("NSDictionary", "parameters", postData.getText(), 4))
                                .blank()
                                .push(
                                        "NSData *postData = [NSJSONSerialization dataWithJSONObject:parameters options:0 error:nil];");
                    }
                    break;

                default:
                    code.blank()
                            .push(
                                    "NSData *postData = [[NSData alloc] initWithData:[@\""
                                            + postData.getText()
                                            + "\" dataUsingEncoding:NSUTF8StringEncoding]];");
            }
        }

        code.blank()
                .push(
                        "NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:@\""
                                + harRequest.getUrl()
                                + "\"]")
                // NSURLRequestUseProtocolCachePolicy is the default policy, let"s just always set it to
                // avoid confusion.
                .push(
                        "                                                       cachePolicy:NSURLRequestUseProtocolCachePolicy")
                .push(
                        "                                                   timeoutInterval:" + timeout + "];")
                .push("[request setHTTPMethod:@\"" + harRequest.getMethod() + "\"];");

        if (hasHeaders) {
            code.push("[request setAllHTTPHeaderFields:headers];");
        }

        if (hasBody) {
            code.push("[request setHTTPBody:postData];");
        }

        code.blank()
                // Retrieving the shared session will be less verbose than creating a new one.
                .push("NSURLSession *session = [NSURLSession sharedSession];")
                .push("NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request")
                .push(
                        "                                            completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {")
                .push(1, "                                            if (error) {")
                .push(2, "                                            NSLog(@\"%@\", error);")
                .push(1, "                                            } else {")
                // Casting the NSURLResponse to NSHTTPURLResponse so the user can see the status     .
                .push(
                        2,
                        "                                            NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;")
                .push(2, "                                            NSLog(@\"%@\", httpResponse);")
                .push(1, "                                            }")
                .push("                                            }];")
                .push("[dataTask resume];");

        return code.join();
    }
}
