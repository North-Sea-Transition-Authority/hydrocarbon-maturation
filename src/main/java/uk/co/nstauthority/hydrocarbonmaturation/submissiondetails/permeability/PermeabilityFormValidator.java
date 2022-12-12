package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class PermeabilityFormValidator implements SmartValidator, SubmissionFormValidator {

  private SubmissionType submissionType;

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(PermeabilityForm.class);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var validationType = (ValidationType) validationHints[0];
    submissionType = SubmissionFormValidationUtils.extractSubmissionTypeFromHints(validationHints);

    var form = (PermeabilityForm) target;

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var permeabilityForm = (PermeabilityForm) form;

    if (submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || BooleanUtils.isTrue(permeabilityForm.getHasPermeability())) {
      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.permeabilityMin.decimalInputValue",
              "Permeability (md) min",
              permeabilityForm.getPermeabilityValuesForm().getPermeabilityMin())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.permeabilityMax.decimalInputValue",
              "Permeability (md) max",
              permeabilityForm.getPermeabilityValuesForm().getPermeabilityMax())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.permeabilityAverage.decimalInputValue",
              "Permeability (md) average",
              permeabilityForm.getPermeabilityValuesForm().getPermeabilityAverage())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.kvKhMin.decimalInputValue",
              "Kv/kh min",
              permeabilityForm.getPermeabilityValuesForm().getKvKhMin())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.kvKhMax.decimalInputValue",
              "Kv/kh max",
              permeabilityForm.getPermeabilityValuesForm().getKvKhMax())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityValuesForm.kvKhAverage.decimalInputValue",
              "Kv/kh average",
              permeabilityForm.getPermeabilityValuesForm().getKvKhAverage())
          .setMaxDecimalPlaces(2)
          .validate();
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var permeabilityForm = (PermeabilityForm) form;

    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "hasPermeability",
          "hasPermeability.required",
          "Select whether you have permeability details");
      if (BooleanUtils.isTrue(permeabilityForm.getHasPermeability())) {
        validatePermeability(permeabilityForm, errors);
      }
    } else {
      validatePermeability(permeabilityForm, errors);
    }
  }

  private void validatePermeability(PermeabilityForm permeabilityForm, Errors errors) {
    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.permeabilityMin.decimalInputValue",
            "Permeability (md) min",
            permeabilityForm.getPermeabilityValuesForm().getPermeabilityMin())
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.permeabilityMax.decimalInputValue",
            "Permeability (md) max",
            permeabilityForm.getPermeabilityValuesForm().getPermeabilityMax())
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.permeabilityAverage.decimalInputValue",
            "Permeability (md) average",
            permeabilityForm.getPermeabilityValuesForm().getPermeabilityAverage())
        .setMaxDecimalPlaces(2)
        .validate();

    ValidationUtils.rejectIfEmpty(errors,
        "permeabilityValuesForm.averageMethodUsed",
        "permeabilityValuesForm.averageMethodUsed.required",
        "Enter permeability (md) average method used");

    ValidationUtils.rejectIfEmpty(errors,
        "permeabilityValuesForm.permeabilitySource",
        "permeabilityValuesForm.permeabilitySource.required",
        "Enter Permeability (md) Source");

    if (permeabilityForm.getPermeabilityValuesForm().getPermeabilitySource() == PermeabilitySource.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "permeabilityValuesForm.otherPermeabilitySource",
          "permeabilityValuesForm.otherPermeabilitySource.required",
          "Enter other permeability (md) source"
      );
    }

    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.kvKhMin.decimalInputValue",
            "Kv/kh min",
            permeabilityForm.getPermeabilityValuesForm().getKvKhMin())
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.kvKhMax.decimalInputValue",
            "Kv/kh max",
            permeabilityForm.getPermeabilityValuesForm().getKvKhMax())
        .setMaxDecimalPlaces(2)
        .validate();

    DecimalInputValidator.configureValidator(
            errors,
            "permeabilityValuesForm.kvKhAverage.decimalInputValue",
            "Kv/kh average",
            permeabilityForm.getPermeabilityValuesForm().getKvKhAverage())
        .setMaxDecimalPlaces(2)
        .validate();

    ValidationUtils.rejectIfEmpty(
        errors,
        "permeabilityValuesForm.kvKhAverageMethodUsed",
        "permeabilityValuesForm.kvKhAverageMethodUsed.required",
        "Enter kv/kh average method");

    ValidationUtils.rejectIfEmpty(
        errors,
        "permeabilityValuesForm.kvKhSource",
        "permeabilityValuesForm.kvKhSource.required",
        "Enter kv/kh source");

    if (permeabilityForm.getPermeabilityValuesForm().getKvKhSource() == PermeabilitySource.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "permeabilityValuesForm.otherKvKhSource",
          "permeabilityValuesForm.otherKvKhSource.required",
          "Enter other kv/kh source"
      );
    }
  }
}
