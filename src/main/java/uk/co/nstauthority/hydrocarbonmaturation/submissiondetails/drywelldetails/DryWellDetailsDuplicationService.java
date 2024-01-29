package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class DryWellDetailsDuplicationService implements SubmissionSectionDuplicationService {
  private final DryWellDetailsPersistenceService dryWellDetailsPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  DryWellDetailsDuplicationService(DryWellDetailsPersistenceService dryWellDetailsPersistenceService,
                                   SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.dryWellDetailsPersistenceService = dryWellDetailsPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(oldSubmissionDetail)
        .ifPresent(dryWellDetail ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, dryWellDetail,
                DryWellDetail.class)
        );
  }
}
