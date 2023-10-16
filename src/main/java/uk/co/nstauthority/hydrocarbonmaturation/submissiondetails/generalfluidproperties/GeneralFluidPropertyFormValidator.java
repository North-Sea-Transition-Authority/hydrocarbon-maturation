package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ReservoirSubmissionValidator;

@Service
class GeneralFluidPropertyFormValidator implements ReservoirSubmissionValidator {

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var generalFluidPropertyForm = (GeneralFluidPropertyForm) form;
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

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

    if (submissionDetail.isCessationOfProductionSubmission()) {
      DecimalInputValidator.configureOptionalValidator(
              errors,
              "initialTemperature.decimalInputValue",
              "The initial temperature",
              generalFluidPropertyForm.getInitialTemperature())
          .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
    }

    DecimalInputValidator.configureOptionalValidator(
        errors,
        "pressureAtDatumDepth.decimalInputValue",
        "The pressure at datum depth",
        generalFluidPropertyForm.getPressureAtDatumDepth())
        .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

    if (submissionDetail.isCessationOfProductionSubmission()) {
      DecimalInputValidator.configureOptionalValidator(
              errors,
              "initialPressure.decimalInputValue",
              "The initial pressure",
              generalFluidPropertyForm.getInitialPressure())
          .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
    }

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
  public void validateFull(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var generalFluidPropertyForm = (GeneralFluidPropertyForm) form;
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    if (reservoirSubmissionDetail.getSubmissionDetail().isRelinquishment()) {
      validatePartial(form, errors, reservoirSubmissionDetail);
    } else {
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

      if (submissionDetail.isCessationOfProductionSubmission()) {
        DecimalInputValidator.configureValidator(
                errors,
                "initialTemperature.decimalInputValue",
                "The initial temperature",
                generalFluidPropertyForm.getInitialTemperature())
            .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
      }

      DecimalInputValidator.configureValidator(
              errors,
              "pressureAtDatumDepth.decimalInputValue",
              "The pressure at datum depth",
              generalFluidPropertyForm.getPressureAtDatumDepth())
          .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();

      if (submissionDetail.isCessationOfProductionSubmission()) {
        DecimalInputValidator.configureValidator(
                errors,
                "initialPressure.decimalInputValue",
                "The initial pressure",
                generalFluidPropertyForm.getInitialPressure())
            .setMaxDecimalPlaces(0).setMinValue(BigDecimal.ZERO).validate();
      }

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
}
