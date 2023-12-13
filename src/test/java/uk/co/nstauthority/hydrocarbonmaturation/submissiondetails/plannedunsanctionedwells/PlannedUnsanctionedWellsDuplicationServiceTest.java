package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateRepository;

@ExtendWith(MockitoExtension.class)
class PlannedUnsanctionedWellsDuplicationServiceTest {
  @Mock
  private SubmissionUpdateRepository submissionUpdateRepository;
  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  @Mock
  private PlannedUnsanctionedWellsRepository plannedUnsanctionedWellsRepository;
  @InjectMocks
  private PlannedUnsanctionedWellsDuplicationService plannedUnsanctionedWellsDuplicationService;

  private SubmissionDetail oldSubmissionDetail;
  private SubmissionDetail newSubmissionDetail;

  @Test
  void duplicateSubmissionSection_notUpdate_doesNotDuplicate() {
    oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
    newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDPA);

    when(submissionUpdateRepository.existsBySubmissionAndResolvedFalse(oldSubmissionDetail.getSubmission()))
        .thenReturn(false);

    plannedUnsanctionedWellsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  @Test
  void duplicateSubmissionSection_notFdpOrFdpa_doesNotDuplicate() {
    oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
    newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    when(submissionUpdateRepository.existsBySubmissionAndResolvedFalse(oldSubmissionDetail.getSubmission()))
        .thenReturn(true);

    plannedUnsanctionedWellsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  @Test
  void duplicateSubmissionSection_isUpdateAndIsFdpOrFdpa_doesDuplicate() {
    oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
    newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);

    when(submissionUpdateRepository.existsBySubmissionAndResolvedFalse(oldSubmissionDetail.getSubmission()))
        .thenReturn(true);
    when(plannedUnsanctionedWellsRepository.findBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(new PlannedUnsanctionedWells()));

    plannedUnsanctionedWellsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(PlannedUnsanctionedWells.class), eq(PlannedUnsanctionedWells.class));
  }
}