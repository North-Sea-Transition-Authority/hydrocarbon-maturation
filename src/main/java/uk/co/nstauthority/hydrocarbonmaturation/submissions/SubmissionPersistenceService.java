package uk.co.nstauthority.hydrocarbonmaturation.submissions;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Service
public class SubmissionPersistenceService {

  private final SubmissionRepository submissionRepository;
  private final Clock clock;
  private final UserService userService;

  @Autowired
  SubmissionPersistenceService(SubmissionRepository submissionRepository, Clock clock,
                               UserService userService) {
    this.submissionRepository = submissionRepository;
    this.clock = clock;
    this.userService = userService;
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
  public Submission withdrawSubmission(Submission submission, String withdrawalReason) {
    submission.setWithdrawnTimestamp(clock.instant());
    submission.setWithdrawnByWuaId(userService.getUserWuaId());
    submission.setWithdrawalReason(withdrawalReason);
    submission.setWithdrawn(true);
    submissionRepository.save(submission);
    return submission;
  }
}
