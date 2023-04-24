package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesForm;

@Service
class ResourceGroupsFormValidator implements SmartValidator {

  private static final Integer TWO_IN_GROUP = 2;

  private String resourceType;
  private ResourceGroupLink existingGroupLink;
  private UUID selectedGroupId;
  private List<ResourceGroupLink> resourceGroupLinks;

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (SubmissionForm) target;
    var hints = extractResourceGroupsValidatorHints(validationHints);
    existingGroupLink = hints.currentResourceGroupLink();
    resourceGroupLinks = hints.linksWithResource();
    resourceType = hints.resource().getType().getDisplayName().toLowerCase();
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
      ValidationUtils.rejectIfEmpty(errors, "preferredView", "preferredView.required",
          "Select whether this is the regulator preferred view of the %s?".formatted(resourceType));

      ValidationUtils.rejectIfEmpty(
          errors,
          "submissionDetailIdOfRelatedResource",
          "submissionDetailIdOfRelatedResource.required",
          "Select a %s or %s group".formatted(resourceType, resourceType));

      if (BooleanUtils.isFalse(form.getPreferredView())
          && isResourcePreferredViewAndGroupHasMoreThanTwo(existingGroupLink, resourceGroupLinks)) {
        //cannot remove the preferred view on the resource if there are more than two resources in the group
        errors.rejectValue(
            "preferredView",
            "preferredView.mustHavePreferredView",
            "To change whether this %s is the preferred view you must set another resource as the preferred view"
                .formatted(resourceType)
        );
      }
      if (Objects.nonNull(form.getSubmissionDetailIdOfRelatedResource())
          && !isTheSelectedGroupTheSameAsPrevious()
          && isResourcePreferredViewAndGroupHasMoreThanTwo(existingGroupLink, resourceGroupLinks)) {
        //if the group has been changed and the resource was the preferred view
        errors.rejectValue(
            "submissionDetailIdOfRelatedResource",
            "submissionDetailIdOfRelatedResource.mustHavePreferredView",
            "You cannot change the group of this %s if it is the preferred view."
                .formatted(resourceType));
      }
    } else if (isResourcePreferredViewAndGroupHasMoreThanTwo(existingGroupLink, resourceGroupLinks)) {
      //cannot remove the resource from group if it is the preferred view and there are more than two resources in the group
      errors.rejectValue("relatedToAnotherResource",
          "relatedToAnotherResource.mustHavePreferredView",
          "To remove this %s you must first set another resource as the preferred view"
              .formatted(resourceType));
    }
  }

  private ResourceGroupsValidatorHints extractResourceGroupsValidatorHints(Object... validationHints) {
    return Arrays.stream(validationHints)
        .filter(ResourceGroupsValidatorHints.class::isInstance)
        .map(ResourceGroupsValidatorHints.class::cast)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot get ResourceGroupsValidatorHints"));
  }

  private boolean isTheSelectedGroupTheSameAsPrevious() {
    //returns true if the selected resource group is different to the saved resource group
    return ((selectedGroupId != null && existingGroupLink.getResourceGroupId() != null)
        && selectedGroupId.equals(existingGroupLink.getResourceGroupId()))
        || ObjectUtils.allNull(existingGroupLink.getResourceGroupId());
  }

  private boolean isResourcePreferredViewAndGroupHasMoreThanTwo(
      ResourceGroupLink existingGroupLink, List<ResourceGroupLink> resourceGroupLinks) {
    //returns true if the resource was the preferred view and there are more than two in its group
    return BooleanUtils.isTrue(existingGroupLink.getPreferredView()) && resourceGroupLinks.size() > TWO_IN_GROUP;
  }
}
