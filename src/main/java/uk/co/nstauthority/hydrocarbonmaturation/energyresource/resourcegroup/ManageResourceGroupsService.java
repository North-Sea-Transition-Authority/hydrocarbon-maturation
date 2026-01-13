package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesForm;

@Service
class ManageResourceGroupsService {

  private final ResourceGroupLinksRepository resourceGroupLinksRepository;
  private final ResourceGroupService resourceGroupService;
  private final ResourceGroupQueryService resourceGroupQueryService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceGroupsFormValidator resourceGroupsFormValidator;

  @Autowired
  ManageResourceGroupsService(ResourceGroupLinksRepository resourceGroupLinksRepository,
                              ResourceGroupService resourceGroupService,
                              ResourceGroupQueryService resourceGroupQueryService,
                              SubmissionDetailService submissionDetailService,
                              ResourceGroupsFormValidator resourceGroupsFormValidator) {
    this.resourceGroupLinksRepository = resourceGroupLinksRepository;
    this.resourceGroupService = resourceGroupService;
    this.resourceGroupQueryService = resourceGroupQueryService;
    this.submissionDetailService = submissionDetailService;
    this.resourceGroupsFormValidator = resourceGroupsFormValidator;
  }

  RelatedResourcesForm getRelatedResourcesFormFromResource(Resource resource) {
    var resourceGroupLink = resourceGroupLinksRepository.findByResource(resource);
    return resourceGroupLinkEntityToForm(resourceGroupLink);
  }

  BindingResult validateForResource(RelatedResourcesForm form,
                                    BindingResult bindingResult,
                                    Resource resource) {
    var selectedGroup = getSelectedResourceGroupOrNull(form);

    resourceGroupsFormValidator.validate(
        form,
        bindingResult,
        new ResourceGroupsValidatorHints(resource, selectedGroup));
    return bindingResult;
  }

  @Transactional
  public void updateResourceGroupLinkFromResource(Resource resource, RelatedResourcesForm form) {
    var currentGroupLink = resourceGroupService.getResourceGroupLinkByResource(resource);
    var currentResourceIsActive = resource.getActive();
    if (isRelatedToAnotherResource(form)) {
      var selectedSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(
          UUID.fromString(form.getSubmissionDetailIdOfRelatedResource())
      );
      var selectedResourceGroupLink = resourceGroupService.getResourceGroupLinkByResource(
          selectedSubmissionDetail.getSubmission().getResource()
      );
      var selectedGroupLinks = resourceGroupService.getResourceGroupLinksByResourceGroup(
          selectedResourceGroupLink.getResourceGroup()
      );
      if (Boolean.TRUE.equals(currentResourceIsActive)) {
        // if the current resource is active then set all other resources in the group to be part of this group
        updateOtherResourceGroupLinks(currentGroupLink, selectedGroupLinks);
      } else {
        // update the current resource to be part of the selected group
        addToGroupFromForm(currentGroupLink, selectedResourceGroupLink);
      }
    } else {
      // if there are other resources in the group then create a new group for this resource,
      // otherwise do not change the group for the current resource
      if (resourceGroupQueryService.resourceIsInGroupWithOtherResources(currentGroupLink)) {
        resourceGroupService.removeResourceFromGroup(resource);
        resourceGroupService.createNewGroupForResource(resource);
      }
    }
  }

  private boolean isRelatedToAnotherResource(RelatedResourcesForm form) {
    return BooleanUtils.isTrue(form.getRelatedToAnotherResource())
        && Objects.nonNull(form.getSubmissionDetailIdOfRelatedResource());
  }

  private void addToGroupFromForm(ResourceGroupLink currentResourceLink,
                                  ResourceGroupLink selectedResourceGroupLink) {
    currentResourceLink.setResourceGroup(selectedResourceGroupLink.getResourceGroup());
    resourceGroupLinksRepository.save(currentResourceLink);
  }

  private void updateOtherResourceGroupLinks(ResourceGroupLink currentResourceGroupLink,
                                             List<ResourceGroupLink> resourceGroupLinks) {
    for (ResourceGroupLink resourceGroupLink : resourceGroupLinks) {
      resourceGroupLink.setResourceGroup(currentResourceGroupLink.getResourceGroup());
    }
    resourceGroupLinksRepository.saveAll(resourceGroupLinks);
  }

  /**
   * Converts a ResourceGroupLink entity to a RelatedResourcesForm.
   *
   * <p>If the resource is part of a group, this method populates the form with group information:
   * <ul>
   *   <li>Sets relatedToAnotherResource to true if the resource is in a group with other resources.</li>
   *   <li>Find the active resource in the group.</li>
   *   <li>If an active resource is found,
   *   sets submissionDetailIdOfRelatedResource to the latest approved submission detail for that resource.</li>
   *   <li>If no active resource is found,
   *   sets submissionDetailIdOfRelatedResource to the latest submission detail (by accepted timestamp)
   *   among all resources in the group.</li>
   *   <li>If no submission details are found, sets submissionDetailIdOfRelatedResource to null.</li>
   * </ul>
   * If the resource is not part of a group, sets relatedToAnotherResource to false.
   *
   * @param resourceGroupLink the ResourceGroupLink entity to convert
   * @return a populated RelatedResourcesForm
   */
  private RelatedResourcesForm resourceGroupLinkEntityToForm(ResourceGroupLink resourceGroupLink) {
    var form = new RelatedResourcesForm();
    form.setRelatedToAnotherResource(resourceGroupQueryService.resourceIsInGroupWithOtherResources(resourceGroupLink));

    if (BooleanUtils.isTrue(form.getRelatedToAnotherResource())) {
      var relatedResources = resourceGroupLinksRepository.findByResourceGroup(resourceGroupLink.getResourceGroup());

      if (!relatedResources.isEmpty()) {
        var resourcesInGroup = resourceGroupQueryService.getResourcesInGroup(
            resourceGroupLink.getResourceGroup());
        var activeResourceInGroup = resourceGroupQueryService.getActiveResourceForResourceList(resourcesInGroup);

        if (activeResourceInGroup.isPresent()) {
          var approvedActiveSubmission = submissionDetailService.getLatestApprovedSubmissionDetailForResource(
              activeResourceInGroup.get()
          );
          approvedActiveSubmission.ifPresent(
              submissionDetail -> form.setSubmissionDetailIdOfRelatedResource(submissionDetail.getId().toString()));
        } else {
          var latestSubmissionDetailFromResources = resourcesInGroup.stream()
              .map(submissionDetailService::getLatestApprovedSubmissionDetailForResource)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .max(Comparator.comparing(SubmissionDetail::getAcceptedTimestamp));

          form.setSubmissionDetailIdOfRelatedResource(latestSubmissionDetailFromResources
              .map(SubmissionDetail::getId)
              .map(UUID::toString)
              .orElse(null));
        }
      }
    }

    return form;
  }

  private ResourceGroup getSelectedResourceGroupOrNull(RelatedResourcesForm form) {
    if (form.getSubmissionDetailIdOfRelatedResource() != null) {
      var selectedResource = submissionDetailService.getSubmissionDetailForIdOrError(
          UUID.fromString(form.getSubmissionDetailIdOfRelatedResource())
      ).getSubmission().getResource();
      return resourceGroupLinksRepository.findByResource(selectedResource).getResourceGroup();
    } else {
      return null;
    }
  }
}
