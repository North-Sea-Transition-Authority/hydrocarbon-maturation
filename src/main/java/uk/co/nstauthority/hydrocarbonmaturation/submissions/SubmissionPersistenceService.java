package uk.co.nstauthority.hydrocarbonmaturation.submissions;

import jakarta.transaction.Transactional;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Service
public class SubmissionPersistenceService {

  private final SubmissionRepository submissionRepository;
  private final Clock clock;

  @Autowired
  SubmissionPersistenceService(SubmissionRepository submissionRepository, Clock clock) {
    this.submissionRepository = submissionRepository;
    this.clock = clock;
  }

  public Optional<Submission> getSubmissionById(UUID id) {
    return submissionRepository.findById(id);
  }

  public List<Submission> getSubmissionsByResource(Resource resource) {
    return submissionRepository.findSubmissionsByResource(resource)
        .stream()
        .filter(submission -> BooleanUtils.isNotTrue(submission.isWithdrawn()))
        .toList();
  }

  @Transactional
  public Submission createSubmission(Resource resource) {
    var submission = new Submission();
    submission.setResource(resource);
    submissionRepository.save(submission);
    return submission;
  }

  @Transactional
  public Submission withdrawSubmission(Submission submission, String withdrawalReason, User user) {
    submission.setWithdrawnTimestamp(clock.instant());
    submission.setWithdrawnByWuaId(user.wuaId().intValue());
    submission.setWithdrawalReason(withdrawalReason);
    submission.setWithdrawn(true);
    submissionRepository.save(submission);
    return submission;
  }
}
