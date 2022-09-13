package uk.co.nstauthority.hydrocarbonmaturation.file.virus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.capybara.clamav.ClamavClient;

@Configuration
class ClamavClientProvider {

  @Bean
  ClamavClient s3Client(ClamavConfig clamavConfig) {
    return new ClamavClient(clamavConfig.getHost(), clamavConfig.getPort());
  }
}
