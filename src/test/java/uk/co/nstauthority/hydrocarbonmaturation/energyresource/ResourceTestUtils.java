package uk.co.nstauthority.hydrocarbonmaturation.energyresource;


import java.util.UUID;

public class ResourceTestUtils {

  private ResourceTestUtils() {
    throw new AssertionError();
  }

  public static Resource buildResource(ResourceType resourceType) {
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    resource.setType(resourceType);
    return resource;
  }
}
