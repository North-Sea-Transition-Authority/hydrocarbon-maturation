package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Repository
interface ProbabilityDistributionRepository
    extends CrudRepository<ProbabilityDistribution, UUID> {

  Optional<ProbabilityDistribution> findBySubmissionDetailAndInstance(
      SubmissionDetail submissionDetail,
      ProbabilityDistributionFieldInstance instance);

  void deleteBySubmissionDetailAndInstance(
      SubmissionDetail submissionDetail,
      ProbabilityDistributionFieldInstance instance);

  List<ProbabilityDistribution> findBySubmissionDetailAndInstanceIn(SubmissionDetail submissionDetail,
                                                                    Set<ProbabilityDistributionFieldInstance> instances);

  Optional<ProbabilityDistribution> findByReservoirSubmissionDetailAndInstance(
      ReservoirSubmissionDetail reservoirSubmissionDetail,
      ProbabilityDistributionFieldInstance instance);

  void deleteByReservoirSubmissionDetailAndInstance(
      ReservoirSubmissionDetail reservoirSubmissionDetail,
      ProbabilityDistributionFieldInstance instance);

  void deleteBySubmissionDetailAndInstanceIn(
      SubmissionDetail submissionDetail,
      List<ProbabilityDistributionFieldInstance> instance);

  void deleteByReservoirSubmissionDetailAndInstanceIn(
      ReservoirSubmissionDetail reservoirSubmissionDetail,
      List<ProbabilityDistributionFieldInstance> instance);

  List<ProbabilityDistribution> findByReservoirSubmissionDetailAndInstanceIn(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                                    Set<ProbabilityDistributionFieldInstance> instances);
}
