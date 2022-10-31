package uk.co.nstauthority.hydrocarbonmaturation.energyportal.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.fivium.energyportalapi.client.EnergyPortal;
import uk.co.fivium.energyportalapi.client.licence.application.LicenceApplicationApi;

@Configuration
class EnergyPortalApiBeans {

  @Bean
  EnergyPortal energyPortal(EnergyPortalApiConfiguration energyPortalApiConfiguration) {
    return EnergyPortal.defaultConfiguration(
        energyPortalApiConfiguration.url(),
        energyPortalApiConfiguration.token()
    );
  }

  @Bean
  LicenceApplicationApi licenceApplicationApi(EnergyPortal energyPortal) {
    return new LicenceApplicationApi(energyPortal);
  }
}