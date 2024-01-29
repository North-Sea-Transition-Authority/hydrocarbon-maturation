package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import jakarta.transaction.Transactional;
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
  private final SubmissionDetailService submissionDetailService;
  private final ResourceGroupsFormValidator resourceGroupsFormValidator;

  @Autowired
  ManageResourceGroupsService(ResourceGroupLinksRepository resourceGroupLinksRepository,
                                     ResourceGroupService resourceGroupService,
                                     SubmissionDetailService submissionDetailService,
                                     ResourceGroupsFormValidator resourceGroupsFormValidator) {
    this.resourceGroupLinksRepository = resourceGroupLinksRepository;
    this.resourceGroupService = resourceGroupService;
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
    var currentResourceGroupLink = resourceGroupLinksRepository.findByResource(resource)
        .orElse(new ResourceGroupLink());

    UUID selectedGroupLinkId = getSelectedResourceGroupIdOrNull(form);
    List<ResourceGroupLink> currentResourceGroupLinks = getResourceGroupLinksFromGroupId(
        currentResourceGroupLink.getResourceGroupId());

    resourceGroupsFormValidator.validate(
        form,
        bindingResult,
        new ResourceGroupsValidatorHints(resource, currentResourceGroupLink, currentResourceGroupLinks, selectedGroupLinkId));
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
        updateCurrentResourceGroup(form, resource, existingGroupLinkOpt.get(), selectedGroupLinks);
      } else if (selectedResourceGroupLinkOpt.isPresent()) {
        //removes the resource from the previous group and adds the resource to an existing group
        existingGroupLinkOpt.ifPresent(this::removeFromGroup);
        addToGroupFromForm(resource, selectedResourceGroupLinkOpt.get(), form);
      } else {
        //creates a group and removes previous group from resource.
        existingGroupLinkOpt.ifPresent(this::removeFromGroup);
        createNewGroupFromForm(form, resource, selectedSubmissionDetail);
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
                                  ResourceGroupLink resourceGroupLink,
                                  RelatedResourcesForm form) {
    var newResourceLink = ResourceGroupService.buildResourceGroupLink(
        resourceGroupLink.getResourceGroupId(), currentResource);
    if (BooleanUtils.isTrue(form.getPreferredView())) {
      newResourceLink.setPreferredView(true);
      resourceGroupService.setCurrentlyPreferredViewAsNotPreferred(resourceGroupLink, resourceGroupLink.getResourceGroupId());
    } else {
      newResourceLink.setPreferredView(false);
    }
    resourceGroupLinksRepository.save(newResourceLink);
  }

  private void createNewGroupFromForm(RelatedResourcesForm form,
                                      Resource currentResource,
                                      SubmissionDetail selectedSubmissionDetail) {
    var resourceGroupId = UUID.randomUUID();
    var resourceGroupLinkForCurrentResource = ResourceGroupService.buildResourceGroupLink(
        resourceGroupId, currentResource);

    var otherResourceLink = ResourceGroupService.buildResourceGroupLink(
        resourceGroupId, selectedSubmissionDetail.getSubmission().getResource());
    if (BooleanUtils.isTrue(form.getPreferredView())) {
      resourceGroupLinkForCurrentResource.setPreferredView(true);
    } else {
      otherResourceLink.setPreferredView(true);
    }

    resourceGroupLinksRepository.save(otherResourceLink);
    resourceGroupLinksRepository.save(resourceGroupLinkForCurrentResource);
  }

  private void updateCurrentResourceGroup(RelatedResourcesForm form,
                                          Resource currentResource,
                                          ResourceGroupLink currentResourceGroupLink,
                                          List<ResourceGroupLink> resourceGroupLinks) {
    var resourceGroupId = currentResourceGroupLink.getResourceGroupId();

    if (isTwoInGroupAndCurrentIsChangedToNotPreferred(currentResourceGroupLink, resourceGroupLinks, form)) {
      //if only two resources in group, and the preferred view is removed, the other resource is made the preferred view
      var otherLink = resourceGroupLinks.stream()
          .filter(groupLink -> !groupLink.getResource().getId().equals(currentResource.getId()))
          .findFirst()
          .orElseThrow(() ->
              new RemiEntityNotFoundException("Could not find a second resource in the resource group."));
      otherLink.setPreferredView(true);
      currentResourceGroupLink.setPreferredView(false);
      resourceGroupLinksRepository.saveAll(List.of(otherLink, currentResourceGroupLink));
    } else if (BooleanUtils.isTrue(form.getPreferredView())) {
      //if current resource is set as preferred view
      resourceGroupService.setCurrentlyPreferredViewAsNotPreferred(currentResourceGroupLink, resourceGroupId);
      currentResourceGroupLink.setPreferredView(true);
      resourceGroupLinksRepository.save(currentResourceGroupLink);
    }
  }

  private boolean isTwoInGroupAndCurrentIsChangedToNotPreferred(ResourceGroupLink currentResourceLink,
                                                                List<ResourceGroupLink> resourceGroupLinks,
                                                                RelatedResourcesForm form) {
    return resourceGroupLinks.size() == TWO_IN_GROUP
        && currentResourceLink.getPreferredView()
        && BooleanUtils.isFalse(form.getPreferredView());
  }

  private RelatedResourcesForm resourceGroupLinkEntityToForm(ResourceGroupLink resourceGroupLink) {
    var form = new RelatedResourcesForm();
    form.setRelatedToAnotherResource(resourceGroupLink.getResourceGroupId() != null);

    if (BooleanUtils.isTrue(form.getRelatedToAnotherResource())) {
      resourceGroupLinksRepository
          .findByResourceGroupIdAndPreferredViewIsTrue(resourceGroupLink.getResourceGroupId())
          .map(ResourceGroupLink::getResource)
          .flatMap(submissionDetailService::getLatestApprovedSubmissionDetailForResource)
          .ifPresent(latestSubmissionDetail ->
              form.setSubmissionDetailIdOfRelatedResource(latestSubmissionDetail.getId().toString()));
    }
    form.setPreferredView(resourceGroupLink.getPreferredView());

    return form;
  }


  private List<ResourceGroupLink> getResourceGroupLinksFromGroupId(UUID resourceGroupId) {
    if (resourceGroupId != null) {
      return resourceGroupLinksRepository.findByResourceGroupId(resourceGroupId);
    } else {
      return List.of();
    }
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
