package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface ResourceDetailsRepository extends CrudRepository<ResourceDetail, UUID> {

  Optional<ResourceDetail> findBySubmissionDetail(SubmissionDetail submissionDetail);

  List<ResourceDetail> findByResourceNameContainingIgnoreCase(String name);
}
