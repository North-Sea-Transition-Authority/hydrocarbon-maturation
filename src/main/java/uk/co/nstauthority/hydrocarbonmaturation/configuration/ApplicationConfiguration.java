package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import java.time.Clock;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  public EmailValidator emailValidator() {
    return EmailValidator.getInstance();
  }
}
