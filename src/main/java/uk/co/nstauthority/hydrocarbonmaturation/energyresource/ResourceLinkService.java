package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.List;
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
  public void createNewProspectDiscoveryResourceLink(Resource prospect, Resource discovery) {
    var resourceLink = new ResourceLink();
    resourceLink.setDiscoveryResource(discovery);
    resourceLink.setProspectResource(prospect);
    resourceLinksRepository.save(resourceLink);
  }

  @Transactional
  public void createNewDiscoveryFieldResourceLink(Resource discovery, Resource field) {
    var resourceLink = new ResourceLink();
    resourceLink.setDiscoveryResource(discovery);
    resourceLink.setFieldResource(field);
    resourceLinksRepository.save(resourceLink);
  }

  public List<ResourceLink> getResourceLinksByProspect(Resource prospect) {
    return resourceLinksRepository.findAllByProspectResource(prospect);
  }

  public boolean prospectIsLinkedToDiscovery(Resource resource) {
    if (resource.getType() == ResourceType.PROSPECT) {
      return resourceLinksRepository.existsByProspectResource(resource);
    } else {
      return false;
    }
  }

  public List<ResourceLink> getResourceLinksByDiscovery(Resource discovery) {
    return resourceLinksRepository.findAllByDiscoveryResource(discovery);
  }

  public List<ResourceLink> getResourceLinksByField(Resource field) {
    return resourceLinksRepository.findAllByFieldResource(field);
  }

  public boolean isResourceLinkedOrSameResource(Resource resource, Resource otherResource) {
    if (resource.getId().equals(otherResource.getId())) {
      return true;
    }

    return getResourceLinksByResource(resource)
        .stream()
        .anyMatch(resourceLink ->
            resourceLink.getDiscoveryResource().getId().equals(otherResource.getId())
                || resourceLink.getProspectResource().getId().equals(otherResource.getId())
                || resourceLink.getFieldResource().getId().equals(otherResource.getId()));
  }

  private List<ResourceLink> getResourceLinksByResource(Resource resource) {
    return switch (resource.getType()) {
      case PROSPECT -> getResourceLinksByProspect(resource);
      case DISCOVERY -> getResourceLinksByDiscovery(resource);
      case FIELD -> getResourceLinksByField(resource);
    };
  }
}
