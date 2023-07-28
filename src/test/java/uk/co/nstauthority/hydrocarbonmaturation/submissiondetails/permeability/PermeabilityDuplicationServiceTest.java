package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
class PermeabilityDuplicationServiceTest {
  @Mock
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @InjectMocks
  private PermeabilityDuplicationService permeabilityDuplicationService;

  @Test
  void duplicateReservoirSection_PermeabilityExists_DoesDuplicate() {
    var oldReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);
    var newReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);

    var permeability = mock(PermeabilityDetail.class);
    when(permeabilityPersistenceService.getPermeabilityDetail(oldReservoirSubmissionDetail))
        .thenReturn(Optional.of(permeability));

    permeabilityDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, permeability,
        PermeabilityDetail.class);
  }

  @Test
  void duplicateReservoirSection_PermeabilityDoesNotExist_DoesNotDuplicate() {
    var oldReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);
    var newReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);

    when(permeabilityPersistenceService.getPermeabilityDetail(oldReservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    permeabilityDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(PermeabilityDetail.class), eq(PermeabilityDetail.class));
  }
}