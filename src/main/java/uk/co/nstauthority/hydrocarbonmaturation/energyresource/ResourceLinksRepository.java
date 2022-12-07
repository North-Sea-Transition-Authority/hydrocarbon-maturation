package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ResourceLinksRepository extends CrudRepository<ResourceLink, UUID> {

  List<ResourceLink> findAllByProspectResource(Resource prospect);
}
