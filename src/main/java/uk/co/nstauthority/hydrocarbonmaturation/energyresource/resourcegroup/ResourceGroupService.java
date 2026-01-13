package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesSubmissionService;

@Service
public class ResourceGroupService {
  private final RelatedResourcesPersistenceService relatedResourcesPersistenceService;
  private final ResourceGroupRepository resourceGroupRepository;
  private final ResourceGroupLinksRepository resourceGroupLinksRepository;
  private final RelatedResourcesSubmissionService relatedResourcesSubmissionService;

  public ResourceGroupService(RelatedResourcesPersistenceService relatedResourcesPersistenceService,
                              ResourceGroupRepository resourceGroupRepository,
                              ResourceGroupLinksRepository resourceGroupLinksRepository,
                              RelatedResourcesSubmissionService relatedResourcesSubmissionService) {
    this.relatedResourcesPersistenceService = relatedResourcesPersistenceService;
    this.resourceGroupRepository = resourceGroupRepository;
    this.resourceGroupLinksRepository = resourceGroupLinksRepository;
    this.relatedResourcesSubmissionService = relatedResourcesSubmissionService;
  }

  @Transactional
  public void createNewGroupForResource(Resource resource) {
    var newResourceGroup = new ResourceGroup();
    var resourceGroup = resourceGroupRepository.save(newResourceGroup);

    var resourceGroupLinkForResource = buildResourceGroupLink(resourceGroup, resource);

    resourceGroupLinksRepository.save(resourceGroupLinkForResource);
  }

  @Transactional
  public void createResourceGroupLink(SubmissionDetail submissionDetail, User user) {
    if (!relatedResourcesSubmissionService.isSectionSubmittable(submissionDetail, user)) {
      return;
    }

    relatedResourcesPersistenceService.getRelatedResourceIfRelatedToAnotherResource(submissionDetail)
        .ifPresent(relatedRes -> {

          var relatedResource = relatedRes.getRelatedSubmissionDetail().getSubmission().getResource();
          var currentResource = submissionDetail.getSubmission().getResource();

          var resourceGroupLink = resourceGroupLinksRepository.findByResource(relatedResource);
          addToGroup(currentResource, resourceGroupLink);
        });
  }

  public ResourceGroupLink getResourceGroupLinkByResource(Resource resource) {
    return resourceGroupLinksRepository.findByResource(resource);
  }

  public List<ResourceGroupLink> getResourceGroupLinksByResourceGroup(ResourceGroup resourceGroup) {
    return resourceGroupLinksRepository.findByResourceGroup(resourceGroup);
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
    var resourceGroupLink = getResourceGroupLinkByResource(resource);
    resourceGroupLinksRepository.delete(resourceGroupLink);
  }

  private void addToGroup(Resource currentResourceResource,
                          ResourceGroupLink resourceGroupLinkForRelatedResource) {
    var resourceGroupId = resourceGroupLinkForRelatedResource.getResourceGroup();

    // create the resource group link
    var resourceGroupLinkForCurrentResource = buildResourceGroupLink(
        resourceGroupId,
        currentResourceResource
    );
    resourceGroupLinksRepository.save(resourceGroupLinkForCurrentResource);
  }

  static ResourceGroupLink buildResourceGroupLink(ResourceGroup resourceGroup, Resource resource) {
    var resourceGroupLinkForRelated = new ResourceGroupLink();
    resourceGroupLinkForRelated.setResourceGroup(resourceGroup);
    resourceGroupLinkForRelated.setResource(resource);
    return resourceGroupLinkForRelated;
  }
}
