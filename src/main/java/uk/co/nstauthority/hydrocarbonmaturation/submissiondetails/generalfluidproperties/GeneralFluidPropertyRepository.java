package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface GeneralFluidPropertyRepository extends CrudRepository<GeneralFluidProperty, UUID> {

  Optional<GeneralFluidProperty> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
