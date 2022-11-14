package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class PermeabilityDryWellFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(PermeabilityDryWellForm.class);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var validationType = (ValidationType) validationHints[0];
    var form = (PermeabilityDryWellForm) target;

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
    var permeabilityForm = (PermeabilityDryWellForm) form;

    if (BooleanUtils.isTrue(permeabilityForm.getHasPermeability())) {
      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityForm.permeabilityMin.decimalInputValue",
              "Permeability (md) min",
              permeabilityForm.getPermeabilityForm().getPermeabilityMin())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityForm.permeabilityMax.decimalInputValue",
              "Permeability (md) max",
              permeabilityForm.getPermeabilityForm().getPermeabilityMax())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityForm.permeabilityAverage.decimalInputValue",
              "Permeability (md) average",
              permeabilityForm.getPermeabilityForm().getPermeabilityAverage())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityForm.kvKhMin.decimalInputValue",
              "Kv/kh min",
              permeabilityForm.getPermeabilityForm().getKvKhMin())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityForm.kvKhMax.decimalInputValue",
              "Kv/kh max",
              permeabilityForm.getPermeabilityForm().getKvKhMax())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureOptionalValidator(
              errors,
              "permeabilityForm.kvKhAverage.decimalInputValue",
              "Kv/kh average",
              permeabilityForm.getPermeabilityForm().getKvKhAverage())
          .setMaxDecimalPlaces(2)
          .validate();
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var permeabilityForm = (PermeabilityDryWellForm) form;

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "hasPermeability",
        "hasPermeability.required",
        "Select whether you have permeability details");
    if (BooleanUtils.isTrue(permeabilityForm.getHasPermeability())) {
      DecimalInputValidator.configureValidator(
              errors,
              "permeabilityForm.permeabilityMin.decimalInputValue",
              "Permeability (md) min",
              permeabilityForm.getPermeabilityForm().getPermeabilityMin())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureValidator(
              errors,
              "permeabilityForm.permeabilityMax.decimalInputValue",
              "Permeability (md) max",
              permeabilityForm.getPermeabilityForm().getPermeabilityMax())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureValidator(
              errors,
              "permeabilityForm.permeabilityAverage.decimalInputValue",
              "Permeability (md) average",
              permeabilityForm.getPermeabilityForm().getPermeabilityAverage())
          .setMaxDecimalPlaces(2)
          .validate();

      ValidationUtils.rejectIfEmpty(errors,
          "permeabilityForm.averageMethodUsed",
          "permeabilityForm.averageMethodUsed.required",
          "Enter permeability (md) average method used");

      ValidationUtils.rejectIfEmpty(errors,
          "permeabilityForm.permeabilitySource",
          "permeabilityForm.permeabilitySource.required",
          "Enter Permeability (md) Source");

      if (permeabilityForm.getPermeabilityForm().getPermeabilitySource() == PermeabilitySource.OTHER) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "permeabilityForm.otherPermeabilitySource",
            "permeabilityForm.otherPermeabilitySource.required",
            "Enter other permeability (md) source"
        );
      }

      DecimalInputValidator.configureValidator(
              errors,
              "permeabilityForm.kvKhMin.decimalInputValue",
              "Kv/kh min",
              permeabilityForm.getPermeabilityForm().getKvKhMin())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureValidator(
              errors,
              "permeabilityForm.kvKhMax.decimalInputValue",
              "Kv/kh max",
              permeabilityForm.getPermeabilityForm().getKvKhMax())
          .setMaxDecimalPlaces(2)
          .validate();

      DecimalInputValidator.configureValidator(
              errors,
              "permeabilityForm.kvKhAverage.decimalInputValue",
              "Kv/kh average",
              permeabilityForm.getPermeabilityForm().getKvKhAverage())
          .setMaxDecimalPlaces(2)
          .validate();

      ValidationUtils.rejectIfEmpty(
          errors,
          "permeabilityForm.kvKhAverageMethodUsed",
          "permeabilityForm.kvKhAverageMethodUsed.required",
          "Enter kv/kh average method");

      if (permeabilityForm.getPermeabilityForm().getKvKhAverageMethodUsed() == PermeabilitySource.OTHER) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "permeabilityForm.kvKhAverageMethodUsedOther",
            "permeabilityForm.kvKhAverageMethodUsedOther.required",
            "Enter other kv/kh average method used"
        );
      }

      ValidationUtils.rejectIfEmpty(
          errors,
          "permeabilityForm.kvKhSource",
          "permeabilityForm.kvKhSource.required",
          "Enter kv/kh source");

      if (permeabilityForm.getPermeabilityForm().getKvKhSource() == PermeabilitySource.OTHER) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "permeabilityForm.otherKvKhSource",
            "permeabilityForm.otherKvKhSource.required",
            "Enter other kv/kh source"
        );
      }
    }
  }
}
