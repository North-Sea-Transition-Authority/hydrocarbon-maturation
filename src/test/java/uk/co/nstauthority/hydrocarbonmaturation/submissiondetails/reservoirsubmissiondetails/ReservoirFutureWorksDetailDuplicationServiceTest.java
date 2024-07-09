package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class ReservoirFutureWorksDetailDuplicationServiceTest {

  @Mock
  private ReservoirFutureWorksDetailRepository futureWorksDetailRepository;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @InjectMocks
  private ReservoirFutureWorksDetailDuplicationService reservoirFutureWorksDetailDuplicationService;

  private static final ReservoirSubmissionDetail OLD_SUBMISSION_DETAIL = new ReservoirSubmissionDetail();
  private static final ReservoirSubmissionDetail NEW_SUBMISSION_DETAIL = new ReservoirSubmissionDetail();

  @Test
  void whenEntityFound_andIsDevelopedUnderFuturePhase_thenCarriedForward() {
    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    reservoirFutureWorksDetail.setIsReservoirBeingDevelopedUnderFuturePhase(true);

    when(futureWorksDetailRepository.findByReservoirSubmissionDetail(OLD_SUBMISSION_DETAIL))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    reservoirFutureWorksDetailDuplicationService.duplicateReservoirSection(OLD_SUBMISSION_DETAIL, NEW_SUBMISSION_DETAIL);

    verify(reservoirDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(
            NEW_SUBMISSION_DETAIL,
            reservoirFutureWorksDetail,
            ReservoirFutureWorksDetail.class
        );
  }

  @Test
  void whenEntityFound_andNotDevelopedUnderFuturePhase_thenNotCarriedForward() {
    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    reservoirFutureWorksDetail.setIsReservoirBeingDevelopedUnderFuturePhase(false);

    when(futureWorksDetailRepository.findByReservoirSubmissionDetail(OLD_SUBMISSION_DETAIL))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    reservoirFutureWorksDetailDuplicationService.duplicateReservoirSection(OLD_SUBMISSION_DETAIL, NEW_SUBMISSION_DETAIL);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(
            any(ReservoirSubmissionDetail.class),
            any(),
            any()
        );
  }

  @Test
  void whenEntityNotFound_thenNotCarriedForward() {
    when(futureWorksDetailRepository.findByReservoirSubmissionDetail(OLD_SUBMISSION_DETAIL))
        .thenReturn(Optional.empty());

    reservoirFutureWorksDetailDuplicationService.duplicateReservoirSection(OLD_SUBMISSION_DETAIL, NEW_SUBMISSION_DETAIL);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(
            any(ReservoirSubmissionDetail.class),
            any(),
            any()
        );
  }
}