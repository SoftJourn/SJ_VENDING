package com.softjourn.vending;

import com.softjourn.common.spring.aspects.logging.EnableLoggingAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableLoggingAspect
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication(exclude = {MultipartAutoConfiguration.class})
@PropertySources(
    @PropertySource(value = "file:${user.home}/.vending/application.properties", ignoreResourceNotFound = true)
)
public class Vending {

  public static void main(String[] args) {
    SpringApplication.run(Vending.class, args);
  }

  public static class ServletInit extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
      return application.sources(Vending.class);
    }
  }
}
