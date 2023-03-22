package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.CoordinateInputValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class DryWellDetailsFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return DryWellDetailsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var dryWellForm = (DryWellDetailsForm) form;

    CoordinateInputValidationUtil.validateOptionalCoordinateInputForm(
        errors,
        dryWellForm.getDrillDetailsForm().getCoordinateInputForm(),
        "drillDetailsForm.coordinateInputForm.latitudeCoordinates",
        "drillDetailsForm.coordinateInputForm.longitudeCoordinates",
        "top reservoir latitude",
        "top reservoir longitude"
    );

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "dryHoleCost.decimalInputValue",
            "The dry well cost",
            dryWellForm.getDryHoleCost())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var dryWellForm = (DryWellDetailsForm) form;

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "drillDetailsForm.well",
        "drillDetailsForm.well.required",
        "Enter the well reference"
    );

    CoordinateInputValidationUtil.validateMandatoryCoordinateInputForm(
        errors,
        dryWellForm.getDrillDetailsForm().getCoordinateInputForm(),
        "drillDetailsForm.coordinateInputForm.latitudeCoordinates",
        "drillDetailsForm.coordinateInputForm.longitudeCoordinates",
        "top reservoir latitude",
        "top reservoir longitude",
        "drillDetailsForm.coordinateInputForm.longitudeHemisphere"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "drillDetailsForm.reservoirDatum",
        "drillDetailsForm.reservoirDatum.required",
        "Select the top reservoir datum"
    );

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "drillDetailsForm.totalDepthFormation",
        "drillDetailsForm.totalDepthFormation.required",
        "Enter the total depth formation"
    );

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "drillDetailsForm.dataAcquisitionProgramme",
        "drillDetailsForm.dataAcquisitionProgramme.required",
        "Enter the data acquisition programme"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "dryHoleType",
        "dryHoleType.required",
        "Select the dry hole shows type"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "sourceFailureCategory",
        "sourceFailureCategory.required",
        "Select the source failure category"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "reservoirFailureCategory",
        "reservoirFailureCategory.required",
        "Select the reservoir failure category"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "sealFailureCategory",
        "sealFailureCategory.required",
        "Select the seal failure category"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "trapFailureCategory",
        "trapFailureCategory.required",
        "Select the trap failure category"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "migrationFailureCategory",
        "migrationFailureCategory.required",
        "Select the migration failure category"
    );

    DecimalInputValidator.configureValidator(
        errors,
        "dryHoleCost.decimalInputValue",
        "The total well cost",
        dryWellForm.getDryHoleCost())
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();
  }
}
