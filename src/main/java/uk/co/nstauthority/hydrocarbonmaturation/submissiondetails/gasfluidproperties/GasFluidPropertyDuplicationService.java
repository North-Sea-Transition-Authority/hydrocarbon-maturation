package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class GasFluidPropertyDuplicationService implements ReservoirSectionDuplicationService {
  private final GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  GasFluidPropertyDuplicationService(GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService,
                                     ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.gasFluidPropertyPersistenceService = gasFluidPropertyPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    gasFluidPropertyPersistenceService.getGasFluidProperty(oldReservoirSubmissionDetail)
        .ifPresent(gasFluidProperty ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newReservoirSubmissionDetail, gasFluidProperty, GasFluidProperty.class
            )
        );
  }
}
