package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.fivium.energyportalapi.generated.types.Licence;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;

class LicenceDtoTest {
  @Test
  void fromLicence() {
    var id = 1;
    var licenceRef = "ref123";
    var licenceStatus = LicenceStatus.EXTANT;

    var licence = new Licence();
    licence.setId(id);
    licence.setLicenceRef(licenceRef);
    licence.setLicenceStatus(licenceStatus);

    var result = LicenceDto.fromLicence(licence);

    assertThat(result)
        .extracting(
            LicenceDto::licenceId,
            LicenceDto::licenceRef,
            LicenceDto::licenceStatus
        )
        .containsExactly(
            id,
            licenceRef,
            licenceStatus
        );
  }
}