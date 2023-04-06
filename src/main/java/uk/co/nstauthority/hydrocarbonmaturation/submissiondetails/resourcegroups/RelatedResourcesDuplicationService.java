package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class RelatedResourcesDuplicationService implements SubmissionSectionDuplicationService {
  private final RelatedResourcesPersistenceService relatedResourcesPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  RelatedResourcesDuplicationService(RelatedResourcesPersistenceService relatedResourcesPersistenceService,
                                     SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.relatedResourcesPersistenceService = relatedResourcesPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (oldSubmissionDetail.getSubmission().getResource().getType()
        == newSubmissionDetail.getSubmission().getResource().getType()) {
      relatedResourcesPersistenceService.getRelatedResource(oldSubmissionDetail)
          .ifPresent(relatedResource ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail,
                  relatedResource,
                  RelatedResource.class));
    }
  }
}
