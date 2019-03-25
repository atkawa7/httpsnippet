package io.github.atkawa7.httpsnippet.fixtures;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.smartbear.har.model.HarRequest;

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
