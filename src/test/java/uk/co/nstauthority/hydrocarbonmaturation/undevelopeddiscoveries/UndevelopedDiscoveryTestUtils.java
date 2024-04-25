package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

public class UndevelopedDiscoveryTestUtils {
  private UndevelopedDiscoveryTestUtils() {
    throw new AssertionError();
  }

  public static UndevelopedDiscovery createUndevelopedDiscovery(Resource resource, Boolean isUndevelopedDiscovery) {
    var undevelopedDiscovery = new UndevelopedDiscovery();
    undevelopedDiscovery.setId(UUID.randomUUID());
    undevelopedDiscovery.setIsUndevelopedDiscovery(isUndevelopedDiscovery);
    undevelopedDiscovery.setResource(resource);
    return undevelopedDiscovery;
  }
}
