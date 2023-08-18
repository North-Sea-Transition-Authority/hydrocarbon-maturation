package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ReservoirRepository extends CrudRepository<Reservoir, UUID> {

  List<Reservoir> findAllByIdIn(List<UUID> reservoirIds);
}
