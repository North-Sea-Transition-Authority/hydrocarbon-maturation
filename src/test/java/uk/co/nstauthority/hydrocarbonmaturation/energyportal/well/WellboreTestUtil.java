package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;

import uk.co.fivium.energyportalapi.generated.types.Wellbore;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class WellboreTestUtil {

  private WellboreTestUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Integer wellboreId = 250;

    private String registrationNumber = "registration number";

    private Builder() {}

    public Builder withId(Integer wellboreId) {
      this.wellboreId = wellboreId;
      return this;
    }

    public Builder withRegistrationNumber(String registrationNumber) {
      this.registrationNumber = registrationNumber;
      return this;
    }

    public Wellbore build() {
      return Wellbore.newBuilder()
          .id(wellboreId)
          .registrationNumber(registrationNumber)
          .build();
    }
  }
}
