package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionRepository;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailServiceTest {

  @Mock
  private SubmissionDetailRepository submissionDetailRepository;

  @Mock
  private SubmissionRepository submissionRepository;

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
  void getSubmissionDetail_nullId_thenEmpty() {
    assertThat(submissionDetailService.getSubmissionDetail(null)).isEmpty();
  }

  @Test
  void getSubmissionDetail_nonNullId_submissionDetailDoesNotExist_thenEmpty() {
    var id = UUID.randomUUID();

    when(submissionDetailRepository.findById(id))
        .thenReturn(Optional.empty());

    assertThat(submissionDetailService.getSubmissionDetail(id)).isEmpty();
  }


  @Test
  void getSubmissionDetail_nonNullId_submissionDetailExists_isReturned() {
    var id = UUID.randomUUID();

    when(submissionDetailRepository.findById(id))
        .thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.getSubmissionDetail(id)).contains(submissionDetail);
  }

  @Test
  void getLatestApprovedDiscoverySubmissionDetailForResource_assertReturn() {
    var resource = new Resource();
    when(submissionDetailRepository.findLatestForSubmissionsBySubmissionTypesAndStatusOrderedByAcceptedTimestamp(
        submissionList,
        List.of(
            SubmissionType.CREATE_DISCOVERY,
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.UPDATE_DISCOVERY,
            SubmissionType.RELINQUISHMENT_DISCOVERY),
        SubmissionDetailStatus.ACCEPTED))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(submissionList);

    assertThat(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(submissionDetail));
  }

  @Test
  void getLatestApprovedProspectSubmissionForResource_assertSubmissionDetailFound() {
    var resource = new Resource();
    var latestApprovedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(submissionList);
    when(submissionDetailRepository
        .findLatestForSubmissionsBySubmissionTypesAndStatusOrderedByAcceptedTimestamp(submissionList,
            List.of(SubmissionType.PROSPECT_UPDATE, SubmissionType.CREATE_PROSPECT, SubmissionType.RELINQUISHMENT_PROSPECT),
            SubmissionDetailStatus.ACCEPTED))
        .thenReturn(Optional.of(latestApprovedSubmissionDetail));

    var result = submissionDetailService.getLatestApprovedProspectSubmissionForResource(resource);

    assertThat(result).isEqualTo(Optional.of(latestApprovedSubmissionDetail));
  }

  @Test
  void isLatestSubmissionForResourceRelinquishment_whenReturnedSubmissionDetail_assertTrue() {
    when(submissionPersistenceService.getSubmissionsByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findLatestForSubmissionsBySubmissionTypesAndStatusesOrderedBySubmittedTimestamp(
        submissionList,
        List.of(SubmissionType.RELINQUISHMENT_DISCOVERY, SubmissionType.RELINQUISHMENT_PROSPECT),
        List.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED)))
        .thenReturn(Optional.of(mock(SubmissionDetail.class)));
    var result = submissionDetailService
        .isLatestSubmissionForResourceRelinquishment(submissionDetail.getSubmission().getResource());
    assertTrue(result);
  }

  @Test
  void isLatestSubmissionForResourceRelinquishment_whenNotReturnedSubmissionDetail_assertFalse() {
    when(submissionPersistenceService.getSubmissionsByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findLatestForSubmissionsBySubmissionTypesAndStatusesOrderedBySubmittedTimestamp(
        submissionList,
        List.of(SubmissionType.RELINQUISHMENT_DISCOVERY, SubmissionType.RELINQUISHMENT_PROSPECT),
        List.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED)))
        .thenReturn(Optional.empty());
    var result = submissionDetailService
        .isLatestSubmissionForResourceRelinquishment(submissionDetail.getSubmission().getResource());
    assertFalse(result);
  }

  @Test
  void getLatestApprovedSubmissionDetailForResource_assertEmpty() {
    when(submissionPersistenceService.getSubmissionsByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findLatestForSubmissionsByStatusOrderedByAcceptedTimestamp(
        submissionList,
        SubmissionDetailStatus.ACCEPTED
    )).thenReturn(Optional.empty());

    assertThat(submissionDetailService.getLatestApprovedSubmissionDetailForResource(
        submissionDetail.getSubmission().getResource()))
        .isNotPresent();
  }

  @Test
  void getLatestApprovedSubmissionDetailForResource_assertReturn() {
    when(submissionPersistenceService.getSubmissionsByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findLatestForSubmissionsByStatusOrderedByAcceptedTimestamp(
        submissionList,
        SubmissionDetailStatus.ACCEPTED
    )).thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.getLatestApprovedSubmissionDetailForResource(
        submissionDetail.getSubmission().getResource()))
        .contains(submissionDetail);
  }

  @Test
  void getLatestApprovedSubmissionDetailForResourceAndSubmissionTypes_assertEmpty() {
    var submissionTypes = List.of(SubmissionType.CREATE_DISCOVERY, SubmissionType.RELINQUISHMENT_DISCOVERY);
    when(submissionPersistenceService.getSubmissionsByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findLatestForSubmissionsBySubmissionTypesAndStatusOrderedByAcceptedTimestamp(
        submissionList,
        submissionTypes,
        SubmissionDetailStatus.ACCEPTED
    )).thenReturn(Optional.empty());

    assertThat(submissionDetailService.getLatestApprovedSubmissionDetailForResourceAndSubmissionTypes(
        submissionDetail.getSubmission().getResource(), submissionTypes))
        .isNotPresent();
  }

  @Test
  void getLatestApprovedSubmissionDetailForResourceAndSubmissionTypes_assertReturn() {
    var submissionTypes = List.of(SubmissionType.CREATE_DISCOVERY, SubmissionType.RELINQUISHMENT_DISCOVERY);
    when(submissionPersistenceService.getSubmissionsByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(submissionList);
    when(submissionDetailRepository.findLatestForSubmissionsBySubmissionTypesAndStatusOrderedByAcceptedTimestamp(
        submissionList,
        submissionTypes,
        SubmissionDetailStatus.ACCEPTED
    )).thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.getLatestApprovedSubmissionDetailForResourceAndSubmissionTypes(
        submissionDetail.getSubmission().getResource(), submissionTypes))
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
  void getSubmissionTypeNameWithAcronym_fromSubmissionDetail_assertReturn() {
    assertThat(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .isEqualTo(SubmissionType.PROSPECT_UPDATE.getTypeName());
  }

  @Test
  void getSubmissionTypeNameWithAcronym_fromSubmissionTypeWithAcronym_assertReturn() {
    assertThat(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_COP))
        .isEqualTo("Cessation of Production notification (CoP)");
  }

  @Test
  void getSubmissionTypeNameWithAcronym_fromSubmissionTypeWithNoAcronym_assertReturn() {
    assertThat(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.PROSPECT_UPDATE))
        .isEqualTo("Prospect update");
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
    var submissionDetail2 = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
    var submissionDetailNotExtant = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var listOfSubmissions = List.of(
        submissionDetail1.getSubmission(), submissionDetail2.getSubmission(),
        submissionDetailNotExtant.getSubmission());

    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(listOfSubmissions);
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submissionDetail1.getSubmission(),
        Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.of(submissionDetail1));
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submissionDetail2.getSubmission(),
        Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.of(submissionDetail2));
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submissionDetailNotExtant.getSubmission(),
        Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
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
        Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.of(latestExtantSubmissionDetail));

    var result = submissionDetailService.getExtantSubmissionDetailsForResource(resource);

    assertThat(result)
        .containsExactly(latestExtantSubmissionDetail);
  }

  @Test
  void getAcceptedExtantSubmissionDetailsForResource_oneNotAccepted_assertReturn() {
    var resource = mock(Resource.class);
    var submissionDetail1 = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
    var listOfSubmissions = List.of(submissionDetail1.getSubmission(), submissionDetail2.getSubmission());

    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(listOfSubmissions);
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submissionDetail1.getSubmission(),
        Set.of(SubmissionDetailStatus.ACCEPTED)))
        .thenReturn(Optional.of(submissionDetail1));
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submissionDetail2.getSubmission(),
        Set.of(SubmissionDetailStatus.ACCEPTED)))
        .thenReturn(Optional.empty());

    var result = submissionDetailService.getAcceptedExtantSubmissionDetailsForResources(resource);

    assertThat(result).containsExactly(submissionDetail1);
  }

  @Test
  void openCorrectionExists() {
    var statuses = Set.of(SubmissionDetailStatus.DRAFT);
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    submissionDetailService.openCorrectionExists(statuses, resource);
    verify(submissionDetailRepository, times(1))
        .existsSubmissionDetailByCorrectionTrueAndStatusInAndSubmission_Resource(statuses, resource);
  }

  @Test
  void getSubmissionTypeNameLowercaseWithAcronym_typeWithUpperCaseAcronym() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    assertThat(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym((submissionDetail)))
        .isEqualTo("concept select report (CSR)");
  }

  @Test
  void getSubmissionTypeNameLowercaseWithAcronym_typeWithNoAcronym() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    assertThat(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym((submissionDetail)))
        .isEqualTo("post-drill discovery");
  }

  @Test
  void getSubmissionTypeNameLowercaseWithAcronym_typeWithNonAllUpperCaseAcronym() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    assertThat(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym((submissionDetail)))
        .isEqualTo("cessation of production notification (CoP)");
  }

  @Test
  void getLatestSubmittedSubmissionDetailForResource_noSubmissions_thenReturnEmpty() {
    var resource = new Resource();
    resource.setType(ResourceType.FIELD);

    List<Submission> submissions = List.of();
    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(submissions);
    when(submissionDetailRepository.findLatestForSubmissionsBySubmissionTypesAndStatusesOrderedBySubmittedTimestamp(
        submissions,
        SubmissionType.getSubmissionTypesForResourceType(resource.getType()),
        List.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED)
    )).thenReturn(Optional.empty());

    assertThat(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .isEmpty();
  }

  @Test
  void getLatestSubmittedSubmissionDetailForResource_withSubmittedDetail_thenReturnMostRecentDetail() {
    var resource = new Resource();
    resource.setType(ResourceType.FIELD);

    List<Submission> submissions = List.of(new Submission());

    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(submissions);

    var mostRecentSubmissionDetail = new SubmissionDetail();
    when(submissionDetailRepository.findLatestForSubmissionsBySubmissionTypesAndStatusesOrderedBySubmittedTimestamp(
        submissions,
        SubmissionType.getSubmissionTypesForResourceType(resource.getType()),
        List.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED)
    )).thenReturn(Optional.of(mostRecentSubmissionDetail));

    assertThat(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .isPresent()
        .contains(mostRecentSubmissionDetail);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FIELD", "CREATE_CSR", "CREATE_FDP", "CREATE_FDPA"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void hasFieldSubmissionDataBeenCarriedForward_whenNotAField_thenReturnFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    assertThat(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .isFalse();
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FIELD", "CREATE_CSR", "CREATE_FDP", "CREATE_FDPA"},
      mode = EnumSource.Mode.INCLUDE
  )
  void hasFieldSubmissionDataBeenCarriedForward_whenAField_notCarriedForward_thenFalse(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var submission = submissionDetail.getSubmission();
    var resource = submission.getResource();

    when(submissionRepository.findSubmissionsByResource(resource))
        .thenReturn(List.of(submission));
    when(submissionDetailRepository.findAllBySubmission(submission))
        .thenReturn(List.of(submissionDetail));

    assertThat(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .isFalse();
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FIELD", "CREATE_CSR", "CREATE_FDP", "CREATE_FDPA"},
      mode = EnumSource.Mode.INCLUDE
  )
  void hasFieldSubmissionDataBeenCarriedForward_whenAField_carriedForward_thenTrue(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var submission = submissionDetail.getSubmission();
    var resource = submission.getResource();

    var otherSubmission = new Submission();
    var otherSubmissionDetail = new SubmissionDetail();

    when(submissionRepository.findSubmissionsByResource(resource))
        .thenReturn(List.of(submission, otherSubmission));
    when(submissionDetailRepository.findAllBySubmission(submission))
        .thenReturn(List.of(submissionDetail));
    when(submissionDetailRepository.findAllBySubmission(otherSubmission))
        .thenReturn(List.of(otherSubmissionDetail));

    assertThat(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .isTrue();
  }

  @Test
  void hasDraftOrSubmittedSubmission_true() {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    var submission = submissionDetail.getSubmission();
    var resource = submission.getResource();

    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(List.of(submission));
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(submission,
        Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.hasDraftOrSubmittedSubmission(resource))
        .isTrue();
  }

  @Test
  void hasDraftOrSubmittedSubmission_false() {
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

    var submission = submissionDetail.getSubmission();
    var resource = submission.getResource();

    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(List.of(submission));
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(submission,
        Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.hasDraftOrSubmittedSubmission(resource))
        .isFalse();
  }

  @Test
  void hasDraftOrSubmittedSubmission_noExtantSubmissionDetails() {
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);

    var submission = submissionDetail.getSubmission();
    var resource = submission.getResource();

    when(submissionPersistenceService.getSubmissionsByResource(resource))
        .thenReturn(List.of(submission));
    when(submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(submission,
        Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)))
        .thenReturn(Optional.empty());

    assertThat(submissionDetailService.hasDraftOrSubmittedSubmission(resource))
        .isFalse();
  }

  @Test
  void getLatestSubmissionDetailsBySubmissionsAndStatuses() {
    var resource1 = new Resource();
    var submission1 = SubmissionTestUtils.createSubmission(resource1);
    var submissionDetail1 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
    submissionDetail1.setSubmission(submission1);
    var resource2 = new Resource();
    var submission2 = SubmissionTestUtils.createSubmission(resource2);
    var submissionDetail2 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
    submissionDetail2.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetail2.setSubmission(submission2);
    var submissionDetail3 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
    submissionDetail3.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetail3.setSubmission(submission2);
    submissionDetail3.setVersionNumber(2);
    var resource3 = new Resource();
    var submission3 = SubmissionTestUtils.createSubmission(resource3);
    var submissionDetail4 = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
    submissionDetail4.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetail4.setSubmission(submission3);
    var submissions = List.of(submission1, submission2, submission3);
    var submissionDetailList = List.of(submissionDetail1, submissionDetail2, submissionDetail3, submissionDetail4);
    when(submissionDetailRepository.findAllBySubmissionIn(submissions)).thenReturn(
        submissionDetailList
    );

    assertThat(submissionDetailService.getLatestSubmissionDetailsBySubmissionsAndStatuses(
        submissions,
        Set.of(SubmissionDetailStatus.ACCEPTED))
    ).containsExactlyInAnyOrder(submissionDetail3, submissionDetail4);
  }
}
