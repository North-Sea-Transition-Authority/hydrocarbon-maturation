package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class OilFluidPropertyDuplicationService implements ReservoirSectionDuplicationService {
  private final OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;


  @Autowired
  OilFluidPropertyDuplicationService(OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService,
                                     ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.oilFluidPropertyPersistenceService = oilFluidPropertyPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    oilFluidPropertyPersistenceService.getOilFluidProperty(oldReservoirSubmissionDetail)
        .ifPresent(oilFluidProperty ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newReservoirSubmissionDetail, oilFluidProperty, OilFluidProperty.class
            )
        );
  }
}
