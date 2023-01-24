package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface RelatedProspectsRepository extends CrudRepository<RelatedProspect, UUID> {

  Optional<RelatedProspect> findBySubmissionDetail(SubmissionDetail submissionDetail);
}