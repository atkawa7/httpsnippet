package io.github.atkawa7.httpsnippet.demo.swagger.extensions;

import java.util.List;

import springfox.documentation.service.ListVendorExtension;

import io.github.atkawa7.httpsnippet.demo.swagger.models.CodeSample;

public class CodeSampleVendorExtension extends ListVendorExtension<CodeSample> {
  public CodeSampleVendorExtension(final String name, final List<CodeSample> values) {
    super(name, values);
  }
}
