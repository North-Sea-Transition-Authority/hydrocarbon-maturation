package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class OilFluidPropertyDuplicationService implements SubmissionSectionDuplicationService {
  private final OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  OilFluidPropertyDuplicationService(OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService,
                                     SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.oilFluidPropertyPersistenceService = oilFluidPropertyPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (newSubmissionDetail.isDiscoverySubmission()) {
      oilFluidPropertyPersistenceService.getOilFluidProperty(oldSubmissionDetail)
          .ifPresent(oilFluidProperty ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail,
                  oilFluidProperty,
                  OilFluidProperty.class));
    }
  }
}
