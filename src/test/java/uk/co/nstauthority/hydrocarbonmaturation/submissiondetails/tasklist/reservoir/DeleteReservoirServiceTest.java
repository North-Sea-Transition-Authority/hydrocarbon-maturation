package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirLink;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class DeleteReservoirServiceTest {

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private ReservoirLinkService reservoirLinkService;

  @InjectMocks
  private DeleteReservoirService deleteReservoirService;

  @Test
  void deleteReservoirSubmissionDetail_discovery() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    deleteReservoirService.deleteReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .deleteReservoirSubmissionDetail(reservoirSubmissionDetail);
    verifyNoInteractions(reservoirLinkService);
  }

  @Test
  void deleteReservoirSubmissionDetail_field() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(SubmissionType.CREATE_CSR);
    var linkedReservoirs = List.of(new ReservoirLink(), new ReservoirLink());
    when(reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(reservoirSubmissionDetail)).thenReturn(linkedReservoirs);
    deleteReservoirService.deleteReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .deleteReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(reservoirLinkService, times(1)).deleteReservoirLinks(linkedReservoirs);
  }

  @Test
  void canDeleteReservoir_field() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(SubmissionType.CREATE_CSR);
    assertTrue(deleteReservoirService.canDeleteReservoir(reservoirSubmissionDetail));
  }

  @Test
  void canDeleteReservoir_noReservoirsLinked() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirLinkService.reservoirLinksExistForReservoir(reservoirSubmissionDetail)).thenReturn(false);
    assertTrue(deleteReservoirService.canDeleteReservoir(reservoirSubmissionDetail));
  }

  @Test
  void canDeleteReservoir_reservoirsLinked() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirLinkService.reservoirLinksExistForReservoir(reservoirSubmissionDetail)).thenReturn(true);
    assertFalse(deleteReservoirService.canDeleteReservoir(reservoirSubmissionDetail));
  }

  @Test
  void getLinkedFieldNamesForDiscoveryReservoir() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    deleteReservoirService.getLinkedFieldNamesForDiscoveryReservoir(reservoirSubmissionDetail);
    verify(reservoirLinkService, times(1)).getAssociatedFieldNamesFromLinkedDiscovery(reservoirSubmissionDetail);
  }
}
