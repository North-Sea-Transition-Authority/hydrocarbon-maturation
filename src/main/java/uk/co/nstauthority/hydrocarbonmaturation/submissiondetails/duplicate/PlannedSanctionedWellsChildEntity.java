package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWells;

public interface PlannedSanctionedWellsChildEntity {
  void setId(UUID id);

  void setPlannedSanctionedWells(PlannedSanctionedWells plannedSanctionedWells);
}
