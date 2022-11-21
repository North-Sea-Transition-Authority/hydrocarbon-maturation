package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.CoordinateInputValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class DryWellDetailsFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (DryWellDetailsForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return DryWellDetailsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var dryWellForm = (DryWellDetailsForm) form;

    CoordinateInputValidationUtil.validateOptionalCoordinateInputForm(
        errors,
        dryWellForm.getDrillDetailsForm().getCoordinateInputForm(),
        "drillDetailsForm.coordinateInputForm.latitudeCoordinates",
        "drillDetailsForm.coordinateInputForm.longitudeCoordinates"
    );

    DecimalInputValidator.configureOptionalValidator(
            errors,
            "dryHoleCost.decimalInputValue",
            "the dry well cost",
            dryWellForm.getDryHoleCost())
        .setMaxDecimalPlaces(2)
        .validate();
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var dryWellForm = (DryWellDetailsForm) form;

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "drillDetailsForm.dryWell",
        "drillDetailsForm.dryWell.required",
        "Enter the dry well reference"
    );

    CoordinateInputValidationUtil.validateMandatoryCoordinateInputForm(
        errors,
        dryWellForm.getDrillDetailsForm().getCoordinateInputForm(),
        "drillDetailsForm.coordinateInputForm.latitudeCoordinates",
        "drillDetailsForm.coordinateInputForm.longitudeCoordinates",
        "drillDetailsForm.coordinateInputForm.longitudeHemisphere"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "drillDetailsForm.reservoirDatum",
        "drillDetailsForm.reservoirDatum.required",
        "Select the datum"
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

    DecimalInputValidator.configureValidator(
        errors,
        "dryHoleCost.decimalInputValue",
        "the total well cost",
        dryWellForm.getDryHoleCost())
        .setMaxDecimalPlaces(2)
        .validate();
  }
}
