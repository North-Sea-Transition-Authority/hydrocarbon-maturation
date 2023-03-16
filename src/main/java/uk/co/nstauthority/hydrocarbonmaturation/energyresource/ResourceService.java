package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {

  private final ResourceRepository resourceRepository;
  private final Clock clock;

  @Autowired
  ResourceService(ResourceRepository resourceRepository, Clock clock) {
    this.resourceRepository = resourceRepository;
    this.clock = clock;
  }

  public Optional<Resource> getResourceById(UUID id) {
    return resourceRepository.findById(id);
  }

  public Resource createResource(ResourceType resourceType) {
    var resource = new Resource();
    resource.setType(resourceType);
    resource.setCreatedTimestamp(clock.instant());
    resourceRepository.save(resource);
    return resource;
  }
}
