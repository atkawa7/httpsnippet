package io.github.atkawa7.httpsnippet.demo.swagger.extensions;

import lombok.Data;
import org.springframework.core.env.Environment;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.StringVendorExtension;

@Data
public class LogoVendorExtension {
private static final String SPRING_APPLICATION_LOGO_URL = "spring.application.logo.url";
private static final String SPRING_APPLICATION_LOGO_BACKGROUND_COLOR =
	"spring.application.logo.background-color";

private static final String X_LOGO = "x-logo";
private static final String X_LOGO_BACKGROUND_COLOR = "backgroundColor";
private static final String X_LOGO_URL = "url";

private final ObjectVendorExtension objectVendorExtension;

public LogoVendorExtension(Environment environment) {
	this.objectVendorExtension = new ObjectVendorExtension(X_LOGO);
	this.objectVendorExtension.addProperty(
		new StringVendorExtension(
			X_LOGO_BACKGROUND_COLOR,
			environment.getProperty(SPRING_APPLICATION_LOGO_BACKGROUND_COLOR)));
	this.objectVendorExtension.addProperty(
		new StringVendorExtension(
			X_LOGO_URL, environment.getProperty(SPRING_APPLICATION_LOGO_URL)));
}
}
