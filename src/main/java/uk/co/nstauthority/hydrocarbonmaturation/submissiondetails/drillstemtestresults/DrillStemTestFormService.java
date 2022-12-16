package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class DrillStemTestFormService implements SubmissionFormService {

  private final DrillStemTestInitialFormValidator drillStemTestInitialFormValidator;
  private final DrillStemTestSummaryFormValidator drillStemTestSummaryFormValidator;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @Autowired
  public DrillStemTestFormService(DrillStemTestInitialFormValidator drillStemTestInitialFormValidator,
                                  DrillStemTestSummaryFormValidator drillStemTestSummaryFormValidator,
                                  DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService) {
    this.drillStemTestInitialFormValidator = drillStemTestInitialFormValidator;
    this.drillStemTestSummaryFormValidator = drillStemTestSummaryFormValidator;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
  }

  public DrillStemTestInitialForm getDrillStemTestInitialForm(SubmissionDetail submissionDetail) {
    return drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail)
        .map(this::mapDrillStemTestEntityToInitialForm)
        .orElse(new DrillStemTestInitialForm());
  }

  public DrillStemTestSummaryForm getDrillStemTestSummaryForm(SubmissionDetail submissionDetail) {
    return drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail)
        .map(this::mapDrillStemTestEntityToSummaryForm)
        .orElse(new DrillStemTestSummaryForm());
  }

  public BindingResult validateDrillStemTestInitialForm(SubmissionForm form,
                                                        BindingResult bindingResult) {
    drillStemTestInitialFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    if (form instanceof DrillStemTestSummaryForm) {
      drillStemTestSummaryFormValidator.validate(form, bindingResult, validationType);
    } else {
      validateDrillStemTestInitialForm(form, bindingResult);
    }
    return bindingResult;
  }

  private DrillStemTestInitialForm mapDrillStemTestEntityToInitialForm(DrillStemTest drillStemTest) {
    var form = new DrillStemTestInitialForm();
    if (drillStemTest.getDrillStemTestSummaryOption() == DrillStemTestSummaryOptions.NO) {
      form.setHasDrillStemTestResult(false);
    } else {
      form.setHasDrillStemTestResult(null);
    }

    return form;
  }

  private DrillStemTestSummaryForm mapDrillStemTestEntityToSummaryForm(DrillStemTest drillStemTest) {
    var form = new DrillStemTestSummaryForm();
    form.setDrillStemTestSummaryOption(drillStemTest.getDrillStemTestSummaryOption());
    return form;
  }
}
