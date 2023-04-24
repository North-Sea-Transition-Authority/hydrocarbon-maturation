package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import java.time.Clock;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Service
class SubmissionUpdatePersistenceService {

  private final SubmissionUpdateRepository submissionUpdateRepository;
  private final UserDetailService userDetailService;
  private final Clock clock;

  @Autowired
  SubmissionUpdatePersistenceService(SubmissionUpdateRepository submissionUpdateRepository,
                                     UserDetailService userDetailService,
                                     Clock clock) {
    this.submissionUpdateRepository = submissionUpdateRepository;
    this.userDetailService = userDetailService;
    this.clock = clock;
  }

  @Transactional
  public void requestUpdate(SubmissionDetail submissionDetail, RequestUpdateForm form) {
    var user = userDetailService.getUserDetail();
    var submissionUpdate = new SubmissionUpdate();
    submissionUpdate.setSubmission(submissionDetail.getSubmission());
    submissionUpdate.setReason(form.getUpdateReason());
    submissionUpdate.setRequestedByWuaId(user.wuaId().intValue());
    submissionUpdate.setRequestedTimestamp(clock.instant());
    submissionUpdate.setResolved(false);
    submissionUpdateRepository.save(submissionUpdate);
  }

  Optional<SubmissionUpdate> getLatestSubmissionUpdateForSubmission(Submission submission) {
    return submissionUpdateRepository.findFirstBySubmissionOrderByRequestedTimestampDesc(submission);
  }
}
