package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import java.util.Arrays;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesForm;

@Service
class ResourceGroupsFormValidator implements SmartValidator {

  static final String SELECTED_SUBMISSION_DETAIL_ID = "submissionDetailIdOfRelatedResource";
  private final ResourceGroupQueryService resourceGroupQueryService;

  private Resource resource;
  private String resourceType;
  private UUID selectedGroupId;

  @Autowired
  ResourceGroupsFormValidator(ResourceGroupQueryService resourceGroupQueryService) {
    this.resourceGroupQueryService = resourceGroupQueryService;
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (SubmissionForm) target;
    var hints = extractResourceGroupsValidatorHints(validationHints);
    resource = hints.resource();
    resourceType = resource.getType().getDisplayName().toLowerCase();
    selectedGroupId = hints.selectedGroupId();

    validate(form, errors);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return RelatedResourcesForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (RelatedResourcesForm) target;

    ValidationUtils.rejectIfEmpty(errors, "relatedToAnotherResource", "relatedToAnotherResource.required",
        "Select whether this %s is a different view of another %s in the service"
            .formatted(resourceType, resourceType));

    if (BooleanUtils.isTrue(form.getRelatedToAnotherResource())) {
      ValidationUtils.rejectIfEmpty(
          errors,
          SELECTED_SUBMISSION_DETAIL_ID,
          SELECTED_SUBMISSION_DETAIL_ID + ".required",
          "Select a %s or %s group".formatted(resourceType, resourceType));

      if (resourceGroupQueryService.getActiveResourceInGroupForGroupId(selectedGroupId).isPresent()
          && BooleanUtils.isTrue(resource.getActive())) {
        errors.rejectValue(
            SELECTED_SUBMISSION_DETAIL_ID,
            SELECTED_SUBMISSION_DETAIL_ID + ".cannotHaveSeveralActiveResourcesInGroup",
            "This resource cannot be added to the group as there is already an active resource in the group");
      }
    }
  }

  private ResourceGroupsValidatorHints extractResourceGroupsValidatorHints(Object... validationHints) {
    return Arrays.stream(validationHints)
        .filter(ResourceGroupsValidatorHints.class::isInstance)
        .map(ResourceGroupsValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get ResourceGroupsValidatorHints"));
  }
}
