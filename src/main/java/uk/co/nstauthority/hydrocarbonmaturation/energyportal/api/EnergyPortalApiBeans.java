package uk.co.nstauthority.hydrocarbonmaturation.energyportal.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.fivium.energyportalapi.client.EnergyPortal;
import uk.co.fivium.energyportalapi.client.licence.application.LicenceApplicationApi;
import uk.co.fivium.energyportalapi.client.licence.licence.LicenceApi;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.client.referenceblock.fullreferenceblock.FullReferenceBlockApi;
import uk.co.fivium.energyportalapi.client.user.UserApi;
import uk.co.fivium.energyportalapi.client.wellbore.WellboreApi;

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

  @Bean
  LicenceApi licenceApi(EnergyPortal energyPortal) {
    return new LicenceApi(energyPortal);
  }

  @Bean
  FullReferenceBlockApi fullReferenceBlockApi(EnergyPortal energyPortal) {
    return new FullReferenceBlockApi(energyPortal);
  }

  @Bean
  OrganisationApi organisationApi(EnergyPortal energyPortal) {
    return new OrganisationApi(energyPortal);
  }

  @Bean
  UserApi userApi(EnergyPortal energyPortal) {
    return new UserApi(energyPortal);
  }

  @Bean
  WellboreApi wellboreApi(EnergyPortal energyPortal) {
    return new WellboreApi(energyPortal);
  }
}