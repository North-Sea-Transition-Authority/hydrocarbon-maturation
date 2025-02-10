package uk.co.nstauthority.hydrocarbonmaturation.energyportal.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.fivium.energyportalapi.client.EnergyPortal;
import uk.co.fivium.energyportalapi.client.LogCorrelationId;
import uk.co.fivium.energyportalapi.client.field.FieldApi;
import uk.co.fivium.energyportalapi.client.licence.application.LicenceApplicationApi;
import uk.co.fivium.energyportalapi.client.licence.licence.LicenceApi;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalapi.client.referenceblock.fullreferenceblock.FullReferenceBlockApi;
import uk.co.fivium.energyportalapi.client.user.UserApi;
import uk.co.fivium.energyportalapi.client.wellbore.WellboreApi;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;

@Configuration
class EnergyPortalApiBeans {

  @Bean
  EnergyPortal energyPortal(EnergyPortalApiConfiguration energyPortalApiConfiguration, EpaRequestHandler requestHandler) {
    return EnergyPortal.customConfiguration(
        energyPortalApiConfiguration.url(),
        energyPortalApiConfiguration.token(),
        EnergyPortal.DEFAULT_REQUEST_TIMEOUT_SECONDS,
        () -> new LogCorrelationId(CorrelationIdUtil.getCorrelationIdFromMdc()),
        requestHandler
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

  @Bean
  FieldApi fieldApi(EnergyPortal energyPortal) {
    return new FieldApi(energyPortal);
  }
}