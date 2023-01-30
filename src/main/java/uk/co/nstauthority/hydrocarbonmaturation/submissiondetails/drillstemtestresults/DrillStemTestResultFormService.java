package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

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

  public boolean validateDrillStemTestResults(List<DrillStemTestResult> drillStemTestResultList) {
    var drillStemTestResultNumbers  = getDrillStemTestResultNumbers(drillStemTestResultList);

    for (var result : drillStemTestResultList) {
      var form = drillStemTestResultEntityToResultForm(result);
      var bindingResult = new BeanPropertyBindingResult(form, "form");
      drillStemTestResultNumbers.remove(result.getTestNumber());
      validate(form, bindingResult, drillStemTestResultNumbers);
      if (bindingResult.hasErrors()) {
        return true;
      }
    }
    return false;
  }

  BindingResult validateForUpdate(DrillStemTestResultForm form,
                                  BindingResult bindingResult,
                                  List<DrillStemTestResult> drillStemTestResultList,
                                  String oldNumber) {
    var drillResultTestNumbers = getDrillStemTestResultNumbers(drillStemTestResultList);
    drillResultTestNumbers.remove(oldNumber);
    drillStemTestResultFormValidator.validate(
        form, bindingResult, new DrillStemTestResultFormValidatorHints(drillResultTestNumbers));
    return bindingResult;
  }

  BindingResult validate(DrillStemTestResultForm form,
                         BindingResult bindingResult,
                         List<String> drillResultTestNumbers) {
    drillStemTestResultFormValidator.validate(
        form, bindingResult, new DrillStemTestResultFormValidatorHints(drillResultTestNumbers));
    return bindingResult;
  }

  public List<String> getDrillStemTestResultNumbers(List<DrillStemTestResult> drillStemTestResultList) {
    return drillStemTestResultList
        .stream()
        .map(DrillStemTestResult::getTestNumber)
        .collect(Collectors.toList());
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
    form.setTestNumber(drillStemTestResult.getTestNumber());

    return form;
  }
}
