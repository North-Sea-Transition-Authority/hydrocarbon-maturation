package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailPersistenceServiceTest {

  private final static Long USER_WUA_ID = 5L;
  private final static ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().withWuaId(USER_WUA_ID).build();
  private static final Instant INSTANT = Instant.now();

  @Mock
  private SubmissionDetailRepository submissionDetailRepository;
  @Mock
  private Clock clock;
  @Mock
  private UserDetailService userDetailService;
  @Mock
  private ResourceGroupService resourceGroupService;
  @Mock
  private RegulatorTeamService regulatorTeamService;

  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @BeforeEach
  void setUp() {
    var clock = Clock.fixed(Instant.from(INSTANT), ZoneId.systemDefault());
    submissionDetailPersistenceService = new SubmissionDetailPersistenceService(submissionDetailRepository, clock, userDetailService, resourceGroupService,
        regulatorTeamService);
  }

  @Test
  void createInitialSubmissionDetail_whenRegulator() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);
    var submissionType = SubmissionType.CREATE_PROSPECT;
    var resource = new Resource();
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
    var submissionDetail = submissionDetailPersistenceService.createInitialSubmissionDetail(resource, submissionType);
    verify(submissionDetailRepository, times(1)).save(submissionDetailArgumentCaptor.capture());

    var createdSubmissionDetail = submissionDetailArgumentCaptor.getValue();
    assertThat(createdSubmissionDetail.getResourceId()).isEqualTo(resource);
    assertThat(createdSubmissionDetail.getCreatedTimestamp()).isEqualTo(INSTANT);
    assertThat(createdSubmissionDetail.getSubmitterType()).isEqualTo(SubmitterType.NSTA);
    assertThat(createdSubmissionDetail.getCreatedByWuaId()).isEqualTo(USER_WUA_ID.intValue());
    assertThat(createdSubmissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DRAFT);
    assertThat(createdSubmissionDetail.getSubmissionType()).isEqualTo(submissionType);
    assertThat(createdSubmissionDetail.getVersionNumber()).isEqualTo(SubmissionDetailService.FIRST_VERSION);
  }

  @Test
  void createInitialSubmissionDetail_whenIndustry() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
    var submissionType = SubmissionType.CREATE_PROSPECT;
    var resource = new Resource();
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
    var submissionDetail = submissionDetailPersistenceService.createInitialSubmissionDetail(resource, submissionType);
    verify(submissionDetailRepository, times(1)).save(submissionDetailArgumentCaptor.capture());

    var createdSubmissionDetail = submissionDetailArgumentCaptor.getValue();
    assertThat(createdSubmissionDetail.getResourceId()).isEqualTo(resource);
    assertThat(createdSubmissionDetail.getCreatedTimestamp()).isEqualTo(INSTANT);
    assertThat(createdSubmissionDetail.getSubmitterType()).isEqualTo(SubmitterType.INDUSTRY);
    assertThat(createdSubmissionDetail.getCreatedByWuaId()).isEqualTo(USER_WUA_ID.intValue());
    assertThat(createdSubmissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DRAFT);
    assertThat(createdSubmissionDetail.getSubmissionType()).isEqualTo(submissionType);
    assertThat(createdSubmissionDetail.getVersionNumber()).isEqualTo(SubmissionDetailService.FIRST_VERSION);
  }

  @Test
  void completeSubmission_assertSubmittedBy() {
    var submissionDetail = new SubmissionDetail();
    when(userDetailService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());

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

  @Test
  void approveSubmission_assertApprovedBy() {
    var submissionDetail = new SubmissionDetail();
    when(userDetailService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());
    submissionDetailPersistenceService.approveSubmission(submissionDetail);

    assertThat(submissionDetail)
        .extracting(
            SubmissionDetail::getApprovedByWuaId,
            SubmissionDetail::getApprovedTimestamp,
            SubmissionDetail::getStatus)
        .containsExactly(
            USER_WUA_ID.intValue(),
            INSTANT,
            SubmissionDetailStatus.APPROVED
        );
    verify(submissionDetailRepository, times(1)).save(submissionDetail);
  }

  @Test
  void withdrawSubmission_assertWithdrawnBy() {
    var submissionDetail = new SubmissionDetail();
    when(userDetailService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());
    submissionDetailPersistenceService.withdrawSubmission(submissionDetail);

    assertThat(submissionDetail)
        .extracting(
            SubmissionDetail::getWithdrawnByWuaId,
            SubmissionDetail::getWithdrawnTimestamp,
            SubmissionDetail::getStatus)
        .containsExactly(
            USER_WUA_ID.intValue(),
            INSTANT,
            SubmissionDetailStatus.WITHDRAWN
        );
    verify(submissionDetailRepository, times(1)).save(submissionDetail);
  }

  @Test
  void deleteSubmission() {
    var submissionDetail = new SubmissionDetail();
    submissionDetailPersistenceService.deleteSubmission(submissionDetail);

    assertThat(submissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DELETED);
  }
}