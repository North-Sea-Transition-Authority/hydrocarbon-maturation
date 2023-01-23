package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class DrillStemTestResultFormService {

  private final DrillStemTestResultFormValidator drillStemTestResultFormValidator;

  @Autowired
  DrillStemTestResultFormService(DrillStemTestResultFormValidator drillStemTestResultFormValidator) {
    this.drillStemTestResultFormValidator = drillStemTestResultFormValidator;
  }

  DrillStemTestResultForm getDrillStemTestResultForm(DrillStemTestResult drillStemTestResult) {
    return drillStemTestResultEntityToResultForm(drillStemTestResult);
  }

  public boolean validateDrillStemTestResults(List<DrillStemTestResult> drillStemTestResultList,
                                              ValidationType validationType,
                                              SubmissionDetail submissionDetail) {
    for (var result : drillStemTestResultList) {
      var form = drillStemTestResultEntityToResultForm(result);
      var bindingResult = new BeanPropertyBindingResult(form, "form");
      validate(form, bindingResult, validationType, submissionDetail);
      if (bindingResult.hasErrors()) {
        return true;
      }
    }
    return false;
  }

  BindingResult validate(DrillStemTestResultForm form,
                         BindingResult bindingResult,
                         ValidationType validationType,
                         SubmissionDetail submissionDetail) {
    drillStemTestResultFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  private DrillStemTestResultForm drillStemTestResultEntityToResultForm(DrillStemTestResult drillStemTestResult) {
    var form = new DrillStemTestResultForm();
    form.setBhfp(DecimalInput.createDecimalInput(drillStemTestResult.getBhfp()));
    form.setBottomDepth(DecimalInput.createDecimalInput(drillStemTestResult.getBottomDepth()));
    form.setCommentsInterpretation(drillStemTestResult.getCommentsInterpretation());
    form.setFlowDuration(DecimalInput.createDecimalInput(drillStemTestResult.getFlowDuration()));
    form.setFormationName(drillStemTestResult.getFormationName());
    form.setGasFlowrate(DecimalInput.createDecimalInput(drillStemTestResult.getGasFlowrate()));
    form.setOilFlowrate(DecimalInput.createDecimalInput(drillStemTestResult.getOilFlowrate()));
    form.setTopDepth(DecimalInput.createDecimalInput(drillStemTestResult.getTopDepth()));
    form.setWhfp(DecimalInput.createDecimalInput(drillStemTestResult.getWhfp()));
    form.setWaterFlowrate(DecimalInput.createDecimalInput(drillStemTestResult.getWaterFlowrate()));

    return form;
  }
}
