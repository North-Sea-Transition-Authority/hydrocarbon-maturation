package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface ReservoirPropertiesRepository extends CrudRepository<ReservoirProperty, UUID> {

  Optional<ReservoirProperty> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
