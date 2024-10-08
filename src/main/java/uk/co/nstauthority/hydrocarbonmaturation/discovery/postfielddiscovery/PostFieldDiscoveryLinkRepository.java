package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
public interface PostFieldDiscoveryLinkRepository extends CrudRepository<PostFieldDiscoveryLink, UUID> {

  Optional<PostFieldDiscoveryLink> getPostFieldDiscoveryLinkByDiscoveryResource(Resource discoveryResource);

  List<PostFieldDiscoveryLink> getPostFieldDiscoveryLinksByFieldResource(Resource fieldResource);

  boolean existsByDiscoveryResource(Resource discoveryResource);
}
