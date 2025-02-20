package uk.co.nstauthority.hydrocarbonmaturation.submissions;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
public interface SubmissionRepository extends CrudRepository<Submission, UUID> {

  List<Submission> findSubmissionsByResource(Resource resource);

  List<Submission> findAllByResourceIn(List<Resource> resource);
}
