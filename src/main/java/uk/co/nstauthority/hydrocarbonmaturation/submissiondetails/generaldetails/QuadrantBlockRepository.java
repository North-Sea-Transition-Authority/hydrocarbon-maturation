package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface QuadrantBlockRepository extends CrudRepository<QuadrantAndBlock, UUID> {
}
