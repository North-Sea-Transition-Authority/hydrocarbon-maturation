package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;

@Service
class DrillStemTestResultFormValidator implements SmartValidator {

  private List<String> alreadyUsedTestNumbers;

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var hints = SubmissionFormValidationUtils
        .extractDrillStemTestResultFormHints(validationHints);
    alreadyUsedTestNumbers = hints.getDrillStemTestTestNumbers();
    validateFull((SubmissionForm) target, errors);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("You must provide DrillStemTestResultFormValidatorHints");
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return DrillStemTestResultForm.class.equals(clazz);
  }

  public void validateFull(SubmissionForm form, Errors errors) {
    var drillStemTestResultForm = (DrillStemTestResultForm) form;

    if (!StringUtils.isEmpty(drillStemTestResultForm.getTestNumber())
        && alreadyUsedTestNumbers.contains(drillStemTestResultForm.getTestNumber())) {
      errors.rejectValue(
          "testNumber",
          "testNumber.notUnique",
          "The test numbers must be unique on each submission");
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors, "testNumber", "testNumber.required", "Enter the test number");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "formationName",
        "formationName.required",
        "Enter the formation name");

    DecimalInputValidator.configureValidator(
        errors,
        "topDepth.decimalInputValue",
        "The top depth",
        drillStemTestResultForm.getTopDepth())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureValidator(
        errors,
        "bottomDepth.decimalInputValue",
        "The bottom depth",
        drillStemTestResultForm.getBottomDepth())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureValidator(
        errors,
        "bhfp.decimalInputValue",
        "The BHFP",
        drillStemTestResultForm.getBhfp())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();

    DecimalInputValidator.configureValidator(
        errors,
        "whfp.decimalInputValue",
        "The WHFP",
        drillStemTestResultForm.getWhfp())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();

    DecimalInputValidator.configureValidator(
        errors,
        "flowDuration.decimalInputValue",
        "The flow duration",
        drillStemTestResultForm.getFlowDuration())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureValidator(
        errors,
        "gasFlowrate.decimalInputValue",
        "The gas flowrate",
        drillStemTestResultForm.getGasFlowrate())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(1)
        .validate();

    DecimalInputValidator.configureValidator(
        errors,
        "oilFlowrate.decimalInputValue",
        "The oil flowrate",
        drillStemTestResultForm.getOilFlowrate())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(1)
        .validate();

    DecimalInputValidator.configureValidator(
        errors,
        "waterFlowrate.decimalInputValue",
        "The water flowrate",
        drillStemTestResultForm.getWaterFlowrate())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(1)
        .validate();
  }
}
