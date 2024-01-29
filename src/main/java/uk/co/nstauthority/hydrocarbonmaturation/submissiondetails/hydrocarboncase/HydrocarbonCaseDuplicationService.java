package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class HydrocarbonCaseDuplicationService implements SubmissionSectionDuplicationService {
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  HydrocarbonCaseDuplicationService(HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                    SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (!newSubmissionDetail.isProspectSubmission()) {
      return;
    }

    hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(oldSubmissionDetail)
        .ifPresent(hydrocarbonCaseDetail ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, hydrocarbonCaseDetail,
                HydrocarbonCaseDetail.class)
        );
  }
}
