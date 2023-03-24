package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;

@Service
class RelatedResourcesFormValidator extends PartialFullFormValidator {

  private String resourceType;

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (SubmissionForm) target;
    var hints = SubmissionFormValidationUtils
        .extractValidatorHints(validationHints);
    var validationType = hints
        .getValidationType();
    resourceType = hints.getSubmissionDetail().getResourceId().getType().getDisplayName().toLowerCase();

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return RelatedResourcesForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    //no fields require partial validation
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var relatedProspectsForm = (RelatedResourcesForm) form;

    ValidationUtils.rejectIfEmpty(errors, "relatedToAnotherResource", "relatedToAnotherResource.required",
        "Select whether this %s is a different view of another %s in the service"
            .formatted(resourceType, resourceType));

    if (BooleanUtils.isTrue(relatedProspectsForm.getRelatedToAnotherResource())) {
      ValidationUtils.rejectIfEmpty(errors, "submissionDetailIdOfRelatedResource", "submissionDetailIdOfRelatedResource.required",
          "Select a %s or %s group".formatted(resourceType, resourceType));

      ValidationUtils.rejectIfEmpty(errors, "preferredView", "preferredView.required",
          "Select whether this is the NSTA preferred view of the %s?".formatted(resourceType));
    }
  }
}
