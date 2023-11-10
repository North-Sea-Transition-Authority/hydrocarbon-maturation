package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import javax.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class SubmissionApprovalService {

  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final RegulatorTeamService regulatorTeamService;
  private final UserDetailService userDetailService;
  private final SubmissionEmailService submissionEmailService;
  private final SubmissionUpdateService submissionUpdateService;
  private final ResponsibleTeamService responsibleTeamService;
  private final AreaTeamService areaTeamService;
  private final ReservoirTotalsCleanAndCopyService reservoirTotalsCleanAndCopyService;

  @Autowired
  public SubmissionApprovalService(
      SubmissionDetailPersistenceService submissionDetailPersistenceService,
      RegulatorTeamService regulatorTeamService,
      UserDetailService userDetailService,
      SubmissionEmailService submissionEmailService,
      SubmissionUpdateService submissionUpdateService,
      ResponsibleTeamService responsibleTeamService,
      AreaTeamService areaTeamService, ReservoirTotalsCleanAndCopyService reservoirTotalsCleanAndCopyService) {
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.regulatorTeamService = regulatorTeamService;
    this.userDetailService = userDetailService;
    this.submissionEmailService = submissionEmailService;
    this.submissionUpdateService = submissionUpdateService;
    this.responsibleTeamService = responsibleTeamService;
    this.areaTeamService = areaTeamService;
    this.reservoirTotalsCleanAndCopyService = reservoirTotalsCleanAndCopyService;
  }

  @Transactional
  public void submitSubmission(SubmissionDetail submissionDetail) {
    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);

    // corrections will already have submission and approval details stored, just need to set the status
    if (BooleanUtils.isTrue(submissionDetail.isCorrection())) {
      submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
      submissionDetailPersistenceService.saveSubmissionDetail(submissionDetail);
    } else {
      submissionDetailPersistenceService.submitSubmission(submissionDetail);

      if (submissionDetail.isUpdateSubmission()) {
        submissionUpdateService.resolveUpdateOnSubmission(submissionDetail);
      }

      if (canAutoApproveSubmission()) {
        submissionDetailPersistenceService.approveSubmission(submissionDetail);
      } else {
        submissionEmailService.sendSubmittedSubmissionEmails(submissionDetail);
      }
    }

  }

  @Transactional
  public void approveSubmissionOrThrow(SubmissionDetail submissionDetail) {
    if (canApproveSubmission(submissionDetail)) {
      submissionDetailPersistenceService.approveSubmission(submissionDetail);

      submissionEmailService.sendSubmissionApprovalEmail(submissionDetail);
    } else {
      throw new ActionNotAllowedException("Submission cannot be accepted by user with ID %s"
          .formatted(userDetailService.getUserWuaId()));
    }
  }

  public boolean canApproveSubmission(SubmissionDetail submissionDetail) {
    return responsibleTeamService
        .isInResponsibleTeam(userDetailService.getUserDetail(), submissionDetail)
        && !submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission());
  }

  private boolean canAutoApproveSubmission() {
    var userDetail = userDetailService.getUserDetail();
    return regulatorTeamService.isRegulatorUser(userDetail) || areaTeamService.isAreaUser(userDetail);
  }

}
