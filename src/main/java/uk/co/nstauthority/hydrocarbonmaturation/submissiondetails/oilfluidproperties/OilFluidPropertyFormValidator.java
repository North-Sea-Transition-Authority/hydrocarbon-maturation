package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PercentageValidationUtils;

@Service
class OilFluidPropertyFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(OilFluidPropertyForm.class);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var oiLFluidPropertyForm = (OilFluidPropertyForm) form;

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "oilGravity.decimalInputValue",
        "The oil gravity",
        oiLFluidPropertyForm.getOilGravity())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "oilViscosity.decimalInputValue",
            "The oil viscosity",
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
            "The oil wax",
            errors)
        .setMaxDecimalPlaces(2).isOptional().setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "oilOtherWaxAppearanceTemperature.decimalInputValue",
            "The oil other incl wax appearance temperature",
            oiLFluidPropertyForm.getOilOtherWaxAppearanceTemperature())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var oiLFluidPropertyForm = (OilFluidPropertyForm) form;

    DecimalInputValidator.configureValidator(
            errors,
            "oilGravity.decimalInputValue",
            "The oil gravity",
            oiLFluidPropertyForm.getOilGravity())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "oilViscosity.decimalInputValue",
            "The oil viscosity",
            oiLFluidPropertyForm.getOilViscosity())
        .setMaxDecimalPlaces(1).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "oilSulphur.decimalInputValue",
            "The oil sulphur",
            oiLFluidPropertyForm.getOilSulphur())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    PercentageValidationUtils.configurePercentageValidator(
            oiLFluidPropertyForm.getOilWax(),
            "oilWax.decimalInputValue",
            "The oil wax",
            errors)
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "oilOtherWaxAppearanceTemperature.decimalInputValue",
            "The oil other incl wax appearance temperature",
            oiLFluidPropertyForm.getOilOtherWaxAppearanceTemperature())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
  }
}
