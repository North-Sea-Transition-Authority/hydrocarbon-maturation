package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class SourceAndSealDuplicationService implements SubmissionSectionDuplicationService {
  private final SourceAndSealPersistenceService sourceAndSealPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  SourceAndSealDuplicationService(SourceAndSealPersistenceService sourceAndSealPersistenceService,
                                  SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.sourceAndSealPersistenceService = sourceAndSealPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    sourceAndSealPersistenceService.getSourceAndSeal(oldSubmissionDetail)
        .ifPresent(sourceAndSeal ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, sourceAndSeal,
                SourceAndSeal.class)
        );
  }
}
