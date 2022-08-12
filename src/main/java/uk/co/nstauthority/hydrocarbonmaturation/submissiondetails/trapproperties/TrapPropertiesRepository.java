package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface TrapPropertiesRepository extends CrudRepository<TrapProperty, UUID> {

  Optional<TrapProperty> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
