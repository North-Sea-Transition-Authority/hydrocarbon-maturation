package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SubmissionDetailRepository extends CrudRepository<SubmissionDetail, UUID> {
}
