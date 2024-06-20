package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
public interface OilCaseVolumetricTotalsRepository extends CrudRepository<OilCaseVolumetricTotal, UUID> {

  Optional<OilCaseVolumetricTotal> findBySubmissionDetail(SubmissionDetail submissionDetail);

  void deleteBySubmissionDetail(SubmissionDetail submissionDetail);
}
