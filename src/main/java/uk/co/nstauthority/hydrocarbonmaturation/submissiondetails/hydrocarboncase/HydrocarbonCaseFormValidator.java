package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import java.math.BigDecimal;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PercentageValidationUtils;

@Service
class HydrocarbonCaseFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return HydrocarbonCaseForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var hydrocarbonCaseForm = (HydrocarbonCaseForm) form;

    if (hydrocarbonCaseForm.isOilOnlyCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getOilOnlyProbability(),
          "oilOnlyProbability.decimalInputValue", "The oil only probability", errors)
          .isOptional()
          .validate();
    }

    if (hydrocarbonCaseForm.isGasOnlyCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getGasOnlyProbability(),
          "gasOnlyProbability.decimalInputValue", "The gas only probability", errors)
          .isOptional()
          .validate();
    }

    if (hydrocarbonCaseForm.isOilAndGasCaseType()) {
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
    } else {
      checkAddsUpTo100(hydrocarbonCaseForm, errors);
    }

    if (hydrocarbonCaseForm.isOilOnlyCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getOilOnlyProbability(),
          "oilOnlyProbability.decimalInputValue", "The oil only probability", errors)
          .validate();
    }

    if (hydrocarbonCaseForm.isGasOnlyCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getGasOnlyProbability(),
          "gasOnlyProbability.decimalInputValue", "The gas only probability", errors)
          .validate();
    }

    if (hydrocarbonCaseForm.isOilAndGasCaseType()) {
      PercentageValidationUtils.configurePercentageValidator(hydrocarbonCaseForm.getOilAndGasProbability(),
          "oilAndGasProbability.decimalInputValue", "The oil and gas probability", errors)
          .validate();
    }
  }

  private void checkAddsUpTo100(HydrocarbonCaseForm hydrocarbonCaseForm, Errors errors) {
    BigDecimal total = BigDecimal.ZERO;

    if (hydrocarbonCaseForm.isGasOnlyCaseType()
        && hydrocarbonCaseForm.getGasOnlyProbability() != null) {
      total = total.add(hydrocarbonCaseForm.getGasOnlyProbability()
          .asBigDecimal()
          .orElse(BigDecimal.ZERO));
    }

    if (hydrocarbonCaseForm.isOilOnlyCaseType()
        && hydrocarbonCaseForm.getOilOnlyProbability() != null) {
      total = total.add(hydrocarbonCaseForm.getOilOnlyProbability()
          .asBigDecimal()
          .orElse(BigDecimal.ZERO));
    }

    if (hydrocarbonCaseForm.isOilAndGasCaseType()
        && hydrocarbonCaseForm.getOilAndGasProbability() != null) {
      total = total.add(hydrocarbonCaseForm.getOilAndGasProbability()
          .asBigDecimal()
          .orElse(BigDecimal.ZERO));
    }

    if (total.compareTo(BigDecimal.valueOf(100)) != 0) {
      errors.rejectValue(
          "oilOnlyCaseType", "oilOnlyCaseType.decimalInputError",
          String.format("The selected cases must add up to 100 percent. Currently they add up to %s percent.", total));
    }
  }


}
