package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.util.Objects;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
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
    if (!Objects.equals(newSubmissionDetail.getSubmissionType(), SubmissionType.PROSPECT_UPDATE)
        && !Objects.equals(newSubmissionDetail.getSubmissionType(), SubmissionType.RELINQUISHMENT_PROSPECT)) {
      return;
    }

    chanceOfSuccessPersistenceService.getChanceOfSuccess(oldSubmissionDetail)
        .ifPresent(chanceOfSuccess ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, chanceOfSuccess,
                ChanceOfSuccess.class)
        );
  }
}