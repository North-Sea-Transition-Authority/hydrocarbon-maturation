package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.CoordinateInputValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class DiscoveryWellDetailsFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return DiscoveryWellDetailsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var discoveryWellDetailsForm = (DiscoveryWellDetailsForm) form;

    CoordinateInputValidationUtil.validateOptionalCoordinateInputForm(
        errors,
        discoveryWellDetailsForm.getWellDetailsForm().getCoordinateInputForm(),
        "wellDetailsForm.coordinateInputForm.latitudeCoordinates",
        "wellDetailsForm.coordinateInputForm.longitudeCoordinates",
        "top reservoir latitude",
        "top reservoir longitude"
    );

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "dryHoleCost.decimalInputValue",
            "The total well cost",
            discoveryWellDetailsForm.getDryHoleCost())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    var successRelatedCostValidator = DecimalInputValidator.configureOptionalValidator(
            errors,
            "successRelatedCosts.decimalInputValue",
            "The success-related costs",
            discoveryWellDetailsForm.getSuccessRelatedCosts());

    successRelatedCostValidator
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();
    if (errors.getFieldErrors("successRelatedCosts.decimalInputValue").isEmpty()) {
      successRelatedCostValidator
          .setMaxValue(discoveryWellDetailsForm.getDryHoleCost().createBigDecimalOrNull())
          .overrideDefaultErrorMessage("The success related-costs must be less than or equal to total well cost of %s"
              .formatted(discoveryWellDetailsForm.getDryHoleCost().createBigDecimalOrNull()))
          .validate();
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var discoveryWellDetailsForm = (DiscoveryWellDetailsForm) form;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wellDetailsForm.well", "wellDetailsForm.well.required",
        "Enter the well reference");

    CoordinateInputValidationUtil.validateMandatoryCoordinateInputForm(errors,
        discoveryWellDetailsForm.getWellDetailsForm().getCoordinateInputForm(),
        "wellDetailsForm.coordinateInputForm.latitudeCoordinates",
        "wellDetailsForm.coordinateInputForm.longitudeCoordinates",
        "top reservoir latitude",
        "top reservoir longitude",
        "wellDetailsForm.coordinateInputForm.longitudeHemisphere");

    ValidationUtils.rejectIfEmpty(errors, "wellDetailsForm.reservoirDatum",
        "wellDetailsForm.reservoirDatum.required", "Select the top reservoir datum");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wellDetailsForm.totalDepthFormation",
        "wellDetailsForm.totalDepthFormation.required", "Enter the total depth formation"
    );

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wellDetailsForm.dataAcquisitionProgramme",
        "wellDetailsForm.dataAcquisitionProgramme.required", "Enter the data acquisition programme");

    ValidationUtils.rejectIfEmpty(errors, "discoveryStatus",
        "discoveryStatus.required", "Enter the discovery status");

    DecimalInputValidator.configureValidator(errors, "dryHoleCost.decimalInputValue",
            "The total well cost", discoveryWellDetailsForm.getDryHoleCost())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    var successRelatedCostValidator = DecimalInputValidator.configureValidator(
        errors,
        "successRelatedCosts.decimalInputValue",
        "The success-related costs",
        discoveryWellDetailsForm.getSuccessRelatedCosts());

    successRelatedCostValidator
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();
    if (errors.getFieldErrors("successRelatedCosts.decimalInputValue").isEmpty()) {
      successRelatedCostValidator
          .setMaxValue(discoveryWellDetailsForm.getDryHoleCost().createBigDecimalOrNull())
          .overrideDefaultErrorMessage("The success related-costs must be less than or equal to total well cost of %s"
              .formatted(discoveryWellDetailsForm.getDryHoleCost().createBigDecimalOrNull()))
          .validate();
    }
  }
}
