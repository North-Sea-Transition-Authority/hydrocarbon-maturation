package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceLinksRepository extends CrudRepository<ResourceLink, UUID> {

  List<ResourceLink> findAllByProspectResource(Resource prospect);

  boolean existsByProspectResourceAndDiscoveryResourceIsNotNull(Resource prospect);

  List<ResourceLink> findAllByDiscoveryResource(Resource discovery);

  List<ResourceLink> findAllByFieldResource(Resource field);

  @EntityGraph(attributePaths = {"prospectResource", "discoveryResource", "fieldResource"})
  List<ResourceLink> findAllByDiscoveryResource_IdIn(List<UUID> discoveryResourceIds);

  void deleteByDiscoveryResourceAndProspectResourceIsNotNull(Resource discovery);

  void deleteAllByFieldResource(Resource field);
}
