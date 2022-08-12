package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface GeneralDetailsRepository extends CrudRepository<GeneralDetail, UUID> {

  Optional<GeneralDetail> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
