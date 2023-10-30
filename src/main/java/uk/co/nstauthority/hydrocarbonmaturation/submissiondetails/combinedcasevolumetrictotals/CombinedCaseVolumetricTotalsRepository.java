package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
public interface CombinedCaseVolumetricTotalsRepository extends CrudRepository<CombinedCaseVolumetricTotal, UUID> {

  Optional<CombinedCaseVolumetricTotal> findBySubmissionDetail(SubmissionDetail submissionDetail);

  void deleteBySubmissionDetail(SubmissionDetail submissionDetail);
}
