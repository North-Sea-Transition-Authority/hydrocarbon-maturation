package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
public interface FdpaIncreasedVolumesRepository
    extends CrudRepository<FdpaIncreasedVolume, UUID> {

  Optional<FdpaIncreasedVolume> findBySubmissionDetail(SubmissionDetail submissionDetail);
}
