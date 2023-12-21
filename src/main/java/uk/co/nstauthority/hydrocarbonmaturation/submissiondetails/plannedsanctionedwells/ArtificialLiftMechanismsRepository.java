package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtificialLiftMechanismsRepository extends CrudRepository<ArtificialLiftMechanisms, UUID> {
  List<ArtificialLiftMechanisms> findAllByPlannedSanctionedWells(PlannedSanctionedWells plannedSanctionedWells);

  void deleteAllByPlannedSanctionedWells(PlannedSanctionedWells plannedSanctionedWells);
}
