package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import uk.co.nstauthority.hydrocarbonmaturation.externalapi.FieldExternalDtoController;

@Configuration
public class ExternalApiWebSecurityConfiguration {

  private final String preSharedKey;
  private final ExternalApiAuthenticationEntryPoint externalApiAuthenticationEntryPoint;
  private static final String ENERGY_PORTAL_API_PATH_MATCHER =
      FieldExternalDtoController.ENERGY_PORTAL_API_BASE_PATH + "/**";

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
        .securityMatcher(ENERGY_PORTAL_API_PATH_MATCHER)
        .authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry.anyRequest().authenticated())
        .sessionManagement(httpSecuritySessionManagementConfigurer ->
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilter(filter)
        .csrf(csrfConfigurer -> csrfConfigurer.ignoringRequestMatchers(ENERGY_PORTAL_API_PATH_MATCHER))
        .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
            httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(externalApiAuthenticationEntryPoint)
        );

    return httpSecurity.build();
  }
}
