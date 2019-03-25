package io.github.atkawa7.httpsnippet.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.github.atkawa7.httpsnippet.demo.config.DemoConfig;

@SpringBootApplication
@Import({DemoConfig.class})
public class DemoStarter {

  public static void main(String[] args) {
    SpringApplication.run(DemoStarter.class, args);
  }
}
