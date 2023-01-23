package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class GeneralFluidPropertyFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(GeneralFluidPropertyForm.class);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var generalFluidPropertyForm = (GeneralFluidPropertyForm) form;

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "datumDepth.decimalInputValue",
        "The datum depth",
        generalFluidPropertyForm.getDatumDepth())
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "temperatureAtDatumDepth.decimalInputValue",
        "The temperature at datum depth",
        generalFluidPropertyForm.getTemperatureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "pressureAtDatumDepth.decimalInputValue",
        "The pressure at datum depth",
        generalFluidPropertyForm.getPressureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "saturationPressureAtDatumDepth.decimalInputValue",
        "The saturation pressure at datum depth",
        generalFluidPropertyForm.getSaturationPressureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();


    DecimalInputValidator.configureOptionalValidator(
        errors,
        "waterSalinity.decimalInputValue",
        "The water salinity",
        generalFluidPropertyForm.getWaterSalinity())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var generalFluidPropertyForm = (GeneralFluidPropertyForm) form;

    DecimalInputValidator.configureValidator(
            errors,
            "datumDepth.decimalInputValue",
            "The datum depth",
            generalFluidPropertyForm.getDatumDepth())
        .setMaxDecimalPlaces(2).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "temperatureAtDatumDepth.decimalInputValue",
            "The temperature at datum depth",
            generalFluidPropertyForm.getTemperatureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "pressureAtDatumDepth.decimalInputValue",
            "The pressure at datum depth",
            generalFluidPropertyForm.getPressureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    DecimalInputValidator.configureValidator(
            errors,
            "saturationPressureAtDatumDepth.decimalInputValue",
            "The saturation pressure at datum depth",
            generalFluidPropertyForm.getSaturationPressureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();


    DecimalInputValidator.configureValidator(
            errors,
            "waterSalinity.decimalInputValue",
            "The water salinity",
            generalFluidPropertyForm.getWaterSalinity())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
  }
}
