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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.ResourceGroupService;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailPersistenceServiceTest {

  private static final Instant INSTANT = Instant.now();

  private final static Long USER_WUA_ID = 5L;

  @Mock
  private SubmissionDetailRepository submissionDetailRepository;
  @Mock
  private Clock clock;
  @Mock
  private UserDetailService userDetailService;
  @Mock
  private ResourceGroupService resourceGroupService;

  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @BeforeEach
  void setUp() {
    var clock = Clock.fixed(Instant.from(INSTANT), ZoneId.systemDefault());
    submissionDetailPersistenceService = new SubmissionDetailPersistenceService(submissionDetailRepository, clock, userDetailService, resourceGroupService);
  }

  @Test
  void createInitialSubmissionDetail() {
    when(userDetailService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());

    var submissionType = SubmissionType.CREATE_PROSPECT;
    var resource = new Resource();
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
    var submissionDetail = submissionDetailPersistenceService.createInitialSubmissionDetail(resource, submissionType);
    verify(submissionDetailRepository, times(1)).save(submissionDetailArgumentCaptor.capture());

    var createdSubmissionDetail = submissionDetailArgumentCaptor.getValue();
    assertThat(createdSubmissionDetail.getResourceId()).isEqualTo(resource);
    assertThat(createdSubmissionDetail.getCreatedTimestamp()).isEqualTo(INSTANT);
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

    assertThat(submissionDetail.getSubmittedByWuaId())
        .isEqualTo(USER_WUA_ID.intValue());
    assertThat(submissionDetail.getSubmittedTimestamp()).isEqualTo(INSTANT);

    verify(submissionDetailRepository, times(1)).save(submissionDetail);
  }

  @Test
  void approveSubmission_assertApprovedBy() {
    var submissionDetail = new SubmissionDetail();
    when(userDetailService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());
    submissionDetailPersistenceService.approveSubmission(submissionDetail);

    assertThat(submissionDetail.getApprovedByWuaId())
        .isEqualTo(USER_WUA_ID.intValue());
    assertThat(submissionDetail.getApprovedTimestamp()).isEqualTo(INSTANT);
    verify(submissionDetailRepository, times(1)).save(submissionDetail);
  }

  @Test
  void deleteSubmission() {
    var submissionDetail = new SubmissionDetail();
    submissionDetailPersistenceService.deleteSubmission(submissionDetail);

    assertThat(submissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DELETED);
  }
}