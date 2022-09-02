package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PercentageValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class HydrocarbonCaseFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return HydrocarbonCaseForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (HydrocarbonCaseForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var hydrocarbonCaseForm = (HydrocarbonCaseForm) form;

    if (hydrocarbonCaseForm.getOilOnlyCaseType() != null && hydrocarbonCaseForm.getOilOnlyCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getOilOnlyProbability(),
          "oilOnlyProbability.decimalInputValue", "The oil only probability", errors)
          .isOptional()
          .validate();
    }

    if (hydrocarbonCaseForm.getGasOnlyCaseType() != null && hydrocarbonCaseForm.getGasOnlyCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getGasOnlyProbability(),
          "gasOnlyProbability.decimalInputValue", "The gas only probability", errors)
          .isOptional()
          .validate();
    }

    if (hydrocarbonCaseForm.getOilAndGasCaseType() != null && hydrocarbonCaseForm.getOilAndGasCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getOilAndGasProbability(),
          "oilAndGasProbability.decimalInputValue", "The oil and gas probability", errors)
          .isOptional()
          .validate();
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var hydrocarbonCaseForm = (HydrocarbonCaseForm) form;

    if (ObjectUtils.allNull(hydrocarbonCaseForm.getOilOnlyCaseType(),
        hydrocarbonCaseForm.getGasOnlyCaseType(), hydrocarbonCaseForm.getOilAndGasCaseType())) {
      errors.rejectValue("oilOnlyCaseType", "oilOnlyCaseType.required", "Select a hydrocarbon case type");
      errors.rejectValue("gasOnlyCaseType", "gasOnlyCaseType.required", "");
      errors.rejectValue("oilAndGasCaseType", "oilAndGasCaseType.required", "");
    }

    if (hydrocarbonCaseForm.getOilOnlyCaseType() != null && hydrocarbonCaseForm.getOilOnlyCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getOilOnlyProbability(),
          "oilOnlyProbability.decimalInputValue", "The oil only probability", errors)
          .validate();
    }

    if (hydrocarbonCaseForm.getGasOnlyCaseType() != null && hydrocarbonCaseForm.getGasOnlyCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getGasOnlyProbability(),
          "gasOnlyProbability.decimalInputValue", "The gas only probability", errors)
          .validate();
    }

    if (hydrocarbonCaseForm.getOilAndGasCaseType() != null && hydrocarbonCaseForm.getOilAndGasCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getOilAndGasProbability(),
          "oilAndGasProbability.decimalInputValue", "The oil and gas probability", errors)
          .validate();
    }
  }

}
