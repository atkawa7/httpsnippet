package io.github.atkawa7.httpsnippet.demo.swagger.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Wither
public class CodeSample {
  String lang;
  String source;
  String label;
}
