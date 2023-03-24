package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class RelatedResourcesDuplicationService implements SubmissionSectionDuplicationService {
  private final RelatedResourcesPersistenceService opportunityDetailsPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  RelatedResourcesDuplicationService(RelatedResourcesPersistenceService opportunityDetailsPersistenceService,
                                     SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    opportunityDetailsPersistenceService.getRelatedResource(oldSubmissionDetail)
        .ifPresent(opportunityDetail ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, opportunityDetail,
                RelatedResource.class));
  }
}
