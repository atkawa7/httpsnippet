package io.github.atkawa7.httpsnippet.demo.config;

import io.github.atkawa7.httpsnippet.demo.domain.EntityMarker;
import io.github.atkawa7.httpsnippet.demo.processors.SpeakerProcessor;
import io.github.atkawa7.httpsnippet.demo.processors.SpeakerProcessorImpl;
import io.github.atkawa7.httpsnippet.demo.repository.RepositoryMarker;
import io.github.atkawa7.httpsnippet.demo.repository.SpeakerRepository;
import io.github.atkawa7.httpsnippet.demo.resources.SpeakerResource;
import io.github.atkawa7.httpsnippet.demo.service.SpeakerService;
import io.github.atkawa7.httpsnippet.demo.service.SpeakerServiceImpl;
import io.github.atkawa7.httpsnippet.demo.swagger.extensions.ExternalDocsVendorExtension;
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
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EntityScan(basePackageClasses = EntityMarker.class)
@EnableJpaRepositories(basePackageClasses = RepositoryMarker.class)
@EnableSwagger2
public class DemoConfig {
    @Bean
    public DemoProperties demoProperties(Environment environment) {
        return new DemoProperties(environment);
    }

@Bean
public SpeakerService speakerService(SpeakerRepository speakerRepository) {
	return new SpeakerServiceImpl(speakerRepository);
}

@Bean
public SpeakerProcessor speakerProcessor(SpeakerService speakerService) {
	return new SpeakerProcessorImpl(speakerService);
}

@Bean
public CodeSampleOperationBuilderPlugin codeSampleProcessor(DemoProperties demoProperties) {
	return new CodeSampleOperationBuilderPlugin(demoProperties);
}

@Bean
public Docket docket(DemoProperties demoProperties) {

	Set<String> protocols = new HashSet<>(Collections.singleton("http"));

	List<VendorExtension> apiInfoVendorExtensions =
            Collections.singletonList(
                    new LogoVendorExtension(demoProperties).getObjectVendorExtension());
	List<VendorExtension> docketVendorExtensions =
            Collections.singletonList(
                    new ExternalDocsVendorExtension(demoProperties).getObjectVendorExtension());
	ApiInfo apiInfo =
		new ApiInfoBuilder()
                .title(demoProperties.getApplicationName())
                .description(demoProperties.getApplicationDescription())
                .contact(
                        new Contact(
                                demoProperties.getApplicationName(),
                                demoProperties.getApplicationDomain(),
                                demoProperties.getApplicationSupport()))
                .license(demoProperties.getApplicationLicenseName())
                .licenseUrl(demoProperties.getApplicationLicenseUrl())
                .version(demoProperties.getApplicationVersion())
                .termsOfServiceUrl(demoProperties.getApplicationLicenseUrl())
                .extensions(apiInfoVendorExtensions)
			.build();

	return new Docket(DocumentationType.SWAGGER_2)
		.protocols(protocols)
		.apiInfo(apiInfo)
		.produces(Collections.singleton(MediaType.APPLICATION_JSON))
		.consumes(Collections.singleton(MediaType.APPLICATION_JSON))
            .extensions(docketVendorExtensions)
		.tags(new Tag("Speakers", "Speakers at Spring One Conference"))
		.select()
		.apis(RequestHandlerSelectors.basePackage(SpeakerResource.class.getPackage().getName()))
		.paths(PathSelectors.any())
		.build();
}
}
