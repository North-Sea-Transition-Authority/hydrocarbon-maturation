package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface ReservoirSubmissionDetailRepository extends CrudRepository<ReservoirSubmissionDetail, UUID> {

  List<ReservoirSubmissionDetail> findBySubmissionDetailOrderByReservoir_Name(SubmissionDetail submissionDetail);
}
