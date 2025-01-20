package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

class SubmissionFilterUtilTest {

  static private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isSubmissionPreventingDiscoveryUpdate_whenDeletedSubmission_assertFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingDiscoveryUpdate(submissionDetail)).isFalse();
  }

  @Test
  void isSubmissionPreventingDiscoveryUpdate_whenUpdateDiscoveryDraft_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.UPDATE_DISCOVERY);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingDiscoveryUpdate(submissionDetail)).isTrue();
  }

  @Test
  void isSubmissionPreventingDiscoveryUpdate_whenDiscoveryRelinquishment_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_DISCOVERY);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingDiscoveryUpdate(submissionDetail)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isSubmissionPreventingDiscoveryRelinquishment_whenDeletedSubmission_assertFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingDiscoveryRelinquishment(submissionDetail)).isFalse();
  }

  @Test
  void isSubmissionPreventingDiscoveryRelinquishment_whenDiscoveryRelinquishment_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_DISCOVERY);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingDiscoveryRelinquishment(submissionDetail)).isTrue();
  }

  @Test
  void filterByResourceType_assertProspect_isTrue() {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    assertThat(SubmissionFilterUtil.filterByResourceType(resource, ResourceType.PROSPECT)).isTrue();
    assertThat(SubmissionFilterUtil.filterByResourceType(resource, ResourceType.DISCOVERY)).isFalse();
  }

  @Test
  void filterByResourceType_assertDiscovery_isTrue() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);
    assertThat(SubmissionFilterUtil.filterByResourceType(resource, ResourceType.PROSPECT)).isFalse();
    assertThat(SubmissionFilterUtil.filterByResourceType(resource, ResourceType.DISCOVERY)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isExtantDiscoverySubmission_assertSubmissionTypes_andStatus(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    if (submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT) {
      assertThat(SubmissionFilterUtil.isExtantDiscoverySubmission(submissionDetail)).isTrue();
    } else {
      assertThat(SubmissionFilterUtil.isExtantDiscoverySubmission(submissionDetail)).isFalse();
    }

    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    assertThat(SubmissionFilterUtil.isExtantDiscoverySubmission(submissionDetail)).isFalse();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.getSubmission().setWithdrawn(true);
    assertThat(SubmissionFilterUtil.isExtantDiscoverySubmission(submissionDetail)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isDraftOrSubmittedUpdateDiscovery_whenSubmissionType(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    if (submissionType == SubmissionType.UPDATE_DISCOVERY) {
      assertThat(SubmissionFilterUtil.isDraftOrSubmittedUpdateDiscovery(submissionDetail)).isTrue();
    } else {
      assertThat(SubmissionFilterUtil.isDraftOrSubmittedUpdateDiscovery(submissionDetail)).isFalse();
    }

    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    assertThat(SubmissionFilterUtil.isDraftOrSubmittedUpdateDiscovery(submissionDetail)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isExtantDiscoveryRelinquishment_whenSubmissionType(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    if (submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY) {
      assertThat(SubmissionFilterUtil.isExtantDiscoveryRelinquishment(submissionDetail)).isTrue();
    } else {
      assertThat(SubmissionFilterUtil.isExtantDiscoveryRelinquishment(submissionDetail)).isFalse();
    }

    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    assertThat(SubmissionFilterUtil.isExtantDiscoveryRelinquishment(submissionDetail)).isFalse();
  }
}