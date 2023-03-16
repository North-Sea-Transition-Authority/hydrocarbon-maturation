package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;

@Service
class RegulatorManagedInformationFormValidator implements SmartValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return RegulatorManagedInformationForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Must provide validation hints to this validator");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var validatorHints = SubmissionFormValidationUtils
        .extractRegulatorManagedInformationValidatorHints(validationHints);
    var form = (RegulatorManagedInformationForm) target;
    ValidationUtils.rejectIfEmpty(errors, "geographicalArea", "geographicalArea.required",
        "Select a geographical area");

    if (validatorHints.isResourceBeingRelinquished()) {
      var relinquishmentReport = form.getRelinquishmentReport();
      FileValidationUtil.validator()
          .withMaximumNumberOfFiles(1, "Upload at most one relinquishment report")
          .validate(errors, relinquishmentReport, "relinquishmentReport");
    }
  }
}
