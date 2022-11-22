package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PercentageValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class GasFluidPropertyFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (GasFluidPropertyForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(GasFluidPropertyForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var gasFluidPropertyForm = (GasFluidPropertyForm) form;

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "gasSpecificGravity.decimalInputValue",
        "The gas specific gravity (air=1)",
        gasFluidPropertyForm.getGasSpecificGravity())
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "gasH2s.decimalInputValue",
        "The gas H₂S (mol%)",
        gasFluidPropertyForm.getGasH2s())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    PercentageValidationUtils.configurePercentageValidator(
        gasFluidPropertyForm.getGasCo2(),
        "gasCo2.decimalInputValue",
        "The gas CO₂ (mol%)",
        errors)
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).isOptional().validate();

    PercentageValidationUtils.configurePercentageValidator(
        gasFluidPropertyForm.getGasN2(),
        "gasN2.decimalInputValue",
        "The gas N₂ (mol%)",
        errors)
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).isOptional().validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var gasFluidPropertyForm = (GasFluidPropertyForm) form;

    DecimalInputValidator.configureValidator(
        errors,
        "gasSpecificGravity.decimalInputValue",
        "The gas specific gravity (air=1)",
        gasFluidPropertyForm.getGasSpecificGravity())
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
        errors,
        "gasH2s.decimalInputValue",
        "The gas H₂S (ppm by volume)",
        gasFluidPropertyForm.getGasH2s())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    PercentageValidationUtils.configurePercentageValidator(
        gasFluidPropertyForm.getGasCo2(),
        "gasCo2.decimalInputValue",
        "The gas CO₂ (mol%)",
        errors)
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    PercentageValidationUtils.configurePercentageValidator(
        gasFluidPropertyForm.getGasN2(),
        "gasN2.decimalInputValue",
        "The gas N₂ (mol%)",
        errors)
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    ValidationUtils.rejectIfEmpty(
        errors,
        "condensatePresent",
        "condensatePresent.required",
        "Select yes if condensate is present");
  }
}
