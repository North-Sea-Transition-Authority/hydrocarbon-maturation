package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostFieldDiscoveryLinkRepository extends CrudRepository<PostFieldDiscoveryLink, UUID> {
}
