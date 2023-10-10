package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailDuplicationServiceTest {

  @Mock
  private SubmissionSectionDuplicationService submissionSectionDuplicationService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private OilCaseOutputsDuplicationService oilCaseOutputsDuplicationService;

  private SubmissionDetailDuplicationService submissionDetailDuplicationService;

  @BeforeEach
  void setUp() {
    submissionDetailDuplicationService = new SubmissionDetailDuplicationService(List.of(submissionSectionDuplicationService),
        List.of(oilCaseOutputsDuplicationService), reservoirSubmissionDetailPersistenceService);
  }

  @Test
  void duplicateSubmissionDetailSections_hasNewReservoirSubmissionDetails() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(newSubmissionDetail))
        .thenReturn(List.of(newReservoirSubmissionDetail));
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(oldReservoirSubmissionDetail));

    submissionDetailDuplicationService.duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionSectionDuplicationService).duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);
    verify(oilCaseOutputsDuplicationService).duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);
  }

  @Test
  void duplicateSubmissionDetailSections_noNewReservoirSubmissionDetails() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(newSubmissionDetail))
        .thenReturn(List.of());

    submissionDetailDuplicationService.duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionSectionDuplicationService).duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);
    verifyNoInteractions(oilCaseOutputsDuplicationService);
  }
}