package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class ResourceDetailDuplicationService implements SubmissionSectionDuplicationService {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  ResourceDetailDuplicationService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                   SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(oldSubmissionDetail)
        .ifPresent(resourceDetail ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, resourceDetail,
                ResourceDetail.class)
        );
  }
}
