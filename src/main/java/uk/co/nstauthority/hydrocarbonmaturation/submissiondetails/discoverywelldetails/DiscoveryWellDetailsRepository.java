package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface DiscoveryWellDetailsRepository extends CrudRepository<DiscoveryWellDetail, UUID> {

  Optional<DiscoveryWellDetail> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
