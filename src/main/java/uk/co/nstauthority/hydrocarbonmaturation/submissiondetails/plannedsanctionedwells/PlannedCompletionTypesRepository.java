package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

public interface PlannedCompletionTypesRepository extends CrudRepository<PlannedCompletionTypes, UUID> {
  List<PlannedCompletionTypes> findAllByPlannedSanctionedWells(PlannedSanctionedWells plannedSanctionedWells);

  @Modifying
  void deleteAllByPlannedSanctionedWells(PlannedSanctionedWells plannedSanctionedWells);
}
