package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class DrillStemTestInitialPersistenceService {

  private final DrillStemTestRepository drillStemTestRepository;

  @Autowired
  DrillStemTestInitialPersistenceService(DrillStemTestRepository drillStemTestRepository) {
    this.drillStemTestRepository = drillStemTestRepository;
  }

  @Transactional
  public void saveInitialDrillStemTest(DrillStemTestInitialForm form, SubmissionDetail submissionDetail) {
    var drillStemTest = getDrillStemTestBySubmissionDetail(submissionDetail)
        .orElse(new DrillStemTest());
    drillStemTest.setSubmissionDetail(submissionDetail);
    drillStemTest.setHasDrillStemTestResults(form.getHasDrillStemTestResult());
    drillStemTestRepository.save(drillStemTest);
  }

  Optional<DrillStemTest> getDrillStemTestBySubmissionDetail(SubmissionDetail submissionDetail) {
    return drillStemTestRepository.findBySubmissionDetail(submissionDetail);
  }
}
