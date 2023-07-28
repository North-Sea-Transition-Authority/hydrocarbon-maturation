package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class PermeabilityDuplicationService implements ReservoirSectionDuplicationService {
  private final PermeabilityPersistenceService permeabilityPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  PermeabilityDuplicationService(PermeabilityPersistenceService permeabilityPersistenceService,
                                 ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.permeabilityPersistenceService = permeabilityPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    permeabilityPersistenceService.getPermeabilityDetail(oldReservoirSubmissionDetail)
        .ifPresent(permeabilityDetail ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail,
                permeabilityDetail,
                PermeabilityDetail.class)
        );
  }
}
