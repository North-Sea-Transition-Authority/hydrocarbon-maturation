package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SubmissionDetailDisplayUtilTest {

  @Test
  void getDisplayNameWithoutLicenceApplicationReference() {
    var resourceName = "Name";
    var centreOfInterest = "1/1";

    assertThat(SubmissionDetailDisplayUtil.getDisplayNameWithoutLicenceApplicationReference(resourceName,
        centreOfInterest))
        .isEqualTo("%s (%s)".formatted(resourceName, centreOfInterest));
  }

  @Test
  void getDisplayNameWithoutLicenceApplicationReference_nullCentreOfInterest() {
    var resourceName = "Name";

    assertThat(SubmissionDetailDisplayUtil.getDisplayNameWithoutLicenceApplicationReference(resourceName, null))
        .isEqualTo(resourceName);
  }

  @Test
  void getDisplayName() {
    var resourceName = "Name";
    var centreOfInterest = "1/1";
    var licenceApplicationReference = "RT9";

    assertThat(SubmissionDetailDisplayUtil.getDisplayName(resourceName, centreOfInterest, licenceApplicationReference))
        .isEqualTo("Name (1/1) (Round RT9)");
  }

  @Test
  void getDisplayName_nullLicenceApplicationReference_nullCentreOfInterest() {
    var resourceName = "Name";

    assertThat(SubmissionDetailDisplayUtil.getDisplayName(resourceName, null, null))
        .isEqualTo(resourceName);
  }

  @Test
  void getDisplayName_nullLicenceApplicationReference_hasCentreOfInterest() {
    var resourceName = "Name";
    var centreOfInterest = "1/1";

    assertThat(SubmissionDetailDisplayUtil.getDisplayName(resourceName, centreOfInterest, null))
        .isEqualTo("%s (%s)".formatted(resourceName, centreOfInterest));
  }

  @Test
  void getDisplayName_hasLicenceApplicationReference_nullCentreOfInterest() {
    var resourceName = "Name";
    var licenceApplicationReference = "RT9";

    assertThat(SubmissionDetailDisplayUtil.getDisplayName(resourceName, null, licenceApplicationReference))
        .isEqualTo("Name (Round RT9)");
  }

  @Test
  void getDisplayName_handlesEmptyStrings() {
    var resourceName = "Name";
    var centreOfInterest = "";
    var licenceApplicationReference = "";

    assertThat(SubmissionDetailDisplayUtil.getDisplayName(resourceName, centreOfInterest, licenceApplicationReference))
        .isEqualTo(resourceName);
  }
}
