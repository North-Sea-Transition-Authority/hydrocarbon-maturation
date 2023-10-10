package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
class DeleteReservoirService {

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ReservoirLinkService reservoirLinkService;

  @Autowired
  public DeleteReservoirService(ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                ReservoirLinkService reservoirLinkService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.reservoirLinkService = reservoirLinkService;
  }

  public void deleteReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    reservoirSubmissionDetailPersistenceService.deleteReservoirSubmissionDetail(reservoirSubmissionDetail);
    if (reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      var currentReservoirLinks = reservoirLinkService
          .getReservoirLinksByReservoirSubmissionDetail(reservoirSubmissionDetail);
      reservoirLinkService.deleteReservoirLinks(currentReservoirLinks);
    }
  }

  /** Can only delete discovery reservoirs if they are not linked to a field reservoir or if it's a field reservoir.
   * @param reservoirSubmissionDetail the reservoir to be deleted
   * @return true or false if the reservoir can be deleted
   */
  public boolean canDeleteReservoir(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      return true;
    }
    return !reservoirLinkService.reservoirLinksExistForReservoir(reservoirSubmissionDetail);
  }

  public List<String> getLinkedFieldNamesForDiscoveryReservoir(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirLinkService.getAssociatedFieldNamesFromLinkedDiscovery(reservoirSubmissionDetail);
  }

}
