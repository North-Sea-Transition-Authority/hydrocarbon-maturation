package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class ChanceOfSuccessDuplicationService implements SubmissionSectionDuplicationService {

  private final ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  ChanceOfSuccessDuplicationService(ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService,
                                    SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.chanceOfSuccessPersistenceService = chanceOfSuccessPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (!newSubmissionDetail.isProspectSubmission()) {
      return;
    }

    chanceOfSuccessPersistenceService.getChanceOfSuccess(oldSubmissionDetail)
        .ifPresent(chanceOfSuccess ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, chanceOfSuccess,
                ChanceOfSuccess.class)
        );
  }
}