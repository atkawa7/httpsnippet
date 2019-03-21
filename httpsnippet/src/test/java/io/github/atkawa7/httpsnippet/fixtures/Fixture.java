package io.github.atkawa7.httpsnippet.fixtures;

import com.smartbear.har.model.HarRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Fixture {
    private FixtureType fixtureType;
    private HarRequest harRequest;
}
