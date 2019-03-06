package io.github.atkawa7.httpsnippet.demo.swagger.extensions;

import io.github.atkawa7.httpsnippet.demo.swagger.models.CodeSample;
import springfox.documentation.service.ListVendorExtension;

import java.util.List;

public class CodeSampleVendorExtension extends ListVendorExtension<CodeSample> {
public CodeSampleVendorExtension(final String name, final List<CodeSample> values) {
	super(name, values);
}
}
