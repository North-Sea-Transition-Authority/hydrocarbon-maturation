package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ResourceGroupService {
  private final RelatedResourcesPersistenceService relatedResourcesPersistenceService;
  private final ResourceGroupLinksRepository resourceGroupLinksRepository;
  private final RelatedResourcesSubmissionService relatedResourcesSubmissionService;

  public ResourceGroupService(RelatedResourcesPersistenceService relatedResourcesPersistenceService,
                              ResourceGroupLinksRepository resourceGroupLinksRepository,
                              RelatedResourcesSubmissionService relatedResourcesSubmissionService) {
    this.relatedResourcesPersistenceService = relatedResourcesPersistenceService;
    this.resourceGroupLinksRepository = resourceGroupLinksRepository;
    this.relatedResourcesSubmissionService = relatedResourcesSubmissionService;
  }

  @Transactional
  public void createResourceGroupLink(SubmissionDetail submissionDetail) {
    if (!relatedResourcesSubmissionService.isSectionSubmittable(submissionDetail)) {
      return;
    }

    relatedResourcesPersistenceService.getRelatedResourceIfRelatedToAnotherResource(submissionDetail)
        .ifPresent(relatedResource -> {

          var relatedResourceResource = relatedResource.getRelatedSubmissionDetail().getSubmission().getResource();
          var currentResourceResource = submissionDetail.getSubmission().getResource();

          var resourceGroupLinkOptional = resourceGroupLinksRepository.findByResource(relatedResourceResource);

          var relatedResourceIsInGroup = resourceGroupLinkOptional.isPresent();
          if (relatedResourceIsInGroup) {
            addToGroup(relatedResource, currentResourceResource, resourceGroupLinkOptional.get());
          } else {
            createNewGroup(relatedResource, relatedResourceResource, currentResourceResource);
          }
        });
  }

  Optional<ResourceGroupLink> getResourceGroupLinkByResource(Resource resource) {
    return resourceGroupLinksRepository.findByResource(resource);
  }

  List<ResourceGroupLink> getResourceGroupLinksByResourceGroupId(UUID resourceGroupId) {
    return resourceGroupLinksRepository.findByResourceGroupId(resourceGroupId);
  }

  private void addToGroup(RelatedResource relatedResource,
                          Resource currentResourceResource,
                          ResourceGroupLink resourceGroupLinkForRelatedResource) {
    var resourceGroupId = resourceGroupLinkForRelatedResource.getResourceGroupId();

    // create the resource group link
    var resourceGroupLinkForCurrentResource = buildResourceGroupLink(
        resourceGroupId,
        currentResourceResource
    );
    resourceGroupLinksRepository.save(resourceGroupLinkForCurrentResource);

    if (BooleanUtils.isTrue(relatedResource.getPreferredView())) {
      setCurrentlyPreferredViewAsNotPreferred(resourceGroupLinkForRelatedResource, resourceGroupId);

      resourceGroupLinkForCurrentResource.setPreferredView(true);
    }
  }

  private void setCurrentlyPreferredViewAsNotPreferred(ResourceGroupLink resourceGroupLinkForRelatedResource,
                                                       UUID resourceGroupId) {
    if (resourceGroupLinkForRelatedResource.getPreferredView()) {
      resourceGroupLinkForRelatedResource.setPreferredView(false);
    } else {
      var resourceGroupLink = getPreferredViewResourceGroupLinkByGroupIdOrThrow(resourceGroupId);
      resourceGroupLink.setPreferredView(false);
    }
  }

  private ResourceGroupLink getPreferredViewResourceGroupLinkByGroupIdOrThrow(UUID resourceGroupId) {
    return resourceGroupLinksRepository.findByResourceGroupIdAndPreferredViewIsTrue(resourceGroupId)
        .orElseThrow(() -> new IllegalStateException(
            "There should be at least one ResourceGroupLink with a preferred view for resource group id %s"
                .formatted(resourceGroupId)));
  }

  private void createNewGroup(RelatedResource relatedResource,
                              Resource relatedResourceResource,
                              Resource currentResource) {
    var resourceGroupId = UUID.randomUUID();

    var resourceGroupLinkForCurrentResource = buildResourceGroupLink(resourceGroupId, currentResource);

    var resourceGroupLinkForRelatedResource = buildResourceGroupLink(resourceGroupId, relatedResourceResource);

    if (BooleanUtils.isTrue(relatedResource.getPreferredView())) {
      resourceGroupLinkForCurrentResource.setPreferredView(true);
    } else {
      resourceGroupLinkForRelatedResource.setPreferredView(true);
    }

    resourceGroupLinksRepository.saveAll(List.of(resourceGroupLinkForCurrentResource, resourceGroupLinkForRelatedResource));
  }

  static ResourceGroupLink buildResourceGroupLink(UUID resourceGroupId, Resource resource) {
    var resourceGroupLinkForRelated = new ResourceGroupLink();
    resourceGroupLinkForRelated.setResourceGroupId(resourceGroupId);
    resourceGroupLinkForRelated.setResource(resource);
    resourceGroupLinkForRelated.setPreferredView(false);
    return resourceGroupLinkForRelated;
  }
}
