package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class DrillStemTestInitialFormService implements SubmissionFormService {

  private final DrillStemTestInitialFormValidator drillStemTestInitialFormValidator;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @Autowired
  DrillStemTestInitialFormService(DrillStemTestInitialFormValidator drillStemTestInitialFormValidator,
                                         DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService) {
    this.drillStemTestInitialFormValidator = drillStemTestInitialFormValidator;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
  }

  DrillStemTestInitialForm getDrillStemTestInitialForm(SubmissionDetail submissionDetail) {
    return drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail)
        .map(this::drillStemTestEntityToInitialForm)
        .orElse(new DrillStemTestInitialForm());
  }

  private DrillStemTestInitialForm drillStemTestEntityToInitialForm(DrillStemTest drillStemTest) {
    var form = new DrillStemTestInitialForm();
    form.setHasDrillStemTestResult(drillStemTest.getHasDrillStemTestResults());
    return form;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    drillStemTestInitialFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
