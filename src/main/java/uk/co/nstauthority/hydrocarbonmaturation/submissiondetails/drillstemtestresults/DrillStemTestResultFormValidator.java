package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;

@Service
class DrillStemTestResultFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (DrillStemTestResultForm) target;
    var validationType = SubmissionFormValidationUtils.extractValidationTypeFromHints(validationHints);

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return DrillStemTestResultForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Validation type must be either full or partial");
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var drillStemTestResultForm = (DrillStemTestResultForm) form;

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "topDepth.decimalInputValue",
        "The top depth",
        drillStemTestResultForm.getTopDepth())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "bottomDepth.decimalInputValue",
        "The bottom depth",
        drillStemTestResultForm.getBottomDepth())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "bhfp.decimalInputValue",
        "The BHFP",
        drillStemTestResultForm.getBhfp())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "whfp.decimalInputValue",
        "The WHFP",
        drillStemTestResultForm.getWhfp())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(0)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "flowDuration.decimalInputValue",
        "The flow duration",
        drillStemTestResultForm.getFlowDuration())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "gasFlowrate.decimalInputValue",
        "The gas flowrate",
        drillStemTestResultForm.getGasFlowrate())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(1)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "oilFlowrate.decimalInputValue",
        "The oil flowrate",
        drillStemTestResultForm.getOilFlowrate())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(1)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "waterFlowrate.decimalInputValue",
        "The water flowrate",
        drillStemTestResultForm.getWaterFlowrate())
        .setMinValue(BigDecimal.ZERO)
        .setMaxDecimalPlaces(1)
        .validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var drillStemTestResultForm = (DrillStemTestResultForm) form;

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
