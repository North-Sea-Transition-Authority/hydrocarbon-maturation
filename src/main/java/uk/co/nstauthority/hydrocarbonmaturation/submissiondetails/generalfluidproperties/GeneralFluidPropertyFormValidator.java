package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class GeneralFluidPropertyFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (GeneralFluidPropertyForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(GeneralFluidPropertyForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var generalFluidPropertyForm = (GeneralFluidPropertyForm) form;

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "datumDepth.decimalInputValue",
        "The datum depth (ft tvdss)",
        generalFluidPropertyForm.getDatumDepth())
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "temperatureAtDatumDepth.decimalInputValue",
        "The temperature at datum depth (°F)",
        generalFluidPropertyForm.getTemperatureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "pressureAtDatumDepth.decimalInputValue",
        "The pressure at datum depth (psia)",
        generalFluidPropertyForm.getPressureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "saturationPressureAtDatumDepth.decimalInputValue",
        "The saturation pressure at datum depth (psia)",
        generalFluidPropertyForm.getSaturationPressureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();


    DecimalInputValidator.configureOptionalValidator(
        errors,
        "waterSalinity.decimalInputValue",
        "The water salinity (ppm)",
        generalFluidPropertyForm.getWaterSalinity())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var generalFluidPropertyForm = (GeneralFluidPropertyForm) form;

    DecimalInputValidator.configureValidator(
            errors,
            "datumDepth.decimalInputValue",
            "The datum depth (ft tvdss)",
            generalFluidPropertyForm.getDatumDepth())
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "temperatureAtDatumDepth.decimalInputValue",
            "The temperature at datum depth (°F)",
            generalFluidPropertyForm.getTemperatureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "pressureAtDatumDepth.decimalInputValue",
            "The pressure at datum depth (psia)",
            generalFluidPropertyForm.getPressureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "saturationPressureAtDatumDepth.decimalInputValue",
            "The saturation pressure at datum depth (psia)",
            generalFluidPropertyForm.getSaturationPressureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();


    DecimalInputValidator.configureValidator(
            errors,
            "waterSalinity.decimalInputValue",
            "The water salinity (ppm)",
            generalFluidPropertyForm.getWaterSalinity())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
  }
}
