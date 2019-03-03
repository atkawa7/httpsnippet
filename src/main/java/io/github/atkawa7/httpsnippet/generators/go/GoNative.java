package io.github.atkawa7.httpsnippet.generators.go;

import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.HttpHeaders;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import com.smartbear.har.model.HarCookie;
import com.smartbear.har.model.HarHeader;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Setter
public class GoNative extends CodeGenerator {

    private boolean showBoilerplate;
    private boolean checkErrors;
    private boolean printBody;
    private int timeout;

    public GoNative() {
        super(Client.GO, Language.GO);
        this.showBoilerplate = true;
        this.checkErrors = false;
        this.printBody = true;
        this.timeout = -10;
    }

    public String errorPlaceholder() {
        return checkErrors ? "err" : "_";
    }

    public int indent() {
        return showBoilerplate ? 1 : 0;
    }

    public void errorCheck(CodeBuilder builder) {
        int indent = indent();
        if (checkErrors) {
            builder.push(indent, "if err != nil {").push(indent + 1, "panic(err)").push(indent, "}");
        }
    }

    @Override
    public String code(@NonNull final HarRequest harRequest) throws Exception {
        CodeBuilder codeBuilder = new CodeBuilder("\t");
        HarPostData postData = harRequest.getPostData();
        int indent = indent();
        String errorPlaceholder = errorPlaceholder();

        // Create boilerplate
        if (showBoilerplate) {
            codeBuilder.push("package main").blank().push("import (").push(indent, "\"fmt\"");

            if (timeout > 0) {
                codeBuilder.push(indent, "\"time\"");
            }

            if (hasText(postData)) {
                codeBuilder.push(indent, "\"strings\"");
            }

            codeBuilder.push(indent, "\"net/http\"");

            if (printBody) {
                codeBuilder.push(indent, "\"io/ioutil\"");
            }

            codeBuilder.push(")").blank().push("func main() {").blank();
        }

        // Create client
        String client;
        if (timeout > 0) {
            client = "client";
            codeBuilder
                    .push(indent, "client := http.Client{")
                    .push(indent + 1, "Timeout: time.Duration(%s * time.Second),", Integer.toString(timeout))
                    .push(indent, "}")
                    .blank();
        } else {
            client = "http.DefaultClient";
        }

        codeBuilder.push(indent, "url := \"%s\"", harRequest.getUrl()).blank();

        // If we have body content or not create the var and reader or nil
        if (hasText(postData)) {
            codeBuilder
                    .push(indent, "payload := strings.NewReader(%s)", toJson(postData.getText()))
                    .blank()
                    .push(
                            indent,
                            "req, %s := http.NewRequest(\"%s\", url, payload)",
                            errorPlaceholder,
                            harRequest.getMethod())
                    .blank();
        } else {
            codeBuilder
                    .push(
                            indent,
                            "req, %s := http.NewRequest(\"%s\", url, nil)",
                            errorPlaceholder,
                            harRequest.getMethod())
                    .blank();
        }

        errorCheck(codeBuilder);

        List<HarHeader> headers = harRequest.getHeaders();

        // Add headers
        if (ObjectUtils.isNotEmpty(headers)) {
            headers.forEach(
                    harHeader -> {
                        codeBuilder.push(
                                indent,
                                "req.Header.Add(\"%s\", \"%s\")",
                                harHeader.getName(),
                                harHeader.getValue());
                    });

            codeBuilder.blank();
        }

        List<HarCookie> cookies = harRequest.getCookies();

        if (ObjectUtils.isNotEmpty(cookies)) {
            codeBuilder.push(
                    indent, "req.Header.Add(\"%s\", \"%s\")", HttpHeaders.COOKIE, asCookies(cookies));
            codeBuilder.blank();
        }

        // Make request
        codeBuilder.push(indent, "res, %s := %s.Do(req)", errorPlaceholder, client);
        errorCheck(codeBuilder);

        // Get Body
        if (printBody) {
            codeBuilder
                    .blank()
                    .push(indent, "defer res.Body.Close()")
                    .push(indent, "body, %s := ioutil.ReadAll(res.Body)", errorPlaceholder);
            errorCheck(codeBuilder);
        }

        // Print it
        codeBuilder.blank().push(indent, "fmt.Println(res)");

        if (printBody) {
            codeBuilder.push(indent, "fmt.Println(string(body))");
        }

        // End main block
        if (showBoilerplate) {
            codeBuilder.blank().push("}");
        }

        return codeBuilder.join();
    }
}
