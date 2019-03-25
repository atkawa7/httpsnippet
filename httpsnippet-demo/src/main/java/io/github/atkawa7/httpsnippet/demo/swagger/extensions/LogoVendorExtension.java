package io.github.atkawa7.httpsnippet.demo.swagger.extensions;

import java.util.Objects;

import lombok.Data;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.StringVendorExtension;

import io.github.atkawa7.httpsnippet.demo.config.DemoProperties;

@Data
public class LogoVendorExtension {
  private static final String X_LOGO = "x-logo";
  private static final String X_LOGO_BACKGROUND_COLOR = "backgroundColor";
  private static final String X_LOGO_URL = "url";

  private final ObjectVendorExtension objectVendorExtension;

  public LogoVendorExtension(DemoProperties demoProperties) {
    Objects.requireNonNull(demoProperties, "Properties cannot be null");
    this.objectVendorExtension = new ObjectVendorExtension(X_LOGO);
    this.objectVendorExtension.addProperty(
        new StringVendorExtension(
            X_LOGO_BACKGROUND_COLOR, demoProperties.getApplicationLogoBackgroundColor()));
    this.objectVendorExtension.addProperty(
        new StringVendorExtension(X_LOGO_URL, demoProperties.getApplicationLogoUrl()));
  }
}
