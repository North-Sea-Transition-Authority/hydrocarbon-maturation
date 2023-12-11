package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoleCompletionTypesRepository extends CrudRepository<HoleCompletionTypes, UUID> {
  List<HoleCompletionTypes> findAllByPlannedSanctionedWells(PlannedSanctionedWells plannedSanctionedWells);
}
