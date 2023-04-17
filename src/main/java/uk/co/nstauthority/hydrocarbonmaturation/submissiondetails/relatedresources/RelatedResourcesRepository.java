package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface RelatedResourcesRepository extends CrudRepository<RelatedResource, UUID> {

  Optional<RelatedResource> findBySubmissionDetail(SubmissionDetail submissionDetail);
}