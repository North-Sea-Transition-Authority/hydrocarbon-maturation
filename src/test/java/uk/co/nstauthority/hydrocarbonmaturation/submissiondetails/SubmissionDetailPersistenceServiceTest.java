package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailPersistenceServiceTest {

  private final static Long USER_WUA_ID = 5L;
  private final static User USER = UserTestUtil.Builder().withWuaId(USER_WUA_ID).build();
  private static final Instant INSTANT = Instant.now();

  @Mock
  private SubmissionDetailRepository submissionDetailRepository;
  @Mock
  private UserService userService;
  @Mock
  private ResourceGroupService resourceGroupService;
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @Mock
  private AreaTeamService areaTeamService;
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @BeforeEach
  void setUp() {
    var clock = Clock.fixed(Instant.from(INSTANT), ZoneId.systemDefault());
    submissionDetailPersistenceService = new SubmissionDetailPersistenceService(submissionDetailRepository, clock,
        userService, resourceGroupService,
        regulatorTeamService, resourceDetailsPersistenceService, areaTeamService);
  }

  @Test
  void createInitialSubmissionDetail_whenRegulator() {
    when(userService.getUser()).thenReturn(USER);
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);
    var submissionType = SubmissionType.CREATE_PROSPECT;
    var submission = new Submission();
    submissionDetailPersistenceService.createInitialSubmissionDetail(submission, submissionType);
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
    verify(submissionDetailRepository, times(1)).save(submissionDetailArgumentCaptor.capture());

    var createdSubmissionDetail = submissionDetailArgumentCaptor.getValue();
    assertThat(createdSubmissionDetail.getCreatedTimestamp()).isEqualTo(INSTANT);
    assertThat(createdSubmissionDetail.getSubmitterType()).isEqualTo(SubmitterType.REGULATOR);
    assertThat(createdSubmissionDetail.getCreatedByWuaId()).isEqualTo(USER_WUA_ID.intValue());
    assertThat(createdSubmissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DRAFT);
    assertThat(createdSubmissionDetail.getSubmissionType()).isEqualTo(submissionType);
    assertThat(createdSubmissionDetail.getSubmission()).isEqualTo(submission);
    assertThat(createdSubmissionDetail.getVersionNumber()).isEqualTo(SubmissionDetailService.FIRST_VERSION);
  }

  @Test
  void createInitialSubmissionDetail_whenArea() {
    when(userService.getUser()).thenReturn(USER);
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
    when(areaTeamService.isAreaUser(USER)).thenReturn(true);
    var submissionType = SubmissionType.CREATE_PROSPECT;
    var submission = new Submission();
    submissionDetailPersistenceService.createInitialSubmissionDetail(submission, submissionType);
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
    verify(submissionDetailRepository, times(1)).save(submissionDetailArgumentCaptor.capture());

    var createdSubmissionDetail = submissionDetailArgumentCaptor.getValue();
    assertThat(createdSubmissionDetail.getCreatedTimestamp()).isEqualTo(INSTANT);
    assertThat(createdSubmissionDetail.getSubmitterType()).isEqualTo(SubmitterType.REGULATOR);
    assertThat(createdSubmissionDetail.getCreatedByWuaId()).isEqualTo(USER_WUA_ID.intValue());
    assertThat(createdSubmissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DRAFT);
    assertThat(createdSubmissionDetail.getSubmissionType()).isEqualTo(submissionType);
    assertThat(createdSubmissionDetail.getSubmission()).isEqualTo(submission);
    assertThat(createdSubmissionDetail.getVersionNumber()).isEqualTo(SubmissionDetailService.FIRST_VERSION);
  }

  @Test
  void createInitialSubmissionDetail_whenIndustry() {
    when(userService.getUser()).thenReturn(USER);
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
    when(areaTeamService.isAreaUser(USER)).thenReturn(false);
    var submissionType = SubmissionType.CREATE_PROSPECT;
    var submission = new Submission();
    submissionDetailPersistenceService.createInitialSubmissionDetail(submission, submissionType);
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
    verify(submissionDetailRepository, times(1)).save(submissionDetailArgumentCaptor.capture());

    var createdSubmissionDetail = submissionDetailArgumentCaptor.getValue();
    assertThat(createdSubmissionDetail.getCreatedTimestamp()).isEqualTo(INSTANT);
    assertThat(createdSubmissionDetail.getSubmitterType()).isEqualTo(SubmitterType.INDUSTRY);
    assertThat(createdSubmissionDetail.getCreatedByWuaId()).isEqualTo(USER_WUA_ID.intValue());
    assertThat(createdSubmissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DRAFT);
    assertThat(createdSubmissionDetail.getSubmissionType()).isEqualTo(submissionType);
    assertThat(createdSubmissionDetail.getSubmission()).isEqualTo(submission);
    assertThat(createdSubmissionDetail.getVersionNumber()).isEqualTo(SubmissionDetailService.FIRST_VERSION);
  }

  @Test
  void completeSubmission_assertSubmittedBy() {
    var submissionDetail = new SubmissionDetail();
    when(userService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());

    submissionDetailPersistenceService.submitSubmission(submissionDetail);

    assertThat(submissionDetail)
        .extracting(
            SubmissionDetail::getSubmittedByWuaId,
            SubmissionDetail::getSubmittedTimestamp,
            SubmissionDetail::getStatus)
        .containsExactly(
            USER_WUA_ID.intValue(),
            INSTANT,
            SubmissionDetailStatus.SUBMITTED
        );

    verify(submissionDetailRepository, times(1)).save(submissionDetail);
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void approveSubmission_whenSubmissionType_assertApprovedByAndCreateResourceGroupLink(SubmissionType submissionType) {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(submissionType);
    when(userService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());
    submissionDetailPersistenceService.approveSubmission(submissionDetail);

    assertThat(submissionDetail)
        .extracting(
            SubmissionDetail::getAcceptedByWuaId,
            SubmissionDetail::getAcceptedTimestamp,
            SubmissionDetail::getStatus)
        .containsExactly(
            USER_WUA_ID.intValue(),
            INSTANT,
            SubmissionDetailStatus.ACCEPTED
        );
    verify(submissionDetailRepository, times(1)).save(submissionDetail);
    verify(resourceDetailsPersistenceService, times(1))
        .updateOrganisationName(submissionDetail);
    if (submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT) {
      verify(resourceGroupService, times(1)).createResourceGroupLink(submissionDetail);
    } else {
      verifyNoInteractions(resourceGroupService);
    }
  }

  @Test
  void deleteSubmission_whenDraftUpdate() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setVersionNumber(55);
    submissionDetailPersistenceService.deleteSubmission(submissionDetail);

    assertThat(submissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DELETED);
    assertThat(submissionDetail.getVersionNumber()).isNull();
  }

  @Test
  void deleteSubmission() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setVersionNumber(1);
    submissionDetailPersistenceService.deleteSubmission(submissionDetail);

    assertThat(submissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DELETED);
    assertThat(submissionDetail.getVersionNumber()).isEqualTo(1);
  }

  @Test
  void createUpdateSubmissionDetail_assertUpdateProperties() {
    var parentSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submission = SubmissionTestUtils.createSubmission(mock(Resource.class));
    parentSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    parentSubmissionDetail.setVersionNumber(2);
    parentSubmissionDetail.setCreatedByWuaId(1);
    parentSubmissionDetail.setSubmittedTimestamp(INSTANT);
    parentSubmissionDetail.setSubmission(submission);

    when(userService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());

    var updateSubmissionDetail = submissionDetailPersistenceService.createUpdateSubmissionDetail(parentSubmissionDetail);

    assertThat(updateSubmissionDetail)
        .extracting(
            SubmissionDetail::getSubmission,
            SubmissionDetail::getSubmissionType,
            SubmissionDetail::getStatus,
            SubmissionDetail::getVersionNumber,
            SubmissionDetail::getCreatedTimestamp,
            SubmissionDetail::getCreatedByWuaId,
            SubmissionDetail::getSubmitterType)
        .containsExactly(
            submission,
            parentSubmissionDetail.getSubmissionType(),
            SubmissionDetailStatus.DRAFT,
            parentSubmissionDetail.getVersionNumber() + 1,
            INSTANT,
            USER_WUA_ID.intValue(),
            SubmitterType.INDUSTRY
        );
    assertThat(updateSubmissionDetail)
        .hasAllNullFieldsOrPropertiesExcept(
            "submission", "submissionType",
            "status", "versionNumber",
            "createdTimestamp", "createdByWuaId",
            "submitterType");
    verify(submissionDetailRepository, times(1)).save(updateSubmissionDetail);
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void createUpdateSubmissionDetail_whenSubmissionType_assertSubmissionTypeAndVersionIncremented(
      SubmissionType submissionType) {
    var parentVersion = 5;
    var parentSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    parentSubmissionDetail.setVersionNumber(parentVersion);
    var updateSubmissionDetail = submissionDetailPersistenceService.createUpdateSubmissionDetail(
        parentSubmissionDetail);

    assertThat(updateSubmissionDetail.getSubmissionType())
        .isEqualTo(parentSubmissionDetail.getSubmissionType());
    assertThat(updateSubmissionDetail.getVersionNumber())
        .isEqualTo(parentVersion+1);
  }

  @Test
  void createCorrectionSubmissionDetail_assertCorrectionProperties() {
    var parentSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submission = SubmissionTestUtils.createSubmission(mock(Resource.class));
    parentSubmissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    parentSubmissionDetail.setVersionNumber(1);
    parentSubmissionDetail.setCreatedByWuaId(1);
    parentSubmissionDetail.setSubmittedTimestamp(INSTANT);
    parentSubmissionDetail.setSubmission(submission);

    when(userService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());

    var correctionSubmissionDetail = submissionDetailPersistenceService.createCorrectionSubmissionDetail(
        parentSubmissionDetail,
        USER);

    assertThat(correctionSubmissionDetail)
        .extracting(
            SubmissionDetail::getSubmission,
            SubmissionDetail::getSubmissionType,
            SubmissionDetail::getStatus,
            SubmissionDetail::getVersionNumber,
            SubmissionDetail::getCreatedTimestamp,
            SubmissionDetail::getCreatedByWuaId,
            SubmissionDetail::getSubmitterType,
            SubmissionDetail::isCorrection,
            SubmissionDetail::getCorrectionByWuaId)
        .containsExactly(
            submission,
            parentSubmissionDetail.getSubmissionType(),
            SubmissionDetailStatus.DRAFT,
            parentSubmissionDetail.getVersionNumber() + 1,
            INSTANT,
            USER.wuaId().intValue(),
            SubmitterType.REGULATOR,
            true,
            USER.wuaId().intValue()
        );
    verify(submissionDetailRepository, times(1)).save(correctionSubmissionDetail);
  }
}