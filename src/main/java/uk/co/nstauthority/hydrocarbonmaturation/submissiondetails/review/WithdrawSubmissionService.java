package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
class WithdrawSubmissionService {

  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final RegulatorTeamService regulatorTeamService;
  private final UserDetailService userDetailService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final SubmissionEmailService submissionEmailService;
  private final WithdrawSubmissionFormValidator withdrawSubmissionFormValidator;

  @Autowired
  public WithdrawSubmissionService(SubmissionDetailPersistenceService submissionDetailPersistenceService,
                                   RegulatorTeamService regulatorTeamService,
                                   UserDetailService userDetailService,
                                   RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                                   SubmissionEmailService submissionEmailService,
                                   WithdrawSubmissionFormValidator withdrawSubmissionFormValidator) {
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.regulatorTeamService = regulatorTeamService;
    this.userDetailService = userDetailService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.submissionEmailService = submissionEmailService;
    this.withdrawSubmissionFormValidator = withdrawSubmissionFormValidator;
  }

  BindingResult validate(BindingResult bindingResult, WithdrawSubmissionForm form) {
    withdrawSubmissionFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  void withdrawSubmissionOrThrow(SubmissionDetail submissionDetail,
                                 WithdrawSubmissionForm form) {
    if (canWithdrawSubmission(submissionDetail)) {
      submissionDetailPersistenceService.withdrawSubmission(submissionDetail, form.getWithdrawReason());

      submissionEmailService.sendSubmissionWithdrawalEmail(submissionDetail, form.getWithdrawReason());
    } else {
      throw new ActionNotAllowedException("Submission cannot be withdrawn by user with ID %s"
          .formatted(userDetailService.getUserWuaId()));
    }
  }

  boolean canWithdrawSubmission(SubmissionDetail submissionDetail) {
    if (submissionDetail.getStatus() != SubmissionDetailStatus.SUBMITTED) {
      return false;
    }
    var responsibleTeam = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getResourceId());
    return responsibleTeam.getResponsibleTeamType() == ResponsibleTeamType.EXPLORATION
        && regulatorTeamService.isRegulatorTeamRole(
        userDetailService.getUserDetail(), RegulatorTeamRole.EXPLORATION);
  }
}
