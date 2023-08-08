package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Repository
interface ReservoirHydrocarbonsRepository extends CrudRepository<ReservoirHydrocarbons, UUID> {
  Optional<ReservoirHydrocarbons> findReservoirHydrocarbonsByReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail);
}
