package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Repository
interface OilFluidPropertyRepository extends CrudRepository<OilFluidProperty, UUID> {

  Optional<OilFluidProperty> findByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);

  void deleteAllByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);
}
