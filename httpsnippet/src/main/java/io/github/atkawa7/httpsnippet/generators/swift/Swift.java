package io.github.atkawa7.httpsnippet.generators.swift;

import com.smartbear.har.model.HarParam;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Swift extends CodeGenerator {

    public Swift() {
        super(Client.SWIFT, Language.SWIFT);
    }

    private <T> String literalDeclaration(String name, T parameters) {
        return String.format("let %s = %s", name, this.literalRepresentation(parameters, null));
    }

    private <T> String literalRepresentation(T value, Integer indent) {
        int indentLevel = ObjectUtils.isNull(indent)  ? 1 : indent + 1;

          if(ObjectUtils.isNull(value)){
              return  "\"\"";
          }
          else if ( value instanceof List){
              List list = (List) value;
              List<String> listBuilder = new ArrayList<>();
              for (Object obj : list) {
                  listBuilder.add(this.literalRepresentation(obj, indentLevel));
              }

              return "[" +  String.join(",",listBuilder) + "]";

          }
          else if (value instanceof Map){
              Map<Object, Object> map = (Map) value;
              List<String> listBuilder = new ArrayList<>();
              for (Map.Entry<Object, Object> entry : map.entrySet()) {
                  String format = String.format("\"%s\": %s", entry.getKey(), this.literalRepresentation(entry.getValue(), indentLevel));
                  listBuilder.add(format);
              }
              return "[" +  String.join(",",listBuilder) + "]";
          }
          else{
              return '"'+ value.toString() + '"';
          }
    }

    @Override
    protected String generateCode(CodeRequest codeRequest) throws Exception {
        CodeBuilder code = new CodeBuilder(" ");
        int timeout  = 10;


        // We just want to make sure people understand that is the only dependency
        code.push("import Foundation");

        if (codeRequest.hasHeadersAndCookies()) {
            code.blank()
                    .push(this.literalDeclaration("headers", codeRequest.allHeadersAsMap()));
        }

        if (codeRequest.hasBody()) {
            switch (codeRequest.getMimeType()) {
                case MediaType
                        .APPLICATION_FORM_URLENCODED:
                    if(codeRequest.hasParams()){
                        List<HarParam> params = codeRequest.getParams();
                        code.blank()
                                .push("let postData = NSMutableData(data: \"%s=%s\".data(using: String.Encoding.utf8)!)", params.get(0).getName(), params.get(0).getValue());
                        for (int i = 1; i<params.size(); i++) {
                            code.push("postData.append(\"&%s=%s\".data(using: String.Encoding.utf8)!)", params.get(i).getName(), params.get(i).getValue());
                        }
                    }
                    break;

                case MediaType.APPLICATION_JSON:
                    if (codeRequest.hasText()) {
                        code.push(this.literalDeclaration("parameters", codeRequest.fromJsonString()), "as [String : Any]")
                                .blank()
                                .push("let postData = JSONSerialization.data(withJSONObject: parameters, options: [])");
                    }
                    break;

                case MediaType.MULTIPART_FORM_DATA:
                    code.push(this.literalDeclaration("parameters", codeRequest.paramsAsMap()))
                            .blank()
                            .push("var body = \"\"")
                            .push("var error: NSError? = nil")
                            .push("for param in parameters {")
                            .push(1, "let paramName = param[\"name\"]!")
                            .push(1, "body += \"--\\(boundary)\\r\\n\"")
                            .push(1, "body += \"Content-Disposition:form-data; name=\\\"\\(paramName)\\\"\"")
                            .push(1, "if let filename = param[\"fileName\"] {")
                            .push(2, "let contentType = param[\"content-type\"]!")
                            .push(2, "let fileContent = String(contentsOfFile: filename, encoding: String.Encoding.utf8)")
                            .push(2, "if (error != nil) {")
                            .push(3, "print(error)")
                            .push(2, "}")
                            .push(2, "body += \"; filename=\\\"\\(filename)\\\"\\r\\n\"")
                            .push(2, "body += \"Content-Type: \\(contentType)\\r\\n\\r\\n\"")
                            .push(2, "body += fileContent")
                            .push(1, "} else if let paramValue = param[\"value\"] {")
                            .push(2, "body += \"\\r\\n\\r\\n\\(paramValue)\"")
                            .push(1, "}")
                            .push("}");
                    break;

                default:
                    if(codeRequest.hasText()){
                        code.blank()
                                .push("let postData = NSData(data: \"%s\".data(using: String.Encoding.utf8)!)", codeRequest.getText());
                    }
            }
        }

        code.blank()
                // NSURLRequestUseProtocolCachePolicy is the default policy, let"s just always set it to avoid confusion.
                .push("let request = NSMutableURLRequest(url: NSURL(string: \"%s\")! as URL,", codeRequest.getUrl())
                .push("                                        cachePolicy: .useProtocolCachePolicy,")
                .push("                                    timeoutInterval: %s)", Integer.toString(timeout))
                .push("request.httpMethod = \"%s\"", codeRequest.getMethod());

        if (codeRequest.hasHeaders()) {
            code.push("request.allHTTPHeaderFields = headers");
        }

        if (codeRequest.hasHeaders()) {
            code.push("request.httpBody = postData as Data");
        }

        code.blank()
                // Retrieving the shared session will be less verbose than creating a new one.
                .push("let session = URLSession.shared")
                .push("let dataTask = session.dataTask(with: request as URLRequest, completionHandler: { (data, response, error) -> Void in")
                .push(1, "if (error != nil) {")
                .push(2, "print(error)")
                .push(1, "} else {")
                // Casting the NSURLResponse to NSHTTPURLResponse so the user can see the status     .
                .push(2, "let httpResponse = response as? HTTPURLResponse")
                .push(2, "print(httpResponse)")
                .push(1, "}")
                .push("})")
                .blank()
                .push("dataTask.resume()");

        return code.join();
    }
}
