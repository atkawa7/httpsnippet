package io.github.atkawa7.httpsnippet.demo.domain;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
@Entity
public class Speaker {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Long id;

  private String firstName;
  private String lastName;
  private String biography;
  private String company;
  // URLS
  @URL private String thumbnail;

  @URL private String facebook;

  @URL private String github;

  @URL private String linkedIn;

  @URL private String twitter;
}
