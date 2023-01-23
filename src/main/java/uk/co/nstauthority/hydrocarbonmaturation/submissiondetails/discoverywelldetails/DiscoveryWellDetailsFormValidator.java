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
        "wellDetailsForm.coordinateInputForm.longitudeCoordinates"
    );

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "dryHoleCost.decimalInputValue",
            "the total well cost",
            discoveryWellDetailsForm.getDryHoleCost())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "successRelatedCosts.decimalInputValue",
            "the success-related costs",
            discoveryWellDetailsForm.getDryHoleCost())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var discoveryWellDetailsForm = (DiscoveryWellDetailsForm) form;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wellDetailsForm.dryWell", "wellDetailsForm.dryWell.required",
        "Enter the dry well reference");

    CoordinateInputValidationUtil.validateMandatoryCoordinateInputForm(errors,
        discoveryWellDetailsForm.getWellDetailsForm().getCoordinateInputForm(),
        "wellDetailsForm.coordinateInputForm.latitudeCoordinates",
        "wellDetailsForm.coordinateInputForm.longitudeCoordinates",
        "wellDetailsForm.coordinateInputForm.longitudeHemisphere");

    ValidationUtils.rejectIfEmpty(errors, "wellDetailsForm.reservoirDatum",
        "wellDetailsForm.reservoirDatum.required", "Select the datum");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wellDetailsForm.totalDepthFormation",
        "wellDetailsForm.totalDepthFormation.required", "Enter the total depth formation"
    );

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "wellDetailsForm.dataAcquisitionProgramme",
        "wellDetailsForm.dataAcquisitionProgramme.required", "Enter the data acquisition programme");

    ValidationUtils.rejectIfEmpty(errors, "hydrocarbonsDiscovered",
        "hydrocarbonsDiscovered.required", "Enter the hydrocarbons discovered");

    ValidationUtils.rejectIfEmpty(errors, "discoveryStatus",
        "discoveryStatus.required", "Enter the discovery status");

    DecimalInputValidator.configureValidator(errors, "dryHoleCost.decimalInputValue",
            "the total well cost", discoveryWellDetailsForm.getDryHoleCost())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();

    DecimalInputValidator.configureValidator(errors, "successRelatedCosts.decimalInputValue",
        "the success-related costs", discoveryWellDetailsForm.getSuccessRelatedCosts())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();
  }
}
