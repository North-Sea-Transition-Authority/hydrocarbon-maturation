package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class DrillStemTestDuplicationService implements ReservoirSectionDuplicationService {

  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  DrillStemTestDuplicationService(DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                  DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService,
                                  ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(oldReservoirSubmissionDetail)
        .ifPresent(drillStemTest -> {
          var newDrillStemTest = reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
              newReservoirSubmissionDetail, drillStemTest, DrillStemTest.class
          );

          drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest)
              .forEach(drillStemTestResult ->
                  reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
                      newDrillStemTest, drillStemTestResult, DrillStemTestResult.class
                  )
              );
        });
  }
}
