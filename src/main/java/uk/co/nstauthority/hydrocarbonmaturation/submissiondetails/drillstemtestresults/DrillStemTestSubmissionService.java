package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DrillStemTestSubmissionService implements SectionSubmissionService {

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
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = drillStemTestFormService.getDrillStemTestInitialForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = drillStemTestFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    var drillStemTestResults = drillStemTestResultPersistenceService.getDrillStemTestResultsBySubmissionDetail(submissionDetail);
    var hasErrors = drillStemTestResultFormService.validateDrillStemTestResults(
        drillStemTestResults);
    return !bindingResult.hasErrors() && !hasErrors;
  }
}
