package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SubmissionDetailTest {

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail =  SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isProspectSubmission(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    if (submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT) {
      assertTrue(submissionDetail.isProspectSubmission());
    } else {
      assertFalse(submissionDetail.isProspectSubmission());
    }
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isRelinquishment(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    if (submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT) {
      assertTrue(submissionDetail.isRelinquishment());
    } else {
      assertFalse(submissionDetail.isRelinquishment());
    }
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isDryWellOrDiscoverySubmission(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
        || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY) {
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
        || submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
        || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY) {
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

  @ParameterizedTest
  @EnumSource(SubmissionDetailStatus.class)
  void isDraftOrSubmittedSubmission(SubmissionDetailStatus status) {
    submissionDetail.setStatus(status);
    if (status == SubmissionDetailStatus.DRAFT || status == SubmissionDetailStatus.SUBMITTED) {
      assertTrue(submissionDetail.isDraftOrSubmittedSubmission());
    } else {
      assertFalse(submissionDetail.isDraftOrSubmittedSubmission());
    }
  }

  @ParameterizedTest
  @EnumSource(SubmissionDetailStatus.class)
  void isDraftOrApprovedSubmission(SubmissionDetailStatus status) {
    submissionDetail.setStatus(status);
    if (status == SubmissionDetailStatus.DRAFT || status == SubmissionDetailStatus.ACCEPTED) {
      assertTrue(submissionDetail.isDraftOrApprovedSubmission());
    } else {
      assertFalse(submissionDetail.isDraftOrApprovedSubmission());
    }
  }

  @Test
  void isProspectRelinquishment() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    assertFalse(submissionDetail.isProspectRelinquishment());
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    assertTrue(submissionDetail.isProspectRelinquishment());
  }

  @Test
  void isDiscoveryRelinquishment() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    assertFalse(submissionDetail.isDiscoveryRelinquishment());
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_DISCOVERY);
    assertTrue(submissionDetail.isDiscoveryRelinquishment());
  }

  @Test
  void isDiscoveryFromProspect() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    assertFalse(submissionDetail.isDiscoveryFromProspect());
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    assertTrue(submissionDetail.isDiscoveryFromProspect());
  }

  @ParameterizedTest
  @EnumSource(SubmissionDetailStatus.class)
  void isDeletedOrWithdrawnSubmission(SubmissionDetailStatus status) {
    submissionDetail.setStatus(status);
    if (status == SubmissionDetailStatus.DELETED) {
      assertTrue(submissionDetail.isDeletedOrWithdrawnSubmission());
    } else {
      assertFalse(submissionDetail.isDeletedOrWithdrawnSubmission());
    }
  }

  @Test
  void isDryWellSubmission() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    assertTrue(submissionDetail.isDryWellSubmission());
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    assertFalse(submissionDetail.isDryWellSubmission());
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isFieldSubmission(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    if (submissionType == SubmissionType.CREATE_FIELD
        || submissionType == SubmissionType.CREATE_FDP
        || submissionType == SubmissionType.CREATE_CSR
        || submissionType == SubmissionType.CREATE_COP
        || submissionType == SubmissionType.CREATE_FDPA) {
      assertTrue(submissionDetail.isFieldSubmission());
    } else {
      assertFalse(submissionDetail.isFieldSubmission());
    }
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class,
      names = {"CREATE_PROSPECT", "CREATE_DISCOVERY", "CREATE_FDP", "CREATE_CSR", "CREATE_COP", "CREATE_FDPA", "CREATE_DISCOVERY_FROM_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void isFirstSubmission_true(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    assertTrue(submissionDetail.isFirstSubmission());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class,
      names = {"CREATE_PROSPECT", "CREATE_DISCOVERY", "CREATE_FDP", "CREATE_CSR", "CREATE_COP", "CREATE_FDPA", "CREATE_DISCOVERY_FROM_PROSPECT"},
      mode = EnumSource.Mode.EXCLUDE)
  void isFirstSubmission_false(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    assertFalse(submissionDetail.isFirstSubmission());
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void hasSameSubmissionType(SubmissionType submissionType) {
    var otherSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    if (submissionDetail.getSubmissionType() == submissionType) {
      assertThat(otherSubmissionDetail.hasSameSubmissionType(submissionDetail)).isTrue();
      assertThat(submissionDetail.hasSameSubmissionType(otherSubmissionDetail)).isTrue();
    } else {
      assertThat(otherSubmissionDetail.hasSameSubmissionType(submissionDetail)).isFalse();
      assertThat(submissionDetail.hasSameSubmissionType(otherSubmissionDetail)).isFalse();
    }
  }
}