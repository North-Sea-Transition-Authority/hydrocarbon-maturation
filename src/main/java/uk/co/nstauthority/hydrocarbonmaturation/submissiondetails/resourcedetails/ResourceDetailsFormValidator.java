package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import java.util.Arrays;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
public class ResourceDetailsFormValidator extends PartialFullFormValidator {

  private boolean isRegulator;
  private ResourceType resourceType;
  private ResourceDetail latestApprovedResourceDetail;
  private boolean missingOrganisationErrorPresent;

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var hints = extractNewResourceFormValidatorHints(validationHints);
    isRegulator = hints.isRegulator();
    resourceType = hints.resourceType();
    latestApprovedResourceDetail = hints.latestApprovedResourceDetail();
    missingOrganisationErrorPresent = false;
    var validationType = hints.validationType();
    var form = (SubmissionForm) target;

    validateAlways(errors);
    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return ResourceDetailsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    if (!isRegulator && !missingOrganisationErrorPresent) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors,
          "responsibleOrganisationId",
          "responsibleOrganisationId.required",
          "Enter the responsible organisation");
    }
    ValidationUtils.rejectIfEmpty(
        errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("You must provide ResourceDetailsFormValidatorHints");
  }

  private void validateAlways(Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "resourceName",
        "resourceName.required",
        "Enter the %s name".formatted(resourceType.getDisplayName().toLowerCase()));
    if (latestApprovedResourceDetail != null
        && latestApprovedResourceDetail.getOrganisationId() != null) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "responsibleOrganisationId",
          "responsibleOrganisationId.required",
          "You cannot remove the responsible organisation from a resource that has " +
              "a responsible organisation on an earlier approved submission");
      missingOrganisationErrorPresent = true;
    }
  }

  private ResourceDetailsFormValidatorHints extractNewResourceFormValidatorHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(ResourceDetailsFormValidatorHints.class::isInstance)
        .map(ResourceDetailsFormValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get NewResourceFormValidatorHints"));
  }
}
