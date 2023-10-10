package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface ReservoirSubmissionDetailRepository extends CrudRepository<ReservoirSubmissionDetail, UUID> {

  List<ReservoirSubmissionDetail> findBySubmissionDetailAndStatusOrderByReservoir_Name(
      SubmissionDetail submissionDetail,
      ReservoirSubmissionDetailStatus status);

  boolean existsBySubmissionDetailAndStatus(SubmissionDetail submissionDetail, ReservoirSubmissionDetailStatus status);

  List<ReservoirSubmissionDetail> findAllByReservoir(Reservoir reservoir);

  Optional<ReservoirSubmissionDetail> findByIdAndStatus(UUID id, ReservoirSubmissionDetailStatus status);
}
