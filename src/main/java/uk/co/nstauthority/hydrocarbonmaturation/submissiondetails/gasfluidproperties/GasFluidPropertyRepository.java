package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface GasFluidPropertyRepository extends CrudRepository<GasFluidProperty, UUID> {

  Optional<GasFluidProperty> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
