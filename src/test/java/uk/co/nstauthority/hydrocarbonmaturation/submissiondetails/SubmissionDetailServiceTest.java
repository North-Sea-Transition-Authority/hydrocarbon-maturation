package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailServiceTest {

  private static final Instant INSTANT = Instant.now();
  @Mock
  private SubmissionDetailRepository submissionDetailRepository;
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;
  @Mock
  private UserDetailService userDetailService;

  private SubmissionDetailService submissionDetailService;

  private SubmissionDetail submissionDetail;

  private final static Long USER_WUA_ID = 5L;

  @BeforeEach
  void setUp() {
    var clock = Clock.fixed(Instant.from(INSTANT), ZoneId.systemDefault());
    submissionDetailService = new SubmissionDetailService(submissionDetailRepository, generalDetailsPersistenceService,
        clock, userDetailService);
    submissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
  }

  @Test
  void createInitialSubmissionDetail() {
    when(userDetailService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());

    var submissionType = SubmissionType.CREATE_PROSPECT;
    var resource = new Resource();
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
    var submissionDetail = submissionDetailService.createInitialSubmissionDetail(resource, submissionType);
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
  void getLatestSubmissionDetailByResource_verifyMethodCall() {
    var resource = new Resource();
    submissionDetailService.getLatestSubmissionDetailByResource(resource);

    verify(submissionDetailRepository, times(1))
        .findFirstByResourceIdOrderByCreatedTimestampDesc(resource);
  }

  @Test
  void getLatestSubmissionDetailForResourceOrThrow_verifyMethodCall() {
    var resource = new Resource();
    when(submissionDetailRepository.findFirstByResourceIdOrderByCreatedTimestampDesc(resource))
        .thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(resource))
        .isEqualTo(submissionDetail);
  }

  @Test
  void getLatestSubmissionDetailForResourceOrThrow_throw() {
    var resource = new Resource();
    when(submissionDetailRepository.findFirstByResourceIdOrderByCreatedTimestampDesc(resource))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(resource))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Submission detail for resource with ID %s not found"
            .formatted(resource.getId()));
  }

  @Test
  void completeSubmission_assertSubmittedBy() {
    var submissionDetail = new SubmissionDetail();
    when(userDetailService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());
    submissionDetailService.completeSubmission(submissionDetail);

    assertThat(submissionDetail.getSubmittedByWuaId())
        .isEqualTo(USER_WUA_ID.intValue());
    assertThat(submissionDetail.getSubmittedTimestamp()).isEqualTo(INSTANT);
    assertThat(submissionDetail.getApprovedByWuaId())
        .isEqualTo(USER_WUA_ID.intValue());
    assertThat(submissionDetail.getApprovedTimestamp()).isEqualTo(INSTANT);
    verify(submissionDetailRepository, times(2)).save(submissionDetail);
  }

  @Test
  void approveSubmission_assertApprovedBy() {
    var submissionDetail = new SubmissionDetail();
    when(userDetailService.getUserWuaId()).thenReturn(USER_WUA_ID.intValue());
    submissionDetailService.approveSubmission(submissionDetail);

    assertThat(submissionDetail.getApprovedByWuaId())
        .isEqualTo(USER_WUA_ID.intValue());
    assertThat(submissionDetail.getApprovedTimestamp()).isEqualTo(INSTANT);
    verify(submissionDetailRepository, times(1)).save(submissionDetail);
  }

  @Test
  void getSubmissionDetailsForResource_verifyMethodCall() {
    var resource = new Resource();

    submissionDetailService.getSubmissionDetailsForResource(resource);

    verify(submissionDetailRepository, times(1)).findSubmissionDetailsByResourceId(resource);
  }

  @Test
  void deleteSubmission() {
    var submissionDetail = new SubmissionDetail();
    submissionDetailService.deleteSubmission(submissionDetail);

    assertThat(submissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DELETED);
  }

  @Test
  void getResourceName() {
    var submissionDetail = new SubmissionDetail();
    var generalDetail = new GeneralDetail();
    var resourceName = "name";
    generalDetail.setResourceName(resourceName);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var resourceNameOptional = submissionDetailService.getResourceName(submissionDetail);

    assertThat(resourceNameOptional).isEqualTo(Optional.of(resourceName));
  }

  @Test
  void getLatestApprovedProspectSubmissionForResource_assertSubmissionDetailFound() {
    var resource = new Resource();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionDetailRepository
        .findFirstByResourceIdAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(resource,
            List.of(SubmissionType.PROSPECT_UPDATE, SubmissionType.CREATE_PROSPECT),
            SubmissionDetailStatus.APPROVED))
        .thenReturn(Optional.of(submissionDetail));

    var result = submissionDetailService.getLatestApprovedProspectSubmissionForResource(resource);

    assertThat(result).isEqualTo(Optional.of(submissionDetail));
  }
}
