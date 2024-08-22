package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;

@Service
class WithdrawSubmissionService {

  private final SubmissionPersistenceService submissionPersistenceService;
  private final WithdrawSubmissionFormValidator withdrawSubmissionFormValidator;
  private final SubmissionUpdateService submissionUpdateService;
  private final ResponsibleTeamService responsibleTeamService;
  private final SubmissionEmailService submissionEmailService;

  @Autowired
  public WithdrawSubmissionService(SubmissionPersistenceService submissionPersistenceService,
                                   WithdrawSubmissionFormValidator withdrawSubmissionFormValidator,
                                   SubmissionUpdateService submissionUpdateService,
                                   ResponsibleTeamService responsibleTeamService,
                                   SubmissionEmailService submissionEmailService) {
    this.submissionPersistenceService = submissionPersistenceService;
    this.withdrawSubmissionFormValidator = withdrawSubmissionFormValidator;
    this.submissionUpdateService = submissionUpdateService;
    this.responsibleTeamService = responsibleTeamService;
    this.submissionEmailService = submissionEmailService;
  }

  BindingResult validate(BindingResult bindingResult, WithdrawSubmissionForm form) {
    withdrawSubmissionFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  void withdrawSubmissionOrThrow(SubmissionDetail submissionDetail,
                                 WithdrawSubmissionForm form,
                                 User user) {
    if (canWithdrawSubmission(submissionDetail, user)) {
      submissionPersistenceService.withdrawSubmission(submissionDetail.getSubmission(), form.getWithdrawReason(), user);
      submissionEmailService.sendSubmissionWithdrawalEmail(submissionDetail);
    } else {
      throw new ActionNotAllowedException("Submission cannot be withdrawn by user with ID %s"
          .formatted(user.wuaId()));
    }
  }

  boolean canWithdrawSubmission(SubmissionDetail submissionDetail, User user) {
    if (submissionDetail.getStatus() != SubmissionDetailStatus.SUBMITTED) {
      return false;
    }

    return responsibleTeamService
        .isInResponsibleTeam(user, submissionDetail)
        && !submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission());
  }
}
