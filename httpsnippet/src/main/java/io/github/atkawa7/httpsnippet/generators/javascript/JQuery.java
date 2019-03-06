package io.github.atkawa7.httpsnippet.generators.javascript;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import com.smartbear.har.model.HarHeader;
import com.smartbear.har.model.HarParam;
import com.smartbear.har.model.HarPostData;
import com.smartbear.har.model.HarRequest;
import io.github.atkawa7.httpsnippet.Client;
import io.github.atkawa7.httpsnippet.Language;
import io.github.atkawa7.httpsnippet.builder.CodeBuilder;
import io.github.atkawa7.httpsnippet.generators.CodeGenerator;
import io.github.atkawa7.httpsnippet.http.MediaType;
import io.github.atkawa7.httpsnippet.utils.ObjectUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class JQuery extends CodeGenerator {

private final Boolean async;
private final Boolean crossDomain;

public JQuery() {
	super(Client.JQUERY, Language.JAVASCRIPT);
	this.async = TRUE;
	this.crossDomain = TRUE;
}

@Override
protected String generateCode(final HarRequest harRequest) throws Exception {
	CodeBuilder code = new CodeBuilder(CodeBuilder.SPACE);

	List<HarHeader> headers = harRequest.getHeaders();

	Map<String, Object> settings = new HashMap<>();
	settings.put("async", async);
	settings.put("crossDomain", crossDomain);
	settings.put("url", harRequest.getUrl());
	settings.put("method", harRequest.getMethod());
	settings.put("headers", asHeaders(headers));

	HarPostData postData = harRequest.getPostData();

	if (ObjectUtils.isNotNull(postData)) {
	String mimeType = this.getMimeType(postData);

	switch (mimeType) {
		case MediaType.APPLICATION_FORM_URLENCODED:
		{
			List<HarParam> params = postData.getParams();
			settings.put(
				"body", ObjectUtils.isNotEmpty(params) ? asParams(params) : postData.getText());
		}
		break;

		case MediaType.APPLICATION_JSON:
		{
			settings.put("processData", FALSE);
			settings.put("data", postData.getText());
		}
		break;

		case MediaType.MULTIPART_FORM_DATA:
		{
			List<HarParam> params = postData.getParams();
			code.push("var form = new FormData();");

			if (ObjectUtils.isNotEmpty(params)) {
			for (HarParam harParam : params) {
				String value =
					StringUtils.firstNonEmpty(
						harParam.getValue(), harParam.getFileName(), CodeBuilder.SPACE);
				code.push("form.append(%s, %s);", toJson(harParam.getName()), toJson(value));
			}
			}

			settings.put("processData", FALSE);
			settings.put("contentType", FALSE);
			settings.put("mimeType", MediaType.MULTIPART_FORM_DATA);
			settings.put("data", "[form]");
			code.blank();
		}
		break;

		default:
		if (hasText(postData)) {
			settings.put("data", postData.getText());
		}
	}
	}

	code.push("var settings = " + toJson(settings).replace("\"[form]\"", "form"))
		.blank()
		.push("$.ajax(settings).done(function (response) {")
		.push(1, "console.log(response);")
		.push("});");

	return code.join();
}
}
