package uk.co.nstauthority.hydrocarbonmaturation.energyportal.access;

public class EnergyPortalAccessApiConfigurationUtil {

  private static final String BASE_URL = "https://energy-portal-access-api.com";
  private static final String TOKEN = "dev";
  private static final String RESOURCE_TYPE = "REMI_ACCESS_TEAM";
  private static final String PRIVILEGE_NAME = "REMI_ACCESS_PRIVILEGE";

  public static EnergyPortalAccessApiConfiguration getDefaultTestConfig() {
    return new EnergyPortalAccessApiConfiguration(
        BASE_URL,
        TOKEN,
        RESOURCE_TYPE,
        PRIVILEGE_NAME
    );
  }
}
