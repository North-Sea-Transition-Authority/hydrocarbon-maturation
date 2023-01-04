package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SubmissionDetailTest {

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = new SubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isProspectSubmission(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    if (submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.CREATE_PROSPECT) {
      assertTrue(submissionDetail.isProspectSubmission());
    } else {
      assertFalse(submissionDetail.isProspectSubmission());
    }
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isDryWellOrDiscoverySubmission(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.UPDATE_DISCOVERY) {
      assertTrue(submissionDetail.isDryWellOrDiscoverySubmission());
    } else {
      assertFalse(submissionDetail.isDryWellOrDiscoverySubmission());
    }
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isDiscoverySubmission(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    if (submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.UPDATE_DISCOVERY) {
      assertTrue(submissionDetail.isDiscoverySubmission());
    } else {
      assertFalse(submissionDetail.isDiscoverySubmission());
    }
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isDryWellSubmission(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      assertTrue(submissionDetail.isDryWellSubmission());
    } else {
      assertFalse(submissionDetail.isDryWellSubmission());
    }
  }
}