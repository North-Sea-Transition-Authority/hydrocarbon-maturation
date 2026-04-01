package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
public interface UndevelopedDiscoveryRepository extends CrudRepository<UndevelopedDiscovery, UUID> {
  Optional<UndevelopedDiscovery> findByResource(Resource resource);

  @EntityGraph(attributePaths = {"resource"})
  List<UndevelopedDiscovery> findByIsUndevelopedDiscovery(boolean isUndevelopedDiscovery);
}
