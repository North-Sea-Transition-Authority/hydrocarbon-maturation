package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ExternalApiWebSecurityConfiguration {

  public static final String EXTERNAL_API_PATH = "/api/external/v1/**";
  private static final String INTERNAL_API_PATH = "/api/internal/v1/**";

  private final String preSharedKey;
  private final ExternalApiAuthenticationEntryPoint externalApiAuthenticationEntryPoint;

  @Autowired
  public ExternalApiWebSecurityConfiguration(ExternalApiConfiguration externalApiConfiguration,
                                             ExternalApiAuthenticationEntryPoint externalApiAuthenticationEntryPoint) {
    this.preSharedKey = externalApiConfiguration.preSharedKey();
    this.externalApiAuthenticationEntryPoint = externalApiAuthenticationEntryPoint;
  }

  @Bean
  @Order(1)
  protected SecurityFilterChain externalApiFilterChain(HttpSecurity httpSecurity) throws Exception {
    var filter = new PreSharedKeyAuthFilter("Authorization");
    filter.setAuthenticationManager(new ExternalApiSecurityAuthManager(preSharedKey));

    httpSecurity
        .securityMatcher(EXTERNAL_API_PATH, INTERNAL_API_PATH)
        .authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry.anyRequest().authenticated())
        .sessionManagement(httpSecuritySessionManagementConfigurer ->
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilter(filter)
        .csrf(csrfConfigurer -> csrfConfigurer.ignoringRequestMatchers(EXTERNAL_API_PATH, INTERNAL_API_PATH))
        .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
            httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(externalApiAuthenticationEntryPoint)
        );

    return httpSecurity.build();
  }
}
