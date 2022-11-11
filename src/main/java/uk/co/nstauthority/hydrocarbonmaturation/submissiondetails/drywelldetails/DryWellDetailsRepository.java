package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

interface DryWellDetailsRepository extends CrudRepository<DryWellDetail, UUID> {

  Optional<DryWellDetail> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
