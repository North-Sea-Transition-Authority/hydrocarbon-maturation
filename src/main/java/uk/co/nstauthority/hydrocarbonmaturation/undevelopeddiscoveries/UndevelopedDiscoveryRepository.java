package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
public interface UndevelopedDiscoveryRepository extends CrudRepository<UndevelopedDiscovery, UUID> {
  Optional<UndevelopedDiscovery> findByResource(Resource resource);
}
