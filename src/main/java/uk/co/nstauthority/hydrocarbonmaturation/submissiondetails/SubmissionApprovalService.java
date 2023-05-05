package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class SubmissionApprovalService {

  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final RegulatorTeamService regulatorTeamService;
  private final UserDetailService userDetailService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final SubmissionEmailService submissionEmailService;
  private final SubmissionUpdateService submissionUpdateService;

  public SubmissionApprovalService(SubmissionDetailPersistenceService submissionDetailPersistenceService,
                                   RegulatorTeamService regulatorTeamService,
                                   UserDetailService userDetailService,
                                   RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                                   SubmissionEmailService submissionEmailService,
                                   SubmissionUpdateService submissionUpdateService) {
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.regulatorTeamService = regulatorTeamService;
    this.userDetailService = userDetailService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.submissionEmailService = submissionEmailService;
    this.submissionUpdateService = submissionUpdateService;
  }

  @Transactional
  public void submitSubmission(SubmissionDetail submissionDetail) {
    submissionDetailPersistenceService.submitSubmission(submissionDetail);

    if (canAutoApproveSubmission()) {
      submissionDetailPersistenceService.approveSubmission(submissionDetail);
    } else {
      submissionEmailService.sendSubmittedSubmissionEmails(submissionDetail);
    }
  }

  @Transactional
  public void approveSubmissionOrThrow(SubmissionDetail submissionDetail) {
    if (canApproveSubmission(submissionDetail)) {
      submissionDetailPersistenceService.approveSubmission(submissionDetail);

      submissionEmailService.sendSubmissionApprovalEmail(submissionDetail);
    } else {
      throw new ActionNotAllowedException("Submission cannot be approved by user with ID %s"
          .formatted(userDetailService.getUserWuaId()));
    }
  }

  public boolean canApproveSubmission(SubmissionDetail submissionDetail) {
    var responsibleTeam = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getSubmission().getResource());
    return responsibleTeam.getResponsibleTeamType() == ResponsibleTeamType.EXPLORATION
        && regulatorTeamService.isRegulatorTeamRole(
            userDetailService.getUserDetail(), RegulatorTeamRole.EXPLORATION)
        && !submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission());
  }

  private boolean canAutoApproveSubmission() {
    var userDetail = userDetailService.getUserDetail();
    return regulatorTeamService.isRegulatorUser(userDetail);
  }
}
