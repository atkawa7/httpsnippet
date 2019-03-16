package io.github.atkawa7.httpsnippet.demo.swagger.extensions;

import io.github.atkawa7.httpsnippet.demo.config.DemoProperties;
import java.util.Objects;
import lombok.Data;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.StringVendorExtension;

@Data
public class ExternalDocsVendorExtension {
private static final String EXTERNAL_DOCS = "externalDocs";
private static final String EXTERNAL_DOCS_URL = "url";
private static final String EXTERNAL_DOCS_DESCRIPTION = "description";

private final ObjectVendorExtension objectVendorExtension;

public ExternalDocsVendorExtension(DemoProperties demoProperties) {
	Objects.requireNonNull(demoProperties, "Properties cannot be null");
	this.objectVendorExtension = new ObjectVendorExtension(EXTERNAL_DOCS);
	this.objectVendorExtension.addProperty(
		new StringVendorExtension(
			EXTERNAL_DOCS_URL, demoProperties.getApplicationExternalDocsUrl()));
	this.objectVendorExtension.addProperty(
		new StringVendorExtension(
			EXTERNAL_DOCS_DESCRIPTION, demoProperties.getApplicationExternalDocsDescription()));
}
}
