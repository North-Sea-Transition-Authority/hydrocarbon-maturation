package uk.co.nstauthority.hydrocarbonmaturation.energyresource;


import java.time.Instant;
import javax.persistence.EntityManager;

public class ResourceIntegrationTestUtils {

  private ResourceIntegrationTestUtils() {
    throw new AssertionError();
  }

  public static Resource buildResource(ResourceType resourceType, EntityManager entityManager) {
    var resource = new Resource();
    resource.setType(resourceType);
    resource.setCreatedTimestamp(Instant.now());
    entityManager.persist(resource);
    entityManager.flush();
    return resource;
  }
}
