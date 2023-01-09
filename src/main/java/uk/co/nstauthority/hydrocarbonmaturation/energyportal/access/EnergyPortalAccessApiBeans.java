package uk.co.nstauthority.hydrocarbonmaturation.energyportal.access;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.fivium.digital.energyportalteamaccesslibrary.team.EnergyPortalAccessService;

@Configuration
class EnergyPortalAccessApiBeans {

  @Bean
  EnergyPortalAccessService energyPortalAccessService(EnergyPortalAccessApiConfiguration configuration) {
    return new EnergyPortalAccessService(configuration.baseUrl(), configuration.token());
  }
}
