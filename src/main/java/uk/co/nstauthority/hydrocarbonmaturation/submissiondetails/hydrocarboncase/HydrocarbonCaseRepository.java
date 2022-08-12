package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface HydrocarbonCaseRepository extends CrudRepository<HydrocarbonCaseDetail, UUID> {

  Optional<HydrocarbonCaseDetail> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
