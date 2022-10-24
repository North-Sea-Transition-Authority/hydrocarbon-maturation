package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface ProbabilityDistributionRepository
    extends CrudRepository<ProbabilityDistribution, UUID> {

  Optional<ProbabilityDistribution> findBySubmissionDetailAndInstance(
      SubmissionDetail submissionDetail,
      ProbabilityDistributionFieldInstance instance);

  void deleteBySubmissionDetailAndInstance(
      SubmissionDetail submissionDetail,
      ProbabilityDistributionFieldInstance instance);
}
