package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class DrillStemTestResultPersistenceService {

  private final DrillStemTestResultRepository drillStemTestResultRepository;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @Autowired
  DrillStemTestResultPersistenceService(DrillStemTestResultRepository drillStemTestResultRepository,
                                        DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService) {
    this.drillStemTestResultRepository = drillStemTestResultRepository;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
  }

  @Transactional
  public void saveDrillStemTestResult(DrillStemTestResultForm form, DrillStemTest drillStemTest, UUID drillStemTestResultId) {
    var drillStemTestResult = getDrillStemTestResultById(drillStemTestResultId)
        .orElse(new DrillStemTestResult());
    drillStemTestResult.setDrillStemTest(drillStemTest);
    drillStemTestResult.setBhfp(form.getBhfp().createIntegerOrNull());
    drillStemTestResult.setBottomDepth(form.getBottomDepth().createBigDecimalOrNull());
    drillStemTestResult.setFlowDuration(form.getFlowDuration().createBigDecimalOrNull());
    drillStemTestResult.setFormationName(form.getFormationName());
    drillStemTestResult.setCommentsInterpretation(form.getCommentsInterpretation());
    drillStemTestResult.setGasFlowrate(form.getGasFlowrate().createBigDecimalOrNull());
    drillStemTestResult.setTopDepth(form.getTopDepth().createBigDecimalOrNull());
    drillStemTestResult.setOilFlowrate(form.getOilFlowrate().createBigDecimalOrNull());
    drillStemTestResult.setWhfp(form.getWhfp().createIntegerOrNull());
    drillStemTestResult.setWaterFlowrate(form.getWaterFlowrate().createBigDecimalOrNull());
    drillStemTestResultRepository.save(drillStemTestResult);
  }

  DrillStemTest getDrillStemTestBySubmissionDetailOrThrow(SubmissionDetail submissionDetail) {
    return drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Drill stem test had not been found for submission with ID %s"
                .formatted(submissionDetail.getId().toString())));
  }

  DrillStemTestResult getDrillStemTestResultByIdOrThrow(UUID drillStemTestResultId) {
    return getDrillStemTestResultById(drillStemTestResultId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Drill stem test had not been found with ID %s"
                .formatted(drillStemTestResultId.toString())));
  }

  public List<DrillStemTestResult> getDrillStemTestResultsBySubmissionDetail(SubmissionDetail submissionDetail) {
    return drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail)
        .map(drillStemTestResultRepository::findAllByDrillStemTest)
        .orElseGet(List::of);
  }

  List<DrillStemTestResult> getDrillStemTestResultsByDrillStemTest(DrillStemTest drillStemTest) {
    return drillStemTestResultRepository.findAllByDrillStemTest(drillStemTest);
  }

  Optional<DrillStemTestResult> getDrillStemTestResultById(UUID drillStemTestResultId) {
    return drillStemTestResultRepository.findById(drillStemTestResultId);
  }

  @Transactional
  public DrillStemTestResult createNewDrillStemTestResult(DrillStemTest drillStemTest) {
    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setDrillStemTest(drillStemTest);
    drillStemTestResultRepository.save(drillStemTestResult);
    return drillStemTestResult;
  }

  @Transactional
  public void deleteDrillStemTestResult(DrillStemTestResult drillStemTestResult) {
    drillStemTestResultRepository.delete(drillStemTestResult);
  }
}
