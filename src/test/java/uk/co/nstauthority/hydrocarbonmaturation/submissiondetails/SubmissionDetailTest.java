package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

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
    submissionDetail = new SubmissionDetail();
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
    if (status == SubmissionDetailStatus.DRAFT || status == SubmissionDetailStatus.APPROVED) {
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
  void isDeletedSubmission(SubmissionDetailStatus status) {
    submissionDetail.setStatus(status);
    if (status == SubmissionDetailStatus.DELETED) {
      assertTrue(submissionDetail.isDeletedSubmission());
    } else {
      assertFalse(submissionDetail.isDeletedSubmission());
    }
  }

  @Test
  void isDryWellSubmission() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    assertTrue(submissionDetail.isDryWellSubmission());
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    assertFalse(submissionDetail.isDryWellSubmission());
  }
}