package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class DrillStemTestDuplicationService implements SubmissionSectionDuplicationService {

  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  DrillStemTestDuplicationService(DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                  DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService,
                                  SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (newSubmissionDetail.isDiscoverySubmission()) {
      drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(oldSubmissionDetail)
          .ifPresent(drillStemTest -> {
            var newDrillStemTest = submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newSubmissionDetail,
                drillStemTest,
                DrillStemTest.class
            );

            drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest)
                .forEach(drillStemTestResult ->
                    submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                        newDrillStemTest,
                        drillStemTestResult,
                        DrillStemTestResult.class));
          });
    }
  }
}
