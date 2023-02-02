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
}
