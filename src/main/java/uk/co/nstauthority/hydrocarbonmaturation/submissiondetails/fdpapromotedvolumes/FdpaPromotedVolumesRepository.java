package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
public interface FdpaPromotedVolumesRepository
    extends CrudRepository<FdpaPromotedVolume, UUID> {

  Optional<FdpaPromotedVolume> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
