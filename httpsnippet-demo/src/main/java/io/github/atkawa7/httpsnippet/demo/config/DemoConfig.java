package io.github.atkawa7.httpsnippet.demo.config;

import io.github.atkawa7.httpsnippet.demo.domain.EntityMarker;
import io.github.atkawa7.httpsnippet.demo.processors.SpeakerProcessor;
import io.github.atkawa7.httpsnippet.demo.processors.SpeakerProcessorImpl;
import io.github.atkawa7.httpsnippet.demo.repository.RepositoryMarker;
import io.github.atkawa7.httpsnippet.demo.repository.SpeakerRepository;
import io.github.atkawa7.httpsnippet.demo.resources.SpeakerResource;
import io.github.atkawa7.httpsnippet.demo.service.SpeakerService;
import io.github.atkawa7.httpsnippet.demo.service.SpeakerServiceImpl;
import io.github.atkawa7.httpsnippet.demo.swagger.extensions.LogoVendorExtension;
import io.github.atkawa7.httpsnippet.demo.swagger.plugins.CodeSampleOperationBuilderPlugin;
import io.github.atkawa7.httpsnippet.http.MediaType;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EntityScan(basePackageClasses = EntityMarker.class)
@EnableJpaRepositories(basePackageClasses = RepositoryMarker.class)
@EnableSwagger2
public class DemoConfig {

private static final String SPRING_APPLICATION_NAME = "spring.application.name";
private static final String SPRING_APPLICATION_DESCRIPTION = "spring.application.description";
private static final String SPRING_APPLICATION_DOMAIN = "spring.application.domain";
private static final String SPRING_APPLICATION_SUPPORT = "spring.application.support";
private static final String SPRING_APPLICATION_VERSION = "spring.application.version";
private static final String SPRING_APPLICATION_TERMS = "spring.application.terms";

@Bean
public SpeakerService speakerService(SpeakerRepository speakerRepository) {
	return new SpeakerServiceImpl(speakerRepository);
}

@Bean
public SpeakerProcessor speakerProcessor(SpeakerService speakerService) {
	return new SpeakerProcessorImpl(speakerService);
}

@Bean
public CodeSampleOperationBuilderPlugin codeSampleProcessor(Environment environment) {
	return new CodeSampleOperationBuilderPlugin(environment);
}

@Bean
public Docket docket(Environment environment) {

	String applicationName = environment.getProperty(SPRING_APPLICATION_NAME);
	String applicationDescription = environment.getProperty(SPRING_APPLICATION_DESCRIPTION);
	String applicationDomain = environment.getProperty(SPRING_APPLICATION_DOMAIN);
	String applicationSupport = environment.getProperty(SPRING_APPLICATION_SUPPORT);
	String applicationVersion = environment.getProperty(SPRING_APPLICATION_VERSION);
	String applicationTerms = environment.getProperty(SPRING_APPLICATION_TERMS);

	Set<String> protocols = new HashSet<>(Collections.singleton("http"));

	ApiInfo apiInfo =
		new ApiInfoBuilder()
			.title(applicationName)
			.description(applicationDescription)
			.contact(new Contact(applicationName, applicationDomain, applicationSupport))
			.license(applicationName)
			.version(applicationVersion)
			.termsOfServiceUrl(applicationTerms)
			.extensions(
				Collections.singletonList(
					new LogoVendorExtension(environment).getObjectVendorExtension()))
			.build();

	return new Docket(DocumentationType.SWAGGER_2)
		.protocols(protocols)
		.apiInfo(apiInfo)
		.produces(Collections.singleton(MediaType.APPLICATION_JSON))
		.consumes(Collections.singleton(MediaType.APPLICATION_JSON))
		.tags(new Tag("Speakers", "Speakers at Spring One Conference"))
		.select()
		.apis(RequestHandlerSelectors.basePackage(SpeakerResource.class.getPackage().getName()))
		.paths(PathSelectors.any())
		.build();
}
}
