package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class SubmissionApprovalService {

  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final RegulatorTeamService regulatorTeamService;
  private final UserDetailService userDetailService;

  public SubmissionApprovalService(SubmissionDetailPersistenceService submissionDetailPersistenceService,
                                   RegulatorTeamService regulatorTeamService, UserDetailService userDetailService) {
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.regulatorTeamService = regulatorTeamService;
    this.userDetailService = userDetailService;
  }

  @Transactional
  public void submitSubmission(SubmissionDetail submissionDetail) {
    submissionDetailPersistenceService.submitSubmission(submissionDetail);

    if (canAutoApproveSubmission()) {
      submissionDetailPersistenceService.approveSubmission(submissionDetail);
    }
  }

  private boolean canAutoApproveSubmission() {
    var userDetail = userDetailService.getUserDetail();
    return regulatorTeamService.isRegulatorUser(userDetail);
  }
}
