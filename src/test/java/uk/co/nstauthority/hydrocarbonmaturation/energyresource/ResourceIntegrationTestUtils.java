package uk.co.nstauthority.hydrocarbonmaturation.energyresource;


import jakarta.persistence.EntityManager;
import java.time.Instant;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;

public class ResourceIntegrationTestUtils {

  private ResourceIntegrationTestUtils() {
    throw new AssertionError();
  }

  public static Resource buildResource(ResourceType resourceType, EntityManager entityManager) {
    var resource = new Resource();
    resource.setType(resourceType);
    resource.setActive(true);
    resource.setCreatedTimestamp(Instant.now());
    entityManager.persist(resource);
    entityManager.flush();

    if (resourceType == ResourceType.FIELD) {
      var field = new Field();
      field.setResource(resource);
      entityManager.persist(field);
      entityManager.flush();
    }

    return resource;
  }

  public static Resource buildFieldResource(EntityManager entityManager) {
    var resource = new Resource();
    resource.setType(ResourceType.FIELD);
    resource.setActive(true);
    resource.setCreatedTimestamp(Instant.now());

    entityManager.persist(resource);
    entityManager.flush();

    return resource;
  }
}
