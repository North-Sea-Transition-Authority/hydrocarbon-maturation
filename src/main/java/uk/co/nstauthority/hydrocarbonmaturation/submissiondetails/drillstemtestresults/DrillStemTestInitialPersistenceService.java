package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class DrillStemTestInitialPersistenceService {

  private final DrillStemTestRepository drillStemTestRepository;

  @Autowired
  DrillStemTestInitialPersistenceService(DrillStemTestRepository drillStemTestRepository) {
    this.drillStemTestRepository = drillStemTestRepository;
  }

  @Transactional
  public DrillStemTest saveInitialDrillStemTest(DrillStemTestInitialForm form,
                                                ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var drillStemTest = getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .orElse(new DrillStemTest());
    drillStemTest.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    if (BooleanUtils.isFalse(form.getHasDrillStemTestResult())) {
      //saves only the 'no' option
      drillStemTest.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.NO);
    } else {
      drillStemTest.setDrillStemTestSummaryOption(null);
    }
    drillStemTestRepository.save(drillStemTest);
    return drillStemTest;
  }

  @Transactional
  public DrillStemTest saveDrillStemTest(DrillStemTestSummaryForm form,
                                         ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var drillStemTest = getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .orElse(new DrillStemTest());
    drillStemTest.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    if (form.getDrillStemTestSummaryOption() == DrillStemTestSummaryOptions.NO) {
      //saves only the 'no' option
      drillStemTest.setDrillStemTestSummaryOption(form.getDrillStemTestSummaryOption());
    } else {
      drillStemTest.setDrillStemTestSummaryOption(null);
    }
    drillStemTestRepository.save(drillStemTest);
    return drillStemTest;
  }

  @Transactional
  public void deleteDrillStemTest(DrillStemTest drillStemTest) {
    drillStemTestRepository.delete(drillStemTest);
  }

  Optional<DrillStemTest> getDrillStemTestByReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return drillStemTestRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  DrillStemTest getDrillStemTestByReservoirSubmissionDetailOrThrow(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .orElseThrow(() -> new RemiEntityNotFoundException("Drill stem test not found for submission with id %s"
            .formatted(reservoirSubmissionDetail.getId().toString())));
  }
}
