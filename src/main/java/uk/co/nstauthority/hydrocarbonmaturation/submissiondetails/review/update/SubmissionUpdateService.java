package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Service
public class SubmissionUpdateService {

  private final RequestUpdateFormValidator requestUpdateFormValidator;
  private final SubmissionUpdatePersistenceService submissionUpdatePersistenceService;

  @Autowired
  SubmissionUpdateService(RequestUpdateFormValidator requestUpdateFormValidator,
                          SubmissionUpdatePersistenceService submissionUpdatePersistenceService) {
    this.requestUpdateFormValidator = requestUpdateFormValidator;
    this.submissionUpdatePersistenceService = submissionUpdatePersistenceService;
  }

  public boolean canRequestUpdateOnSubmission(SubmissionDetail submissionDetail) {
    if (submissionDetail.getStatus() != SubmissionDetailStatus.SUBMITTED) {
      return false;
    }

    return submissionUpdatePersistenceService.getLatestSubmissionUpdateBySubmission(submissionDetail.getSubmission())
        .map(submissionUpdate -> BooleanUtils.isTrue(submissionUpdate.getResolved()))
        .orElse(true);
  }

  public boolean hasAnUnresolvedUpdateOnSubmission(Submission submission) {
    return submissionUpdatePersistenceService.submissionHasAnUnresolvedUpdate(submission);
  }

  public Optional<SubmissionDetail> getDraftUpdateSubmissionDetailBySubmission(Submission submission) {
    return submissionUpdatePersistenceService.getDraftUpdateSubmissionDetail(submission);
  }

  BindingResult validate(BindingResult bindingResult, RequestUpdateForm form) {
    requestUpdateFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  boolean canStartUpdateSubmissionDetailOnSubmission(Submission submission) {
    return hasAnUnresolvedUpdateOnSubmission(submission)
        && submissionUpdatePersistenceService.getDraftUpdateSubmissionDetail(submission).isEmpty();
  }

}
