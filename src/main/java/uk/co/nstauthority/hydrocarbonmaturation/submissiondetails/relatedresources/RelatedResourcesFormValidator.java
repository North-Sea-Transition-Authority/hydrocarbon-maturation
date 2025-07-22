package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class RelatedResourcesFormValidator implements SubmissionValidator {

  private final ResourceGroupQueryService resourceGroupQueryService;
  private final RelatedResourcesFormService relatedResourcesFormService;

  @Autowired
  RelatedResourcesFormValidator(ResourceGroupQueryService resourceGroupQueryService,
                                RelatedResourcesFormService relatedResourcesFormService) {
    this.resourceGroupQueryService = resourceGroupQueryService;
    this.relatedResourcesFormService = relatedResourcesFormService;
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    //no fields require partial validation
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var relatedProspectsForm = (RelatedResourcesForm) form;

    var resource = submissionDetail.getSubmission().getResource();
    var resourceType = resource.getType().getDisplayName().toLowerCase();

    ValidationUtils.rejectIfEmpty(errors, "relatedToAnotherResource", "relatedToAnotherResource.required",
        "Select whether this %s is a different view of another %s in the service"
            .formatted(resourceType, resourceType));

    if (BooleanUtils.isTrue(relatedProspectsForm.getRelatedToAnotherResource())) {
      ValidationUtils.rejectIfEmpty(errors, "submissionDetailIdOfRelatedResource", "submissionDetailIdOfRelatedResource.required",
          "Select a %s or %s group".formatted(resourceType, resourceType));

      var selectedGroupId = relatedResourcesFormService.getSelectedResourceGroupIdOrNull(relatedProspectsForm);

      if (resourceGroupQueryService.getActiveResourceInGroupForGroupId(selectedGroupId).isPresent()
          && BooleanUtils.isTrue(resource.getActive())) {
        errors.rejectValue(
            "submissionDetailIdOfRelatedResource",
            "submissionDetailIdOfRelatedResource.cannotHaveSeveralActiveResourcesInGroup",
            "This resource cannot be added to the group as there is already an active resource in the group");
      }
    }
  }
}
