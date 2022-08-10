package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TrapPropertiesRepository extends CrudRepository<TrapProperty, UUID> {
}
