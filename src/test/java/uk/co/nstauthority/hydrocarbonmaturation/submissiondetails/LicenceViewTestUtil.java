package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class LicenceViewTestUtil {

  private LicenceViewTestUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Builder Builder() {
    return new Builder();
  }

  public static class Builder {
    private String licenceRef = "default";
    private Integer licenceId = 1;
    private LicenceStatus status = LicenceStatus.EXTANT;

    private Builder(){}

    public Builder withLicenceRef(String ref) {
      this.licenceRef = ref;
      return this;
    }

    public Builder withLicenceId(Integer id) {
      this.licenceId = id;
      return this;
    }

    public Builder withLicenceStatus(LicenceStatus status) {
      this.status = status;
      return this;
    }

    public LicenceDto build() {
      return new LicenceDto(licenceRef, licenceId, status);
    }
  }
}
