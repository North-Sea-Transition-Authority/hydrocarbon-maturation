package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;

@Service
class WithdrawSubmissionService {

  private final SubmissionPersistenceService submissionPersistenceService;
  private final UserDetailService userDetailService;
  private final SubmissionEmailService submissionEmailService;
  private final WithdrawSubmissionFormValidator withdrawSubmissionFormValidator;
  private final SubmissionUpdateService submissionUpdateService;
  private final ResponsibleTeamService responsibleTeamService;

  @Autowired
  public WithdrawSubmissionService(SubmissionPersistenceService submissionPersistenceService,
                                   UserDetailService userDetailService,
                                   SubmissionEmailService submissionEmailService,
                                   WithdrawSubmissionFormValidator withdrawSubmissionFormValidator,
                                   SubmissionUpdateService submissionUpdateService,
                                   ResponsibleTeamService responsibleTeamService) {
    this.submissionPersistenceService = submissionPersistenceService;
    this.userDetailService = userDetailService;
    this.submissionEmailService = submissionEmailService;
    this.withdrawSubmissionFormValidator = withdrawSubmissionFormValidator;
    this.submissionUpdateService = submissionUpdateService;
    this.responsibleTeamService = responsibleTeamService;
  }

  BindingResult validate(BindingResult bindingResult, WithdrawSubmissionForm form) {
    withdrawSubmissionFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  void withdrawSubmissionOrThrow(SubmissionDetail submissionDetail,
                                 WithdrawSubmissionForm form) {
    if (canWithdrawSubmission(submissionDetail)) {
      submissionPersistenceService.withdrawSubmission(submissionDetail.getSubmission(), form.getWithdrawReason());

      submissionEmailService.sendSubmissionWithdrawalEmail(submissionDetail);
    } else {
      throw new ActionNotAllowedException("Submission cannot be withdrawn by user with ID %s"
          .formatted(userDetailService.getUserWuaId()));
    }
  }

  boolean canWithdrawSubmission(SubmissionDetail submissionDetail) {
    if (submissionDetail.getStatus() != SubmissionDetailStatus.SUBMITTED) {
      return false;
    }

    return responsibleTeamService
        .isInResponsibleTeam(userDetailService.getUserDetail(), submissionDetail)
        && !submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission());
  }
}
