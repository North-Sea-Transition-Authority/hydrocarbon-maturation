package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import uk.co.fivium.energyportalapi.generated.types.LicenceApplication;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

class LicenceApplicationTestUtil {

  private LicenceApplicationTestUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  static Builder Builder() {
    return new Builder();
  }

  static class Builder {

    private Integer licenceApplicationId = 1;
    private String licenceApplicationRef = "F123/123";
    private Builder() {}

    Builder withLicenceApplicationId(Integer licenceApplicationId) {
      this.licenceApplicationId = licenceApplicationId;
      return this;
    }

    Builder withLicenceApplicationRef(String licenceApplicationRef) {
      this.licenceApplicationRef = licenceApplicationRef;
      return this;
    }

    LicenceApplication build() {
      return new LicenceApplication(
          licenceApplicationId,
          licenceApplicationRef
      );
    }

  }
}
