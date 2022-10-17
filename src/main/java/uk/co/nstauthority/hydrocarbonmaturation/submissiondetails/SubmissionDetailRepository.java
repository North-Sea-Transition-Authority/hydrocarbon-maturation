package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
interface SubmissionDetailRepository extends CrudRepository<SubmissionDetail, UUID> {

  Optional<SubmissionDetail> findFirstByResourceIdOrderByCreatedTimestampDesc(Resource resource);
}
