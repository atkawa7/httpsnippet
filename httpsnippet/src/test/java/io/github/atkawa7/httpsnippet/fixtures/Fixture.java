package io.github.atkawa7.httpsnippet.fixtures;

import io.github.atkawa7.har.HarRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Fixture {
  private FixtureType fixtureType;
  private HarRequest harRequest;

  @Override
  public String toString() {
    return "Fixture{name='" + fixtureType.getName() + "'}";
  }
}
