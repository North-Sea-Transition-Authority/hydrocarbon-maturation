package uk.co.nstauthority.hydrocarbonmaturation.energyresource;


import java.time.Instant;
import javax.persistence.EntityManager;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;

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

    if (resourceType == ResourceType.FIELD) {
      var field = new Field();
      field.setResource(resource);
      entityManager.persist(field);
      entityManager.flush();
    }

    return resource;
  }
}
