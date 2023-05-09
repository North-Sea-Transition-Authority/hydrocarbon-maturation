package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailServiceTest {
  @Mock
  private SubmissionDetailRepository submissionDetailRepository;
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;
  @Mock
  private SubmissionPersistenceService submissionPersistenceService;
  @InjectMocks
  private SubmissionDetailService submissionDetailService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();

  private List<Submission> submissionList;

  @BeforeEach
  void setUp() {
    submissionList = List.of(new Submission());
  }

  @Test
  void getLatestApprovedDiscoverySubmissionDetailForResource_assertReturn() {
    var resource = new Resource();
    when(submissionDetailRepository.findFirstBySubmissionInAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(
        submissionList,
        List.of(
            SubmissionType.CREATE_DISCOVERY,
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.UPDATE_DISCOVERY),
        SubmissionDetailStatus.APPROVED))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionPersistenceService.getSubmissionsByResource(any()))
        .thenReturn(submissionList);

    assertThat(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(submissionDetail));
  }

  @Test
  void getLatestApprovedProspectSubmissionForResource_assertSubmissionDetailFound() {
    var resource = new Resource();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionPersistenceService.getSubmissionsByResource(any()))
        .thenReturn(submissionList);
    when(submissionDetailRepository
        .findFirstBySubmissionInAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(submissionList,
            List.of(SubmissionType.PROSPECT_UPDATE, SubmissionType.CREATE_PROSPECT),
            SubmissionDetailStatus.APPROVED))
        .thenReturn(Optional.of(submissionDetail));

    var result = submissionDetailService.getLatestApprovedProspectSubmissionForResource(resource);

    assertThat(result).isEqualTo(Optional.of(submissionDetail));
  }

  @Test
  void getApprovedOrSubmittedSubmissionDetailByResource() {
    var resource = new Resource();
    var submissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.getSubmissionsByResource(any()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findFirstBySubmissionInAndStatusInOrderByVersionNumberDesc(
        submissionList,
        List.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED)
    )).thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .contains(submissionDetail);
  }

  @Test
  void isLatestSubmissionForResourceRelinquishment_whenReturnedSubmissionDetail_assertTrue() {
    when(submissionPersistenceService.getSubmissionsByResource(any()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findFirstBySubmissionInAndSubmissionTypeInAndStatusInOrderBySubmittedTimestampDesc(
        submissionList,
        List.of(SubmissionType.RELINQUISHMENT_DISCOVERY, SubmissionType.RELINQUISHMENT_PROSPECT),
        List.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED)))
        .thenReturn(Optional.of(mock(SubmissionDetail.class)));
    var result = submissionDetailService
        .isLatestSubmissionForResourceRelinquishment(submissionDetail.getSubmission().getResource());
    assertTrue(result);
  }

  @Test
  void isLatestSubmissionForResourceRelinquishment_whenNotReturnedSubmissionDetail_assertFalse() {
    when(submissionPersistenceService.getSubmissionsByResource(any()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findFirstBySubmissionInAndSubmissionTypeInAndStatusInOrderBySubmittedTimestampDesc(
        submissionList,
        List.of(SubmissionType.RELINQUISHMENT_DISCOVERY, SubmissionType.RELINQUISHMENT_PROSPECT),
        List.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED)))
        .thenReturn(Optional.empty());
    var result = submissionDetailService
        .isLatestSubmissionForResourceRelinquishment(submissionDetail.getSubmission().getResource());
    assertFalse(result);
  }

  @Test
  void getLatestApprovedSubmissionDetailForResource_assertEmpty() {
    when(submissionPersistenceService.getSubmissionsByResource(any()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findFirstBySubmissionInAndStatusOrderByApprovedTimestampDesc(
        submissionList,
        SubmissionDetailStatus.APPROVED
    )).thenReturn(Optional.empty());

    assertThat(submissionDetailService.getLatestApprovedSubmissionDetailForResource(
        submissionDetail.getSubmission().getResource()))
        .isNotPresent();
  }

  @Test
  void getLatestApprovedSubmissionDetailForResource_assertReturn() {
    when(submissionPersistenceService.getSubmissionsByResource(any()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findFirstBySubmissionInAndStatusOrderByApprovedTimestampDesc(
        submissionList,
        SubmissionDetailStatus.APPROVED
    )).thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.getLatestApprovedSubmissionDetailForResource(
        submissionDetail.getSubmission().getResource()))
        .contains(submissionDetail);
  }

  @Test
  void getSubmissionDetailForIdOrError_assertReturn() {
    var uuid = UUID.randomUUID();
    when(submissionDetailRepository.findById(uuid))
        .thenReturn(Optional.of(submissionDetail));
    assertThat(submissionDetailService.getSubmissionDetailForIdOrError(uuid))
        .isEqualTo(submissionDetail);
  }

  @Test
  void getSubmissionDetailForIdOrError_assertTrow() {
    var uuid = UUID.randomUUID();
    when(submissionDetailRepository.findById(uuid))
        .thenReturn(Optional.empty());
    assertThatThrownBy(() -> submissionDetailService.getSubmissionDetailForIdOrError(uuid))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Submission detail with id %s not found".formatted(uuid));
  }

  @Test
  void getSubmissionTypeName_assertReturn() {
    assertThat(submissionDetailService.getSubmissionTypeName(submissionDetail))
        .isEqualTo(SubmissionType.PROSPECT_UPDATE.getTypeName());
  }

  @Test
  void getSubmissionDetailsBySubmission_assertReturn() {
    var expectedList = List.of(submissionDetail, mock(SubmissionDetail.class));
    var submission = mock(Submission.class);
    when(submissionDetailRepository.findAllBySubmission(submission))
        .thenReturn(expectedList);

    assertThat(submissionDetailService.getSubmissionDetailsBySubmission(submission))
        .isEqualTo(expectedList);
  }

  @ParameterizedTest
  @EnumSource(SubmissionDetailStatus.class)
  void isLatestSubmissionDetailForSubmissionWithStatuses_whenStatus_assertReturn(SubmissionDetailStatus status) {
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submissionDetail.getSubmission(), Set.of(status)))
        .thenReturn(Optional.of(submissionDetail));

    var result = submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
        submissionDetail, Set.of(status));

    assertThat(result).isTrue();
  }

  @Test
  void isLatestSubmissionDetailForSubmissionWithStatuses_whenNotFoundDetail_assertFalse() {
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submissionDetail.getSubmission(), Set.of(SubmissionDetailStatus.SUBMITTED)))
        .thenReturn(Optional.empty());

    var result = submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
        submissionDetail, Set.of(SubmissionDetailStatus.SUBMITTED));

    assertThat(result).isFalse();
  }

  @Test
  void isLatestSubmissionDetailForSubmissionWithStatuses_whenFoundDifferentDetail_assertFalse() {
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submissionDetail.getSubmission(), Set.of(SubmissionDetailStatus.SUBMITTED)))
        .thenReturn(Optional.of(SubmissionDetailTestUtils.createNewProspectSubmissionDetail()));

    var result = submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
        submissionDetail, Set.of(SubmissionDetailStatus.SUBMITTED));

    assertThat(result).isFalse();
  }

  @Test
  void getExtantSubmissionDetailsForResource_oneNotExtant_assertReturn() {
    var resource = mock(Resource.class);
    var submissionDetail1 = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetailNotExtant = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var listOfSubmissions = List.of(
        submissionDetail1.getSubmission(), submissionDetail2.getSubmission(), submissionDetailNotExtant.getSubmission());

    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(listOfSubmissions);
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(submissionDetail1.getSubmission(),
        Set.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.of(submissionDetail1));
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(submissionDetail2.getSubmission(),
        Set.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.of(submissionDetail2));
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(submissionDetailNotExtant.getSubmission(),
        Set.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.empty());

    var result = submissionDetailService.getExtantSubmissionDetailsForResource(resource);

    assertThat(result)
        .containsExactly(submissionDetail1, submissionDetail2);
  }

  @Test
  void getExtantSubmissionDetailsForResource_whenOnSameSubmission_assertLatest() {
    var resource = mock(Resource.class);
    var submission = mock(Submission.class);
    var latestExtantSubmissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    latestExtantSubmissionDetail.setSubmission(submission);

    var listOfSubmissions = List.of(submission);

    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(listOfSubmissions);
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(submission,
        Set.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.of(latestExtantSubmissionDetail));

    var result = submissionDetailService.getExtantSubmissionDetailsForResource(resource);

    assertThat(result)
        .containsExactly(latestExtantSubmissionDetail);
  }
}
