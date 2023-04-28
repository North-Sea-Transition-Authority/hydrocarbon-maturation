package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class GeneralFluidPropertyDuplicationService implements SubmissionSectionDuplicationService {
  private final GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  GeneralFluidPropertyDuplicationService(GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService,
                                     SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.generalFluidPropertyPersistenceService = generalFluidPropertyPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (newSubmissionDetail.isDiscoverySubmission()) {
      generalFluidPropertyPersistenceService.getGeneralFluidProperty(oldSubmissionDetail)
          .ifPresent(generalFluidProperty ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail,
                  generalFluidProperty,
                  GeneralFluidProperty.class));
    }
  }
}
