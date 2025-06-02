package uk.co.nstauthority.hydrocarbonmaturation.energyportal;

import org.springframework.stereotype.Component;
import uk.co.fivium.energyportal.accounts.starter.LogCorrelationIdSupplier;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;

@Component
class EnergyPortalAccountsLogCorrelationIdSupplier implements LogCorrelationIdSupplier {

  @Override
  public String get() {
    return CorrelationIdUtil.getCorrelationIdFromMdc();
  }
}