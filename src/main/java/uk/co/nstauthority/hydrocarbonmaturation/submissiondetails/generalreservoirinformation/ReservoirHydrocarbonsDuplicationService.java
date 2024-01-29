package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class ReservoirHydrocarbonsDuplicationService implements ReservoirSectionDuplicationService {
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  public ReservoirHydrocarbonsDuplicationService(GeneralReservoirInformationService generalReservoirInformationService,
                                                 ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    generalReservoirInformationService.getReservoirHydrocarbons(oldReservoirSubmissionDetail)
        .ifPresent(reservoirHydrocarbons ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail,
                reservoirHydrocarbons,
                ReservoirHydrocarbons.class)
        );
  }
}
