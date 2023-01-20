package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedprospects;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class RelatedProspectsFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return RelatedProspectsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (RelatedProspectsForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    //no fields require partial validation
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var relatedProspectsForm = (RelatedProspectsForm) form;

    ValidationUtils.rejectIfEmpty(errors, "relatedToAnotherProspect", "relatedToAnotherProspect.required",
        "Select whether this prospect is a different view of another prospect in the service");

    if (BooleanUtils.isTrue(relatedProspectsForm.getRelatedToAnotherProspect())) {
      ValidationUtils.rejectIfEmpty(errors, "generalDetailIdOfRelatedProspect", "generalDetailIdOfRelatedProspect.required",
          "Select a prospect or prospect group");

      ValidationUtils.rejectIfEmpty(errors, "preferredView", "preferredView.required",
          "Select whether this is the NSTA preferred view of the prospect?");
    }
  }
}
