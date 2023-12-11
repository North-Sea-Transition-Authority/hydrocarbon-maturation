package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
public interface PlannedSanctionedWellsRepository extends CrudRepository<PlannedSanctionedWells, UUID> {
  Optional<PlannedSanctionedWells> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
