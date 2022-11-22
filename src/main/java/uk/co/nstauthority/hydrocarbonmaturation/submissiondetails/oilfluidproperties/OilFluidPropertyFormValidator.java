package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PercentageValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class OilFluidPropertyFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (OilFluidPropertyForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(OilFluidPropertyForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var oiLFluidPropertyForm = (OilFluidPropertyForm) form;

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "oilGravity.decimalInputValue",
        "The oil gravity (°API)",
        oiLFluidPropertyForm.getOilGravity())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "oilViscosity.decimalInputValue",
            "The oil viscosity (cP)",
            oiLFluidPropertyForm.getOilViscosity())
        .setMaxDecimalPlaces(1).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "oilSulphur.decimalInputValue",
            "The oil sulphur (ppm)",
            oiLFluidPropertyForm.getOilSulphur())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    PercentageValidationUtils.configurePercentageValidator(
            oiLFluidPropertyForm.getOilWax(),
            "oilWax.decimalInputValue",
            "The oil wax (weight%)",
            errors)
        .setMaxDecimalPlaces(2).isOptional().setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "oilOtherWaxAppearanceTemperature.decimalInputValue",
            "The oil other incl wax appearance temperature (°F)",
            oiLFluidPropertyForm.getOilOtherWaxAppearanceTemperature())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var oiLFluidPropertyForm = (OilFluidPropertyForm) form;

    DecimalInputValidator.configureValidator(
            errors,
            "oilGravity.decimalInputValue",
            "The oil gravity (°API)",
            oiLFluidPropertyForm.getOilGravity())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "oilViscosity.decimalInputValue",
            "The oil viscosity (cP)",
            oiLFluidPropertyForm.getOilViscosity())
        .setMaxDecimalPlaces(1).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "oilSulphur.decimalInputValue",
            "The oil sulphur (ppm)",
            oiLFluidPropertyForm.getOilSulphur())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    PercentageValidationUtils.configurePercentageValidator(
            oiLFluidPropertyForm.getOilWax(),
            "oilWax.decimalInputValue",
            "The oil wax (weight%)",
            errors)
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "oilOtherWaxAppearanceTemperature.decimalInputValue",
            "The oil other incl wax appearance temperature (°F)",
            oiLFluidPropertyForm.getOilOtherWaxAppearanceTemperature())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
  }
}
