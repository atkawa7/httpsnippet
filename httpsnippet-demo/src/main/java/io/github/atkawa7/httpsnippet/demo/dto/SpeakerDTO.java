package io.github.atkawa7.httpsnippet.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpeakerDTO {
private Long id;
private String firstName;
private String lastName;
private String biography;
private String company;
// URLS
private String thumbnail;
private String facebook;
private String github;
private String linkedIn;
private String twitter;
}
