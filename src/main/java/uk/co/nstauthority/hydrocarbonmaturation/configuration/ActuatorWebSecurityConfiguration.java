package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class ActuatorWebSecurityConfiguration {

  private static final String[] NO_CSRF_ENDPOINTS = {
      "/actuator/health",
      "/actuator/field-updated-message/**",
      "/actuator/field-status-message/**"
  };

  @Bean
  @Order(3)
  SecurityFilterChain actuatorSecurityFilterChain(
      HttpSecurity httpSecurity,
      PasswordEncoder passwordEncoder,
      ActuatorConfigurationProperties actuatorConfigurationProperties
  ) throws Exception {
    var user = User.builder()
        .username("admin")
        .password(passwordEncoder.encode(actuatorConfigurationProperties.adminUserPassword()))
        .authorities("ACTUATOR")
        .build();
    var userDetailsService = new InMemoryUserDetailsManager(user);

    httpSecurity
        .securityMatcher("/actuator/**")
        .authorizeHttpRequests(request -> request
            .requestMatchers("/actuator/health").permitAll()
            .anyRequest().hasAuthority("ACTUATOR")
        )
        .httpBasic(Customizer.withDefaults())
        .userDetailsService(userDetailsService)
        .csrf(csrfConfigurer -> csrfConfigurer.ignoringRequestMatchers(NO_CSRF_ENDPOINTS))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return httpSecurity.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
