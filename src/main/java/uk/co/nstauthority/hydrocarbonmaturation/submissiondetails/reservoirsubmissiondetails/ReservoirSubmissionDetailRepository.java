package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

interface ReservoirSubmissionDetailRepository extends CrudRepository<ReservoirSubmissionDetail, UUID> {

  List<ReservoirSubmissionDetail> findBySubmissionDetailOrderByReservoir_Name(SubmissionDetail submissionDetail);
}
