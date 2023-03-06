package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class SubmissionApprovalService {

  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final RegulatorTeamService regulatorTeamService;
  private final UserDetailService userDetailService;
  private final ResponsibleTeamPersistenceService responsibleTeamPersistenceService;
  private final SubmissionEmailService submissionEmailService;

  public SubmissionApprovalService(SubmissionDetailPersistenceService submissionDetailPersistenceService,
                                   RegulatorTeamService regulatorTeamService,
                                   UserDetailService userDetailService,
                                   ResponsibleTeamPersistenceService responsibleTeamPersistenceService,
                                   SubmissionEmailService submissionEmailService) {
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.regulatorTeamService = regulatorTeamService;
    this.userDetailService = userDetailService;
    this.responsibleTeamPersistenceService = responsibleTeamPersistenceService;
    this.submissionEmailService = submissionEmailService;
  }

  @Transactional
  public void submitSubmission(SubmissionDetail submissionDetail) {
    submissionDetailPersistenceService.submitSubmission(submissionDetail);

    if (canAutoApproveSubmission()) {
      submissionDetailPersistenceService.approveSubmission(submissionDetail);
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
    var responsibleTeam = responsibleTeamPersistenceService
        .getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId());
    return responsibleTeam.getResponsibleTeamType() == ResponsibleTeamType.EXPLORATION
        && regulatorTeamService.isRegulatorTeamRole(
            userDetailService.getUserDetail(), RegulatorTeamRole.EXPLORATION);
  }

  private boolean canAutoApproveSubmission() {
    var userDetail = userDetailService.getUserDetail();
    return regulatorTeamService.isRegulatorUser(userDetail);
  }
}
