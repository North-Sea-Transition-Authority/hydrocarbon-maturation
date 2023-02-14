package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import uk.co.fivium.energyportalapi.generated.types.Licence;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class LicenceTestUtil {

  private LicenceTestUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Builder Builder() {
    return new Builder();
  }

  public static class Builder {

    private Integer licenceId = 1;
    private String licenceRef = "F123/123";
    private LicenceStatus licenceStatus = LicenceStatus.EXTANT;

    Builder() {}

    public Builder withLicenceId(Integer licenceId) {
      this.licenceId = licenceId;
      return this;
    }

    public Builder withLicenceRef(String licenceRef) {
      this.licenceRef = licenceRef;
      return this;
    }

    public Builder withStatus(LicenceStatus licenceStatus) {
      this.licenceStatus = licenceStatus;
      return this;
    }

    public Licence build() {
      var licence = new Licence();
      licence.setLicenceRef(licenceRef);
      licence.setId(licenceId);
      licence.setLicenceStatus(licenceStatus);
      return licence;
    }
  }
}
