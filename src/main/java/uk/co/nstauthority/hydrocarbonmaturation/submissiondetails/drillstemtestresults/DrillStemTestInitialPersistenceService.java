package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class DrillStemTestInitialPersistenceService {

  private final DrillStemTestRepository drillStemTestRepository;

  @Autowired
  DrillStemTestInitialPersistenceService(DrillStemTestRepository drillStemTestRepository) {
    this.drillStemTestRepository = drillStemTestRepository;
  }

  @Transactional
  public DrillStemTest saveInitialDrillStemTest(DrillStemTestInitialForm form, SubmissionDetail submissionDetail) {
    var drillStemTest = getDrillStemTestBySubmissionDetail(submissionDetail)
        .orElse(new DrillStemTest());
    drillStemTest.setSubmissionDetail(submissionDetail);
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
  public DrillStemTest saveDrillStemTest(DrillStemTestSummaryForm form, SubmissionDetail submissionDetail) {
    var drillStemTest = getDrillStemTestBySubmissionDetail(submissionDetail)
        .orElse(new DrillStemTest());
    drillStemTest.setSubmissionDetail(submissionDetail);
    if (form.getDrillStemTestSummaryOption() == DrillStemTestSummaryOptions.NO) {
      //saves only the 'no' option
      drillStemTest.setDrillStemTestSummaryOption(form.getDrillStemTestSummaryOption());
    } else {
      drillStemTest.setDrillStemTestSummaryOption(null);
    }
    drillStemTestRepository.save(drillStemTest);
    return drillStemTest;
  }

  Optional<DrillStemTest> getDrillStemTestBySubmissionDetail(SubmissionDetail submissionDetail) {
    return drillStemTestRepository.findBySubmissionDetail(submissionDetail);
  }

  DrillStemTest getDrillStemTestBySubmissionDetailOrThrow(SubmissionDetail submissionDetail) {
    return getDrillStemTestBySubmissionDetail(submissionDetail)
        .orElseThrow(() -> new RemiEntityNotFoundException("Drill stem test not found for submission with id %s"
            .formatted(submissionDetail.getId().toString())));
  }
}
