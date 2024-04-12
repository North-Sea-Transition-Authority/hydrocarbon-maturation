package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class AnalyticsPropertiesTestUtil {

  private AnalyticsPropertiesTestUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    public AnalyticsConfigurationProperties build() {
      return new AnalyticsConfigurationProperties(
          "app tag",
          "global tag"
      );
    }
  }
}
