package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

class SubmissionFilterUtilTest {

  static private SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void isSubmissionPreventingProspectRelinquishment_whenSubmittedRelinquishment_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingProspectRelinquishment(submissionDetail)).isTrue();
  }

  @Test
  void isSubmissionPreventingProspectRelinquishment_whenDeletedRelinquishment_assertFalse() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingProspectRelinquishment(submissionDetail)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isSubmissionPreventingProspectRelinquishment_whenDeletedSubmission_assertFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingProspectRelinquishment(submissionDetail)).isFalse();
  }

  @Test
  void isSubmissionPreventingProspectUpdate_whenDraftUpdate_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingProspectUpdate(submissionDetail)).isTrue();
  }

  @Test
  void isSubmissionPreventingProspectUpdate_whenDiscoveryDraft_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingProspectUpdate(submissionDetail)).isTrue();
  }

  @Test
  void isSubmissionPreventingProspectUpdate_whenProspectRelinquishmentDraft_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingProspectUpdate(submissionDetail)).isTrue();
  }

  @Test
  void isSubmissionPreventingProspectUpdate_whenPostDrillDraft_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingProspectUpdate(submissionDetail)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isSubmissionPreventingProspectUpdate_whenDeletedSubmission_assertFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingProspectUpdate(submissionDetail)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isSubmissionPreventingDryHoleOrDiscovery_whenDeletedSubmission_assertFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingDryHoleOrDiscovery(submissionDetail)).isFalse();
  }

  @Test
  void isSubmissionPreventingDryHoleOrDiscovery_whenSubmittedUpdateProspect_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingDryHoleOrDiscovery(submissionDetail)).isTrue();
  }

  @Test
  void isSubmissionPreventingDryHoleOrDiscovery_whenSubmittedProspectRelinquishment_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingDryHoleOrDiscovery(submissionDetail)).isTrue();
  }

  @Test
  void isSubmissionPreventingDryHoleOrDiscovery_whenSubmittedDryHole_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);

    assertThat(SubmissionFilterUtil.isSubmissionPreventingDryHoleOrDiscovery(submissionDetail)).isTrue();
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
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

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
    submissionDetail.setStatus(SubmissionDetailStatus.WITHDRAWN);
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

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isExtantProspectRelinquishment_whenSubmissionType(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    if (submissionType == SubmissionType.RELINQUISHMENT_PROSPECT) {
      assertThat(SubmissionFilterUtil.isExtantProspectRelinquishment(submissionDetail)).isTrue();
    } else {
      assertThat(SubmissionFilterUtil.isExtantProspectRelinquishment(submissionDetail)).isFalse();
    }

    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    assertThat(SubmissionFilterUtil.isExtantProspectRelinquishment(submissionDetail)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isDraftOrSubmittedUpdateProspect_whenSubmissionType(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    if (submissionType == SubmissionType.PROSPECT_UPDATE) {
      assertThat(SubmissionFilterUtil.isDraftOrSubmittedUpdateProspect(submissionDetail)).isTrue();
    } else {
      assertThat(SubmissionFilterUtil.isDraftOrSubmittedUpdateProspect(submissionDetail)).isFalse();
    }

    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    assertThat(SubmissionFilterUtil.isDraftOrSubmittedUpdateProspect(submissionDetail)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void isExtantPostDrillDryHoleSubmission(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      assertThat(SubmissionFilterUtil.isExtantPostDrillDryHoleSubmission(submissionDetail)).isTrue();
    } else {
      assertThat(SubmissionFilterUtil.isExtantPostDrillDryHoleSubmission(submissionDetail)).isFalse();
    }

    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    assertThat(SubmissionFilterUtil.isExtantPostDrillDryHoleSubmission(submissionDetail)).isFalse();

    submissionDetail.setStatus(SubmissionDetailStatus.WITHDRAWN);
    assertThat(SubmissionFilterUtil.isExtantPostDrillDryHoleSubmission(submissionDetail)).isFalse();
  }
}