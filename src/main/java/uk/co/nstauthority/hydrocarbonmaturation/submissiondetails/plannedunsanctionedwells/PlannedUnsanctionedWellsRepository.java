package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
public interface PlannedUnsanctionedWellsRepository extends CrudRepository<PlannedUnsanctionedWells, UUID> {
  Optional<PlannedUnsanctionedWells> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
