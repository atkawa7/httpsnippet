package io.github.atkawa7.httpsnippet.generators;

import static io.github.atkawa7.httpsnippet.models.internal.CodeRequest.newCodeRequest;

import java.util.Objects;

import io.github.atkawa7.har.HarRequest;
import lombok.Getter;


import io.github.atkawa7.httpsnippet.models.Client;
import io.github.atkawa7.httpsnippet.models.Language;
import io.github.atkawa7.httpsnippet.models.internal.CodeRequest;
import io.github.atkawa7.httpsnippet.utils.HarUtils;

@Getter
public abstract class CodeGenerator {

  protected final Client client;
  protected final Language language;
  protected final String displayName;

  protected CodeGenerator(Client client, Language language) {
    Objects.requireNonNull(client, "Client cannot be null");
    Objects.requireNonNull(language, "Language cannot be null");
    this.client = client;
    this.language = language;
    this.displayName = String.format("%s:%s", language.getTitle(), client.getTitle());
  }

  public String code(final HarRequest harRequest) throws Exception {
    return this.generateCode(newCodeRequest(harRequest));
  }

  // internal methods
  protected abstract String generateCode(final CodeRequest harRequest) throws Exception;

  protected String toJson(Object value) throws Exception {
    return HarUtils.toJsonString(value);
  }

  protected String toPrettyJson(Object value) throws Exception {
    return HarUtils.toPrettyJsonString(value);
  }

  @Override
  public String toString() {
    return displayName;
  }
}
