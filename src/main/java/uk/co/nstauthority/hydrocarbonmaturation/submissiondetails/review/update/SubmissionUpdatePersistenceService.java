package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import jakarta.transaction.Transactional;
import java.time.Clock;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService;

@Service
class SubmissionUpdatePersistenceService {

  private final SubmissionUpdateRepository submissionUpdateRepository;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionCreateService submissionCreateService;
  private final SubmissionEmailService submissionEmailService;
  private final Clock clock;

  @Autowired
  SubmissionUpdatePersistenceService(SubmissionUpdateRepository submissionUpdateRepository,
                                     SubmissionDetailService submissionDetailService,
                                     SubmissionCreateService submissionCreateService,
                                     SubmissionEmailService submissionEmailService,
                                     Clock clock) {
    this.submissionUpdateRepository = submissionUpdateRepository;
    this.submissionDetailService = submissionDetailService;
    this.submissionCreateService = submissionCreateService;
    this.submissionEmailService = submissionEmailService;
    this.clock = clock;
  }

  @Transactional
  public void requestUpdate(SubmissionDetail submissionDetail, RequestUpdateForm form, User user) {
    var submissionUpdate = new SubmissionUpdate();
    submissionUpdate.setSubmission(submissionDetail.getSubmission());
    submissionUpdate.setReason(form.getUpdateReason());
    submissionUpdate.setRequestedByWuaId(user.wuaId().intValue());
    submissionUpdate.setRequestedTimestamp(clock.instant());
    submissionUpdate.setResolved(false);
    submissionUpdateRepository.save(submissionUpdate);
    submissionEmailService.sendReturnedForUpdateEmails(submissionDetail);
  }

  Optional<SubmissionUpdate> getLatestUnresolvedSubmissionUpdateBySubmission(Submission submission) {
    return submissionUpdateRepository.findFirstBySubmissionAndResolvedFalseOrderByRequestedTimestampDesc(submission);
  }

  boolean submissionHasAnUnresolvedUpdate(Submission submission) {
    return submissionUpdateRepository.existsBySubmissionAndResolvedFalse(submission);
  }

  @Transactional
  SubmissionDetail startUpdate(SubmissionDetail parentSubmissionDetail, User user) {
    return submissionCreateService.createUpdateSubmissionDetail(parentSubmissionDetail, user);
  }

  @Transactional
  SubmissionUpdate resolveUpdate(SubmissionUpdate submissionUpdate, User user) {
    submissionUpdate.setResolved(true);
    submissionUpdate.setResolvedTimestamp(clock.instant());
    submissionUpdate.setResolvedByWuaId(user.wuaId().intValue());
    submissionUpdateRepository.save(submissionUpdate);
    return submissionUpdate;
  }

  Optional<SubmissionDetail> getDraftUpdateSubmissionDetail(Submission submission) {
    var submissionDetails = submissionDetailService.getSubmissionDetailsBySubmission(submission);
    if (submissionDetails.size() == 1) {
      //single submission detail under a submission is never a draft update submission detail
      return Optional.empty();
    }

    var draftUpdateSubmission = submissionDetails
        .stream()
        .filter(submissionDetail -> submissionDetail.getStatus() == SubmissionDetailStatus.DRAFT)
        .toList();
    if (draftUpdateSubmission.size() == 1) {
      return Optional.of(draftUpdateSubmission.get(0));
    } else {
      return Optional.empty();
    }
  }
}
