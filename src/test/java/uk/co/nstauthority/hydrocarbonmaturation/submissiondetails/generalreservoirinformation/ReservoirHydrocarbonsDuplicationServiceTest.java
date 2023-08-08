package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@ExtendWith(MockitoExtension.class)
class ReservoirHydrocarbonsDuplicationServiceTest {

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @InjectMocks
  private ReservoirHydrocarbonsDuplicationService reservoirHydrocarbonsDuplicationService;

  @Test
  void duplicateReservoirSection_reservoirHydrocarbonsExist_doesDuplicate() {
    var oldReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);
    var newReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);

    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(oldReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirHydrocarbonsDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(newReservoirSubmissionDetail, reservoirHydrocarbons, ReservoirHydrocarbons.class);
  }

  @Test
  void duplicateReservoirSection_reservoirHydrocarbonsDoesNotExist_doesNotDuplicate() {
    var oldReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);
    var newReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);

    when(generalReservoirInformationService.getReservoirHydrocarbons(oldReservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    reservoirHydrocarbonsDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ReservoirHydrocarbons.class), eq(ReservoirHydrocarbons.class));
  }
}