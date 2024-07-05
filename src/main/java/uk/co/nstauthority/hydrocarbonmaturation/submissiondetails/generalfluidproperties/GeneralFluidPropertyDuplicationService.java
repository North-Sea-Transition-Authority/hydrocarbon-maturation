package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class GeneralFluidPropertyDuplicationService implements ReservoirSectionDuplicationService {
  private final GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  GeneralFluidPropertyDuplicationService(GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService,
                                         ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.generalFluidPropertyPersistenceService = generalFluidPropertyPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    generalFluidPropertyPersistenceService.getGeneralFluidProperty(oldReservoirSubmissionDetail)
        .ifPresent(generalFluidProperty ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newReservoirSubmissionDetail, generalFluidProperty, GeneralFluidProperty.class
            )
        );
  }
}
