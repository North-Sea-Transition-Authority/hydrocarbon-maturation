package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class ReservoirLinkDuplicationService implements ReservoirSectionDuplicationService {

  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;
  private final ReservoirLinkService reservoirLinkService;

  @Autowired
  public ReservoirLinkDuplicationService(
      ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService,
      ReservoirLinkService reservoirLinkService) {
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
    this.reservoirLinkService = reservoirLinkService;
  }

  @Override
  @Transactional
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(oldReservoirSubmissionDetail)
        .forEach(reservoirLink ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail,
                reservoirLink,
                ReservoirLink.class)
        );
  }
}
