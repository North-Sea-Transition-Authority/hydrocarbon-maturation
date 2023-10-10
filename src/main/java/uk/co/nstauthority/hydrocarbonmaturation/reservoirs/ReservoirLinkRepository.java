package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Repository
public interface ReservoirLinkRepository extends CrudRepository<ReservoirLink, UUID> {

  List<ReservoirLink> findReservoirLinksByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);

  List<ReservoirLink> findReservoirLinksByLinkedReservoir(Reservoir linkedReservoir);
}