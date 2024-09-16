package uk.co.nstauthority.hydrocarbonmaturation.energyresource;


import java.time.Instant;
import java.util.UUID;

public class ResourceTestUtils {

  private ResourceTestUtils() {
    throw new AssertionError();
  }

  public static Resource buildResource(ResourceType resourceType) {
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    resource.setType(resourceType);
    resource.setCreatedTimestamp(Instant.now());
    return resource;
  }

  public static Resource buildResource(ResourceType resourceType, boolean isArchived) {
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    resource.setType(resourceType);
    resource.setCreatedTimestamp(Instant.now());
    resource.setArchived(isArchived);
    return resource;
  }
}
