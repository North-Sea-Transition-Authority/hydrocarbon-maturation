package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface OpportunityDetailsRepository extends CrudRepository<OpportunityDetail, UUID> {

  Optional<OpportunityDetail> findBySubmissionDetail(SubmissionDetail submissionDetail);
}