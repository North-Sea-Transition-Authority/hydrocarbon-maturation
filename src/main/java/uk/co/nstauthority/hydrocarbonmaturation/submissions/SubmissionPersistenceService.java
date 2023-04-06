package uk.co.nstauthority.hydrocarbonmaturation.submissions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Service
public class SubmissionPersistenceService {

  private final SubmissionRepository submissionRepository;

  @Autowired
  SubmissionPersistenceService(SubmissionRepository submissionRepository) {
    this.submissionRepository = submissionRepository;
  }

  public Optional<Submission> getSubmissionById(UUID id) {
    return submissionRepository.findById(id);
  }

  public List<Submission> getSubmissionsByResource(Resource resource) {
    return submissionRepository.findSubmissionsByResource(resource);
  }

  @Transactional
  public Submission createSubmission(Resource resource) {
    var submission = new Submission();
    submission.setResource(resource);
    submissionRepository.save(submission);
    return submission;
  }
}
