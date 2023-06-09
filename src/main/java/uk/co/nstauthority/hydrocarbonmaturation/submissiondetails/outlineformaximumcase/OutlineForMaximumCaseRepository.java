package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface OutlineForMaximumCaseRepository extends CrudRepository<OutlineForMaximumCase, UUID> {

  Optional<OutlineForMaximumCase> findBySubmissionDetail(SubmissionDetail submissionDetail);

  void deleteBySubmissionDetail(SubmissionDetail submissionDetail);
}
