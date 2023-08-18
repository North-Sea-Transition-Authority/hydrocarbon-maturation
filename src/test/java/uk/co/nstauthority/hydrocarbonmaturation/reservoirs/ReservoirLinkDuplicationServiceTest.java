package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@ExtendWith(MockitoExtension.class)
class ReservoirLinkDuplicationServiceTest {

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Mock
  private ReservoirLinkService reservoirLinkService;

  @InjectMocks
  private ReservoirLinkDuplicationService reservoirLinkDuplicationService;

  @Test
  void duplicateReservoirSection_reservoirLinksExist_doesDuplicate() {
    var oldReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);
    var newReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);

    var firstReservoirLink = mock(ReservoirLink.class);
    var secondReservoirLink = mock(ReservoirLink.class);
    when(reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(oldReservoirSubmissionDetail))
        .thenReturn(List.of(firstReservoirLink, secondReservoirLink));

    reservoirLinkDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(newReservoirSubmissionDetail, firstReservoirLink, ReservoirLink.class);
    verify(reservoirDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(newReservoirSubmissionDetail, secondReservoirLink, ReservoirLink.class);
  }

  @Test
  void duplicateReservoirSection_noReservoirLinks_doesNotDuplicate() {
    var oldReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);
    var newReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);

    when(reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(oldReservoirSubmissionDetail))
        .thenReturn(List.of());

    reservoirLinkDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verifyNoInteractions(reservoirDuplicationPersistenceService);
  }
}