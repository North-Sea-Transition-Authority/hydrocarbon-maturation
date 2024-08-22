package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DrillStemTestSubmissionService implements ReservoirSectionSubmissionService {

  private final DrillStemTestFormService drillStemTestFormService;
  private final DrillStemTestResultFormService drillStemTestResultFormService;
  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @Autowired
  public DrillStemTestSubmissionService(DrillStemTestFormService drillStemTestFormService,
                                        DrillStemTestResultFormService drillStemTestResultFormService,
                                        DrillStemTestResultPersistenceService drillStemTestResultPersistenceService) {
    this.drillStemTestFormService = drillStemTestFormService;
    this.drillStemTestResultFormService = drillStemTestResultFormService;
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var form = drillStemTestFormService.getDrillStemTestInitialForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = drillStemTestFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail, null);
    var drillStemTestResults = drillStemTestResultPersistenceService
        .getDrillStemTestResultsByReservoirSubmissionDetail(reservoirSubmissionDetail);
    var hasErrors = drillStemTestResultFormService.validateDrillStemTestResults(
        drillStemTestResults);
    return !bindingResult.hasErrors() && !hasErrors;
  }
}
