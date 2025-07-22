package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesSubmissionService;

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
  public void createResourceGroupLink(SubmissionDetail submissionDetail, User user) {
    if (!relatedResourcesSubmissionService.isSectionSubmittable(submissionDetail, user)) {
      return;
    }

    relatedResourcesPersistenceService.getRelatedResourceIfRelatedToAnotherResource(submissionDetail)
        .ifPresent(relatedRes -> {

          var relatedResource = relatedRes.getRelatedSubmissionDetail().getSubmission().getResource();
          var currentResourceResource = submissionDetail.getSubmission().getResource();

          var resourceGroupLinkOptional = resourceGroupLinksRepository.findByResource(relatedResource);

          var relatedResourceIsInGroup = resourceGroupLinkOptional.isPresent();
          if (relatedResourceIsInGroup) {
            addToGroup(currentResourceResource, resourceGroupLinkOptional.get());
          } else {
            createNewGroup(relatedResource, currentResourceResource);
          }
        });
  }

  public Optional<ResourceGroupLink> getResourceGroupLinkByResource(Resource resource) {
    return resourceGroupLinksRepository.findByResource(resource);
  }

  public List<ResourceGroupLink> getResourceGroupLinksByResourceGroupId(UUID resourceGroupId) {
    return resourceGroupLinksRepository.findByResourceGroupId(resourceGroupId);
  }

  public List<ResourceGroupLink> getAllResourceGroupLinksForResources(List<Resource> resources) {
    return resourceGroupLinksRepository.findAllByResourceIn(resources);
  }

  /**
   * Removes a resource from a group and removes the group entirely if there were only two resources in the group to start.
   *
   * @param resource the resource to remove from the group
   */
  @Transactional
  public void removeResourceFromGroup(Resource resource) {
    var resourceGroupLinkForResource = getResourceGroupLinkByResource(resource);
    if (resourceGroupLinkForResource.isPresent()) {
      var resourceGroupLink = resourceGroupLinkForResource.get();
      var resourceGroupId = resourceGroupLink.getResourceGroupId();
      var resourceGroupLinks = resourceGroupLinksRepository.findByResourceGroupId(resourceGroupId);
      if (resourceGroupLinks.size() == 2) {
        resourceGroupLinksRepository.deleteAll(resourceGroupLinks);
      } else {
        resourceGroupLinksRepository.delete(resourceGroupLink);
      }
    }
  }

  private void addToGroup(Resource currentResourceResource,
                          ResourceGroupLink resourceGroupLinkForRelatedResource) {
    var resourceGroupId = resourceGroupLinkForRelatedResource.getResourceGroupId();

    // create the resource group link
    var resourceGroupLinkForCurrentResource = buildResourceGroupLink(
        resourceGroupId,
        currentResourceResource
    );
    resourceGroupLinksRepository.save(resourceGroupLinkForCurrentResource);
  }

  private void createNewGroup(Resource relatedResource,
                              Resource currentResource) {
    var resourceGroupId = UUID.randomUUID();

    var resourceGroupLinkForCurrentResource = buildResourceGroupLink(resourceGroupId, currentResource);

    var resourceGroupLinkForRelatedResource = buildResourceGroupLink(resourceGroupId, relatedResource);

    resourceGroupLinksRepository.saveAll(
        List.of(resourceGroupLinkForCurrentResource, resourceGroupLinkForRelatedResource));
  }

  static ResourceGroupLink buildResourceGroupLink(UUID resourceGroupId, Resource resource) {
    var resourceGroupLinkForRelated = new ResourceGroupLink();
    resourceGroupLinkForRelated.setResourceGroupId(resourceGroupId);
    resourceGroupLinkForRelated.setResource(resource);
    return resourceGroupLinkForRelated;
  }
}
