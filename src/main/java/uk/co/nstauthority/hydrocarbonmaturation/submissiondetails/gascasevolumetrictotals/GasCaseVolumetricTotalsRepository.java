package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
public interface GasCaseVolumetricTotalsRepository extends CrudRepository<GasCaseVolumetricTotal, UUID> {

  Optional<GasCaseVolumetricTotal> findBySubmissionDetail(SubmissionDetail submissionDetail);

  void deleteBySubmissionDetail(SubmissionDetail submissionDetail);
}
