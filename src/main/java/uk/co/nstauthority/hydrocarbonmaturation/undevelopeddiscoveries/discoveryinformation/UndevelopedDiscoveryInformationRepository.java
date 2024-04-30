package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;

@Repository
public interface UndevelopedDiscoveryInformationRepository extends CrudRepository<UndevelopedDiscoveryInformation, UUID> {
  List<UndevelopedDiscoveryInformation> findAllByUndevelopedDiscovery(UndevelopedDiscovery undevelopedDiscovery);
}