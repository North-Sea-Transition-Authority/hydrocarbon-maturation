package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import java.math.BigDecimal;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;

@Service
class HydrocarbonCaseFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return HydrocarbonCaseForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (HydrocarbonCaseForm) target;

    if (ObjectUtils.allNull(form.getOilOnlyCaseType(), form.getGasOnlyCaseType(), form.getOilAndGasCaseType())) {
      errors.rejectValue("oilOnlyCaseType", "oilOnlyCaseType.required", "Select a hydrocarbon case type");
      errors.rejectValue("gasOnlyCaseType", "gasOnlyCaseType.required", "");
      errors.rejectValue("oilAndGasCaseType", "oilAndGasCaseType.required", "");
    }

    if (form.getOilOnlyCaseType() != null && form.getOilOnlyCaseType()) {
      DecimalInputValidator.configureValidator(errors, "oilOnlyProbability.decimalInputValue", "The oil only probability",
          form.getOilOnlyProbability())
          .setMinValue(BigDecimal.ONE)
          .setMaxValue(BigDecimal.valueOf(100))
          .setMaxDecimalPlaces(0)
          .validate();
    }

    if (form.getGasOnlyCaseType() != null && form.getGasOnlyCaseType()) {
      DecimalInputValidator.configureValidator(errors, "gasOnlyProbability.decimalInputValue", "The gas only probability",
          form.getGasOnlyProbability())
          .setMinValue(BigDecimal.ONE)
          .setMaxValue(BigDecimal.valueOf(100))
          .setMaxDecimalPlaces(0)
          .validate();
    }

    if (form.getOilAndGasCaseType() != null && form.getOilAndGasCaseType()) {
      DecimalInputValidator.configureValidator(errors, "oilAndGasProbability.decimalInputValue", "The oil and gas probability",
          form.getOilAndGasProbability())
          .setMinValue(BigDecimal.ONE)
          .setMaxValue(BigDecimal.valueOf(100))
          .setMaxDecimalPlaces(0)
          .validate();
    }
  }
}
