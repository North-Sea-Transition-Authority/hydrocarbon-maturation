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
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesForm;

@Service
class ManageResourceGroupsService {

  private static final Integer TWO_IN_GROUP = 2;

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
    return resourceGroupLinksRepository.findByResource(resource)
        .map(this::resourceGroupLinkEntityToForm)
        .orElse(getFormForUnrelatedResource());
  }

  BindingResult validateForResource(RelatedResourcesForm form,
                                    BindingResult bindingResult,
                                    Resource resource) {
    UUID selectedGroupLinkId = getSelectedResourceGroupIdOrNull(form);

    resourceGroupsFormValidator.validate(
        form,
        bindingResult,
        new ResourceGroupsValidatorHints(resource, selectedGroupLinkId));
    return bindingResult;
  }

  @Transactional
  public void updateResourceGroupLinkFromResource(Resource resource, RelatedResourcesForm form) {
    var existingGroupLinkOpt = resourceGroupService.getResourceGroupLinkByResource(resource);
    if (isRelatedToAnotherResource(form)) {
      var selectedSubmissionDetail = submissionDetailService
          .getSubmissionDetailForIdOrError(UUID.fromString(form.getSubmissionDetailIdOfRelatedResource()));
      var selectedResourceGroupLinkOpt = resourceGroupService.getResourceGroupLinkByResource(
          selectedSubmissionDetail.getSubmission().getResource());
      var selectedGroupLinks =  selectedResourceGroupLinkOpt
          .map(resourceGroupLink ->
              resourceGroupService.getResourceGroupLinksByResourceGroupId(resourceGroupLink.getResourceGroupId()))
          .orElse(List.of());
      if (isTheCurrentGroupBeingUpdated(selectedResourceGroupLinkOpt, existingGroupLinkOpt)) {
        //if the resource is in the same group it was, it updates the link
        updateCurrentResourceGroup(resource, existingGroupLinkOpt.get(), selectedGroupLinks);
      } else if (selectedResourceGroupLinkOpt.isPresent()) {
        //removes the resource from the previous group and adds the resource to an existing group
        existingGroupLinkOpt.ifPresent(this::removeFromGroup);
        addToGroupFromForm(resource, selectedResourceGroupLinkOpt.get());
      } else {
        //creates a group and removes previous group from resource.
        existingGroupLinkOpt.ifPresent(this::removeFromGroup);
        createNewGroupFromForm(resource, selectedSubmissionDetail);
      }
    } else {
      existingGroupLinkOpt.ifPresent(this::removeFromGroup);
    }
  }

  private boolean isTheCurrentGroupBeingUpdated(Optional<ResourceGroupLink> selectedGroupLink,
                                                Optional<ResourceGroupLink> currentGroupLink) {
    return selectedGroupLink.isPresent() && currentGroupLink.isPresent()
        && selectedGroupLink.get().getResourceGroupId().equals(currentGroupLink.get().getResourceGroupId());
  }

  private boolean isRelatedToAnotherResource(RelatedResourcesForm form) {
    return BooleanUtils.isTrue(form.getRelatedToAnotherResource())
        && Objects.nonNull(form.getSubmissionDetailIdOfRelatedResource());
  }

  private RelatedResourcesForm getFormForUnrelatedResource() {
    var form = new RelatedResourcesForm();
    form.setRelatedToAnotherResource(false);
    return form;
  }

  private void removeFromGroup(ResourceGroupLink resourceGroupLink) {
    var resourceGroupLinks = resourceGroupService.getResourceGroupLinksByResourceGroupId(
        resourceGroupLink.getResourceGroupId());
    if (resourceGroupLinks.size() == TWO_IN_GROUP) {
      // if there are only two resources in the group before the current resource is removed,
      // then dissolve the whole group. A group should not exist with only a single resource in it.
      resourceGroupLinksRepository.deleteAll(resourceGroupLinks);
    } else {
      resourceGroupLinksRepository.delete(resourceGroupLink);
    }
  }

  private void addToGroupFromForm(Resource currentResource,
                                  ResourceGroupLink resourceGroupLink) {
    var newResourceLink = ResourceGroupService.buildResourceGroupLink(
        resourceGroupLink.getResourceGroupId(), currentResource);
    resourceGroupLinksRepository.save(newResourceLink);
  }

  private void createNewGroupFromForm(Resource currentResource,
                                      SubmissionDetail selectedSubmissionDetail) {
    var resourceGroupId = UUID.randomUUID();
    var resourceGroupLinkForCurrentResource = ResourceGroupService.buildResourceGroupLink(
        resourceGroupId, currentResource);

    var otherResourceLink = ResourceGroupService.buildResourceGroupLink(
        resourceGroupId, selectedSubmissionDetail.getSubmission().getResource());

    resourceGroupLinksRepository.save(otherResourceLink);
    resourceGroupLinksRepository.save(resourceGroupLinkForCurrentResource);
  }

  private void updateCurrentResourceGroup(Resource currentResource,
                                          ResourceGroupLink currentResourceGroupLink,
                                          List<ResourceGroupLink> resourceGroupLinks) {
    var otherLink = resourceGroupLinks.stream()
          .filter(groupLink -> !groupLink.getResource().getId().equals(currentResource.getId()))
          .findFirst()
          .orElseThrow(() ->
              new RemiEntityNotFoundException("Could not find a second resource in the resource group."));
    resourceGroupLinksRepository.saveAll(List.of(otherLink, currentResourceGroupLink));
  }

  /**
   * Converts a ResourceGroupLink entity to a RelatedResourcesForm.
   *
   * <p>If the resource is part of a group, this method populates the form with group information:
   * <ul>
   *   <li>Sets relatedToAnotherResource to true if the resource is in a group.</li>
   *   <li>If the group contains resources, finds the active resource in the group.</li>
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
    form.setRelatedToAnotherResource(resourceGroupLink.getResourceGroupId() != null);

    if (BooleanUtils.isTrue(form.getRelatedToAnotherResource())) {
      var relatedResources = resourceGroupLinksRepository.findByResourceGroupId(resourceGroupLink.getResourceGroupId());

      if (!relatedResources.isEmpty()) {
        var resourcesInGroup = resourceGroupQueryService.getResourcesInGroupForGroupId(
            resourceGroupLink.getResourceGroupId());
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

  private UUID getSelectedResourceGroupIdOrNull(RelatedResourcesForm form) {
    if (form.getSubmissionDetailIdOfRelatedResource() != null) {
      return resourceGroupLinksRepository.findByResource(submissionDetailService
              .getSubmissionDetailForIdOrError(UUID.fromString(form.getSubmissionDetailIdOfRelatedResource()))
              .getSubmission().getResource())
          .map(ResourceGroupLink::getResourceGroupId)
          .orElse(null);
    } else {
      return null;
    }
  }
}
