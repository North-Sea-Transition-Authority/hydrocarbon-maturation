package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface GeneralDetailsRepository extends CrudRepository<GeneralDetail, UUID> {
}
