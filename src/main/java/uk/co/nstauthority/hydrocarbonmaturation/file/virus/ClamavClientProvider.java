package uk.co.nstauthority.hydrocarbonmaturation.file.virus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.capybara.clamav.ClamavClient;

@Configuration
class ClamavClientProvider {

  @Bean
  ClamavClient clamavClient(ClamavConfig clamavConfig) {
    return new ClamavClient(clamavConfig.host(), clamavConfig.port());
  }
}
