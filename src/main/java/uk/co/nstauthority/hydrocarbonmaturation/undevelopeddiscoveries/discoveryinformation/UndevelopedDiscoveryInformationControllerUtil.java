package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;

public class UndevelopedDiscoveryInformationControllerUtil {

  private UndevelopedDiscoveryInformationControllerUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static void verifyInformationBelongsToDiscoveryOrThrow(UndevelopedDiscovery undevelopedDiscovery,
                                                                UndevelopedDiscoveryInformation informationEntity) {
    var discoveryTiedToResource = informationEntity.getUndevelopedDiscovery();
    if (!discoveryTiedToResource.getId().equals(undevelopedDiscovery.getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "Undeveloped discovery information %s does not belong to resource %s".formatted(
              informationEntity.getId(), undevelopedDiscovery.getResource().getId()
          )
      );
    }
  }

  public static void throwIfInformationNotExtant(UndevelopedDiscoveryInformation information) {
    if (!information.getStatus().equals(UndevelopedDiscoveryInformationStatus.EXTANT)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Information %s is %s, required status %s.".formatted(
          information.getId(), information.getStatus().name(), UndevelopedDiscoveryInformationStatus.EXTANT));
    }
  }
}
