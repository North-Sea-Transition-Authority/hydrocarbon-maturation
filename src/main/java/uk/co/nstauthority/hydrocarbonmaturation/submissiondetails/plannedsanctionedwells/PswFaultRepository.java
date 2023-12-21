package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PswFaultRepository extends CrudRepository<PswFaults, UUID> {
  List<PswFaults> findAllByPlannedSanctionedWells(PlannedSanctionedWells plannedSanctionedWells);

  void deleteAllByPlannedSanctionedWells(PlannedSanctionedWells plannedSanctionedWells);
}
