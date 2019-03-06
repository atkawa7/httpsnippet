package io.github.atkawa7.httpsnippet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpSnippet {
private Client client;
private Language language;
private String code;
}
