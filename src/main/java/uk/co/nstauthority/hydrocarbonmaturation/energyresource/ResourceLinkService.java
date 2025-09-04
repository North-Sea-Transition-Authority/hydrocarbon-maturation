package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceLinkService {

  private final ResourceLinksRepository resourceLinksRepository;
  private final ResourceService resourceService;

  @Autowired
  public ResourceLinkService(ResourceLinksRepository resourceLinksRepository, ResourceService resourceService) {
    this.resourceLinksRepository = resourceLinksRepository;
    this.resourceService = resourceService;
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

  @Transactional
  public void clearAllExistingLinksForField(Resource field) {
    resourceLinksRepository.deleteAllByFieldResource(field);
  }

  public List<ResourceLink> getResourceLinksByProspect(Resource prospect) {
    return resourceLinksRepository.findAllByProspectResource(prospect);
  }

  public boolean prospectIsLinkedToDiscovery(Resource resource) {
    if (resource.getType() == ResourceType.PROSPECT) {
      return resourceLinksRepository.existsByProspectResourceAndDiscoveryResourceIsNotNull(resource);
    } else {
      return false;
    }
  }

  @Transactional
  public void deleteDiscoveryToProspectLink(Resource discovery) {
    resourceLinksRepository.deleteByDiscoveryResourceAndProspectResourceIsNotNull(discovery);
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
            (resourceLink.getDiscoveryResource() != null
                && resourceLink.getDiscoveryResource().getId().equals(otherResource.getId()))
                || (resourceLink.getProspectResource() != null
                && resourceLink.getProspectResource().getId().equals(otherResource.getId()))
                || (resourceLink.getFieldResource() != null
                && resourceLink.getFieldResource().getId().equals(otherResource.getId())));
  }

  public boolean discoveryIsLinkedToField(Resource resource) {
    if (!ResourceType.DISCOVERY.equals(resource.getType())) {
      return false;
    }

    return getResourceLinksByResource(resource)
        .stream()
        .anyMatch(this::resourceLinkHasActiveField);
  }

  public List<Resource> getAssociatedFieldResources(Resource discoveryResource) {
    return getResourceLinksByDiscovery(discoveryResource)
        .stream()
        .filter(this::resourceLinkHasActiveField)
        .map(ResourceLink::getFieldResource)
        .toList();
  }

  private List<ResourceLink> getResourceLinksByResource(Resource resource) {
    return switch (resource.getType()) {
      case PROSPECT -> getResourceLinksByProspect(resource);
      case DISCOVERY -> getResourceLinksByDiscovery(resource);
      case FIELD -> getResourceLinksByField(resource);
    };
  }

  private boolean resourceLinkHasActiveField(ResourceLink resourceLink) {
    var fieldResource = resourceLink.getFieldResource();
    if (fieldResource == null) {
      return false;
    }

    return resourceService.getResourceById(fieldResource.getId())
        .filter(resource -> BooleanUtils.isTrue(resource.getActive()))
        .isPresent();
  }
}
