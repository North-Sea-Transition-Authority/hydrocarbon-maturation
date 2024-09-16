package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, UUID> {
}
