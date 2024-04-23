package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({"devtools"})
public class DevtoolsConfiguration {

  @Bean
  @Order(2)
  protected SecurityFilterChain devToolsFilterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity
        .securityMatcher("/devtool/**")
        .authorizeHttpRequests(requestMatcherRegistry ->
          requestMatcherRegistry
              .requestMatchers("/devtool/**").permitAll()
        );

    return httpSecurity.build();
  }
}