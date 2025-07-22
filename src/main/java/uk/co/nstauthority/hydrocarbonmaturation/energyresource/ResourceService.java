package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

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

  public Resource getResourceOrThrow(UUID resourceId) {
    return resourceRepository.findById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find resource with id %s".formatted(resourceId.toString())));
  }

  public Optional<Resource> getResourceOfType(UUID resourceId, ResourceType resourceType) {
    var resource = getResourceOrThrow(resourceId);
    if (resource.getType() != resourceType) {
      return Optional.empty();
    }
    return Optional.of(resource);
  }

  public Resource getResourceOfTypeOrThrow(UUID resourceId, ResourceType resourceType) {
    return getResourceOfType(resourceId, resourceType)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource %s with type %s"
            .formatted(resourceId, resourceType.getDisplayName())));
  }

  public Resource createResource(ResourceType resourceType) {
    var resource = new Resource();
    resource.setType(resourceType);
    resource.setCreatedTimestamp(clock.instant());
    resource.setActive(true);
    resourceRepository.save(resource);
    return resource;
  }
}
