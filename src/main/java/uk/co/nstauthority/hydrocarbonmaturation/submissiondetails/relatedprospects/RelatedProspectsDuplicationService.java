package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedprospects;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class RelatedProspectsDuplicationService implements SubmissionSectionDuplicationService {
  private final RelatedProspectsPersistenceService opportunityDetailsPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  RelatedProspectsDuplicationService(RelatedProspectsPersistenceService opportunityDetailsPersistenceService,
                                   SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    opportunityDetailsPersistenceService.getRelatedProspect(oldSubmissionDetail)
        .ifPresent(opportunityDetail ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, opportunityDetail,
                RelatedProspect.class));
  }
}
