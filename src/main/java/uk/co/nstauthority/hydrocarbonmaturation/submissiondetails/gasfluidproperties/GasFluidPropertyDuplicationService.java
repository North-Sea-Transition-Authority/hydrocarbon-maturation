package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class GasFluidPropertyDuplicationService implements SubmissionSectionDuplicationService {
  private final GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  GasFluidPropertyDuplicationService(GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService,
                                     SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.gasFluidPropertyPersistenceService = gasFluidPropertyPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (newSubmissionDetail.isDiscoverySubmission()) {
      gasFluidPropertyPersistenceService.getGasFluidProperty(oldSubmissionDetail)
          .ifPresent(gasFluidProperty ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail,
                  gasFluidProperty,
                  GasFluidProperty.class));
    }
  }
}
