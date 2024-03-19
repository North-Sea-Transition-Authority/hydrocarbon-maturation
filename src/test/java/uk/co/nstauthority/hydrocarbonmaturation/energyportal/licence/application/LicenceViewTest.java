package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.fivium.energyportalapi.generated.types.Licence;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;

class LicenceViewTest {
  @Test
  void fromLicence() {
    var id = 1;
    var licenceRef = "ref123";
    var licenceStatus = LicenceStatus.EXTANT;

    var licence = new Licence();
    licence.setId(id);
    licence.setLicenceRef(licenceRef);
    licence.setLicenceStatus(licenceStatus);

    var result = LicenceView.fromLicence(licence);

    assertThat(result)
        .extracting(
            LicenceView::licenceId,
            LicenceView::licenceRef,
            LicenceView::licenceStatus
        )
        .containsExactly(
            id,
            licenceRef,
            licenceStatus
        );
  }
}