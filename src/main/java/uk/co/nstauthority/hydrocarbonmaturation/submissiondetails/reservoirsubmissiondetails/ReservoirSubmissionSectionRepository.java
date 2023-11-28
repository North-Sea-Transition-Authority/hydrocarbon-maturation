package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservoirSubmissionSectionRepository extends CrudRepository<ReservoirSubmissionSection, UUID> {
  Set<ReservoirSubmissionSection> findAllByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);

  void deleteAllByReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail);
}
