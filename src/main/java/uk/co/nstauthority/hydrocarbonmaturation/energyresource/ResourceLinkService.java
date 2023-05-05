package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceLinkService {

  private final ResourceLinksRepository resourceLinksRepository;

  @Autowired
  public ResourceLinkService(ResourceLinksRepository resourceLinksRepository) {
    this.resourceLinksRepository = resourceLinksRepository;
  }

  @Transactional
  public void createNewResourceLink(Resource prospect, Resource discovery) {
    var resourceLink = new ResourceLink();
    resourceLink.setDiscoveryResource(discovery);
    resourceLink.setProspectResource(prospect);
    resourceLinksRepository.save(resourceLink);
  }

  public List<ResourceLink> getResourceLinksByProspect(Resource prospect) {
    return resourceLinksRepository.findAllByProspectResource(prospect);
  }

  public List<ResourceLink> getResourceLinksByDiscovery(Resource discovery) {
    return resourceLinksRepository.findAllByDiscoveryResource(discovery);
  }

  public boolean isResourceLinkedOrSameResource(Resource resource, Resource otherResource) {
    if (resource.getId().equals(otherResource.getId())) {
      return true;
    }

    return getResourceLinksByResource(resource)
        .stream()
        .anyMatch(resourceLink ->
            resourceLink.getDiscoveryResource().getId().equals(otherResource.getId())
                || resourceLink.getProspectResource().getId().equals(otherResource.getId()));
  }

  private List<ResourceLink> getResourceLinksByResource(Resource resource) {
    if (resource.getType() == ResourceType.PROSPECT) {
      return getResourceLinksByProspect(resource);
    } else if (resource.getType() == ResourceType.DISCOVERY) {
      return getResourceLinksByDiscovery(resource);
    } else {
      throw new NotImplementedException("This functionality has not yet been implemented for resource type %s"
          .formatted(resource.getType().getDisplayName()));
    }
  }
}
