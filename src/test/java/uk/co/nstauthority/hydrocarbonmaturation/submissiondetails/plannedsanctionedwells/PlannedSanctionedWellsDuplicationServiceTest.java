package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateRepository;

@ExtendWith(MockitoExtension.class)
class PlannedSanctionedWellsDuplicationServiceTest {
  @Mock
  private SubmissionUpdateRepository submissionUpdateRepository;
  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  @Mock
  private PlannedSanctionedWellsRepository plannedSanctionedWellsRepository;
  @Mock
  private PswFaultRepository pswFaultRepository;
  @Mock
  private HoleCompletionTypesRepository holeCompletionTypesRepository;
  @Mock
  private PlannedCompletionTypesRepository plannedCompletionTypesRepository;
  @Mock
  private ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository;
  @InjectMocks
  private PlannedSanctionedWellsDuplicationService plannedSanctionedWellsDuplicationService;

  private SubmissionDetail oldSubmissionDetail;
  private SubmissionDetail newSubmissionDetail;

  @Test
  void duplicateSubmissionSection_notUpdate_doesNotDuplicate() {
    oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
    newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDPA);

    when(submissionUpdateRepository.existsBySubmissionAndResolvedFalse(oldSubmissionDetail.getSubmission()))
        .thenReturn(false);

    plannedSanctionedWellsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void duplicateSubmissionSection_notFdpOrFdpa_doesNotDuplicate(SubmissionType submissionType) {
    oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    when(submissionUpdateRepository.existsBySubmissionAndResolvedFalse(oldSubmissionDetail.getSubmission()))
        .thenReturn(true);

    plannedSanctionedWellsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  @Test
  void duplicateSubmissionSection_isUpdateAndIsFdpOrFdpa_doesDuplicate() {
    oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
    newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);

    var oldPlannedSanctionedWells = new PlannedSanctionedWells();
    var newPlannedSanctionedWells = new PlannedSanctionedWells();

    when(submissionUpdateRepository.existsBySubmissionAndResolvedFalse(oldSubmissionDetail.getSubmission()))
        .thenReturn(true);

    when(plannedSanctionedWellsRepository.findBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(oldPlannedSanctionedWells));

    var allPswFaults = List.of(new PswFaults(), new PswFaults());
    when(pswFaultRepository.findAllByPlannedSanctionedWells(oldPlannedSanctionedWells))
        .thenReturn(allPswFaults);

    var allHoleCompletionTypes = List.of(new HoleCompletionTypes());
    when(holeCompletionTypesRepository.findAllByPlannedSanctionedWells(oldPlannedSanctionedWells))
        .thenReturn(allHoleCompletionTypes);

    var allPlannedCompletionTypes =
        List.of(new PlannedCompletionTypes(), new PlannedCompletionTypes(), new PlannedCompletionTypes());
    when(plannedCompletionTypesRepository.findAllByPlannedSanctionedWells(oldPlannedSanctionedWells))
        .thenReturn(allPlannedCompletionTypes);

    var allArtificialLiftMechanisms = List.of(new ArtificialLiftMechanisms());
    when(artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(oldPlannedSanctionedWells))
        .thenReturn(allArtificialLiftMechanisms);

    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
        newSubmissionDetail,
        oldPlannedSanctionedWells,
        PlannedSanctionedWells.class
    )).thenReturn(newPlannedSanctionedWells);

    plannedSanctionedWellsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(newSubmissionDetail, oldPlannedSanctionedWells, PlannedSanctionedWells.class);
    verify(submissionDuplicationPersistenceService, times(allPswFaults.size()))
        .duplicateChildAndReplaceParent(eq(newPlannedSanctionedWells), any(PswFaults.class), eq(PswFaults.class));
    verify(submissionDuplicationPersistenceService, times(allHoleCompletionTypes.size()))
        .duplicateChildAndReplaceParent(eq(newPlannedSanctionedWells), any(HoleCompletionTypes.class), eq(HoleCompletionTypes.class));
    verify(submissionDuplicationPersistenceService, times(allPlannedCompletionTypes.size()))
        .duplicateChildAndReplaceParent(eq(newPlannedSanctionedWells), any(PlannedCompletionTypes.class), eq(PlannedCompletionTypes.class));
    verify(submissionDuplicationPersistenceService, times(allArtificialLiftMechanisms.size()))
        .duplicateChildAndReplaceParent(eq(newPlannedSanctionedWells), any(ArtificialLiftMechanisms.class), eq(ArtificialLiftMechanisms.class));
    verifyNoMoreInteractions(submissionDuplicationPersistenceService);
  }
}