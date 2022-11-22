package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import java.math.BigDecimal;
import org.apache.commons.lang3.BooleanUtils;
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

    if (BooleanUtils.isTrue(gasFluidPropertyForm.getCondensatePresent())) {
      DecimalInputValidator.configureOptionalValidator(
          errors,
          "condensateGravity.decimalInputValue",
          "The condensate gravity (°API)",
          gasFluidPropertyForm.getCondensateGravity())
          .setMinValue(BigDecimal.ZERO).setMaxDecimalPlaces(0).validate();

      DecimalInputValidator.configureOptionalValidator(
          errors,
          "condensateViscosity.decimalInputValue",
          "The condensate viscosity (cP)",
          gasFluidPropertyForm.getCondensateViscosity())
          .setMinValue(BigDecimal.ZERO).setMaxDecimalPlaces(1).validate();

      DecimalInputValidator.configureOptionalValidator(
          errors,
          "condensateSulphur.decimalInputValue",
          "The condensate sulphur (ppm)",
          gasFluidPropertyForm.getCondensateSulphur())
          .setMinValue(BigDecimal.ZERO).setMaxDecimalPlaces(0).validate();

      PercentageValidationUtils.configurePercentageValidator(
          gasFluidPropertyForm.getCondensateWax(),
          "condensateWax.decimalInputValue",
          "The condensate wax (weight%)",
          errors)
          .setMinValue(BigDecimal.ZERO).isOptional().setMaxDecimalPlaces(2).validate();

      DecimalInputValidator.configureOptionalValidator(
          errors,
          "condensateOtherInclWax.decimalInputValue",
          "The condensate other incl wax appearance temperature (oF)",
          gasFluidPropertyForm.getCondensateOtherInclWax())
          .setMinValue(BigDecimal.ZERO).setMaxDecimalPlaces(0).validate();
    }

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

    if (BooleanUtils.isTrue(gasFluidPropertyForm.getCondensatePresent())) {
      DecimalInputValidator.configureValidator(
          errors,
          "condensateGravity.decimalInputValue",
          "The condensate gravity (°API)",
          gasFluidPropertyForm.getCondensateGravity())
          .setMinValue(BigDecimal.ZERO).setMaxDecimalPlaces(0).validate();

      DecimalInputValidator.configureValidator(
          errors,
          "condensateViscosity.decimalInputValue",
          "The condensate viscosity (cP)",
          gasFluidPropertyForm.getCondensateViscosity())
          .setMinValue(BigDecimal.ZERO).setMaxDecimalPlaces(1).validate();

      DecimalInputValidator.configureValidator(
          errors,
          "condensateSulphur.decimalInputValue",
          "The condensate sulphur (ppm)",
          gasFluidPropertyForm.getCondensateSulphur())
          .setMinValue(BigDecimal.ZERO).setMaxDecimalPlaces(0).validate();

      PercentageValidationUtils.configurePercentageValidator(
          gasFluidPropertyForm.getCondensateWax(),
          "condensateWax.decimalInputValue",
          "The condensate wax (weight%)",
          errors)
          .setMinValue(BigDecimal.ZERO).setMaxDecimalPlaces(2).validate();

      DecimalInputValidator.configureValidator(
          errors,
          "condensateOtherInclWax.decimalInputValue",
          "The condensate other incl wax appearance temperature (oF)",
          gasFluidPropertyForm.getCondensateOtherInclWax())
          .setMinValue(BigDecimal.ZERO).setMaxDecimalPlaces(0).validate();
    }
  }
}
