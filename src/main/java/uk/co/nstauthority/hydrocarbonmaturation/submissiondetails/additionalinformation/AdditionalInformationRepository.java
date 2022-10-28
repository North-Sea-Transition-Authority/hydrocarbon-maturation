package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

interface AdditionalInformationRepository extends CrudRepository<AdditionalInformation, UUID> {

  Optional<AdditionalInformation> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
