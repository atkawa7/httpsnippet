package io.github.atkawa7.httpsnippet.demo.config;

import org.springframework.core.env.Environment;

public class DemoProperties {

  private static final String APPLICATION_EXTERNAL_DOCS_URL = "spring.application.externalDocs.url";
  private static final String APPLICATION_EXTERNAL_DOCS_DESCRIPTION =
      "spring.application.externalDocs.description";
  private static final String APPLICATION_BASE_URL = "spring.application.base-url";
  private static final String APPLICATION_LOGO_URL = "spring.application.logo.url";
  private static final String APPLICATION_LOGO_BACKGROUND_COLOR =
      "spring.application.logo.background-color";
  private static final String APPLICATION_NAME = "spring.application.name";
  private static final String APPLICATION_DESCRIPTION = "spring.application.description";
  private static final String APPLICATION_DOMAIN = "spring.application.domain";
  private static final String APPLICATION_SUPPORT = "spring.application.support";
  private static final String APPLICATION_VERSION = "spring.application.version";
  private static final String APPLICATION_LICENSE_NAME = "spring.application.license.name";
  private static final String APPLICATION_LICENSE_URL = "spring.application.license.url";

  private final Environment environment;

  public DemoProperties(Environment environment) {
    this.environment = environment;
  }

  public String getApplicationBaseUrl() {
    return environment.getProperty(APPLICATION_BASE_URL);
  }

  public String getApplicationExternalDocsUrl() {
    return environment.getProperty(APPLICATION_EXTERNAL_DOCS_URL);
  }

  public String getApplicationExternalDocsDescription() {
    return environment.getProperty(APPLICATION_EXTERNAL_DOCS_DESCRIPTION);
  }

  public String getApplicationLogoUrl() {
    return environment.getProperty(APPLICATION_LOGO_URL);
  }

  public String getApplicationLogoBackgroundColor() {
    return environment.getProperty(APPLICATION_LOGO_BACKGROUND_COLOR);
  }

  public String getApplicationName() {
    return environment.getProperty(APPLICATION_NAME);
  }

  public String getApplicationDescription() {
    return environment.getProperty(APPLICATION_DESCRIPTION);
  }

  public String getApplicationDomain() {
    return environment.getProperty(APPLICATION_DOMAIN);
  }

  public String getApplicationSupport() {
    return environment.getProperty(APPLICATION_SUPPORT);
  }

  public String getApplicationVersion() {
    return environment.getProperty(APPLICATION_VERSION);
  }

  public String getApplicationLicenseName() {
    return environment.getProperty(APPLICATION_LICENSE_NAME);
  }

  public String getApplicationLicenseUrl() {
    return environment.getProperty(APPLICATION_LICENSE_URL);
  }
}
