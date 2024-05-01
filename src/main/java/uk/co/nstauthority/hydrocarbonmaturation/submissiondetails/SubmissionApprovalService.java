package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSubmissionStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;

@Service
public class SubmissionApprovalService {

  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final RegulatorTeamService regulatorTeamService;
  private final UserService userService;
  private final SubmissionEmailService submissionEmailService;
  private final SubmissionUpdateService submissionUpdateService;
  private final ResponsibleTeamService responsibleTeamService;
  private final AreaTeamService areaTeamService;
  private final ReservoirTotalsCleanAndCopyService reservoirTotalsCleanAndCopyService;
  private final ReservoirSubmissionSectionService reservoirSubmissionSectionService;
  private final FieldSubmissionStatusService fieldSubmissionStatusService;

  @Autowired
  public SubmissionApprovalService(
      SubmissionDetailPersistenceService submissionDetailPersistenceService,
      RegulatorTeamService regulatorTeamService,
      UserService userService,
      SubmissionEmailService submissionEmailService,
      SubmissionUpdateService submissionUpdateService,
      ResponsibleTeamService responsibleTeamService,
      AreaTeamService areaTeamService, ReservoirTotalsCleanAndCopyService reservoirTotalsCleanAndCopyService,
      ReservoirSubmissionSectionService reservoirSubmissionSectionService,
      FieldSubmissionStatusService fieldSubmissionStatusService) {
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.regulatorTeamService = regulatorTeamService;
    this.userService = userService;
    this.submissionEmailService = submissionEmailService;
    this.submissionUpdateService = submissionUpdateService;
    this.responsibleTeamService = responsibleTeamService;
    this.areaTeamService = areaTeamService;
    this.reservoirTotalsCleanAndCopyService = reservoirTotalsCleanAndCopyService;
    this.reservoirSubmissionSectionService = reservoirSubmissionSectionService;
    this.fieldSubmissionStatusService = fieldSubmissionStatusService;
  }

  @Transactional
  public void submitSubmission(SubmissionDetail submissionDetail) {
    reservoirTotalsCleanAndCopyService.cleanAndCopyVolumetricTotals(submissionDetail);

    // remove any saved data for reservoir sections not needed, selected in reservoir under future phase of work question
    if (submissionDetail.isFieldSubmission()) {
      reservoirSubmissionSectionService.cleanReservoirSubmissionSections(submissionDetail);
    }

    // corrections will already have submission and approval details stored, just need to set the status
    if (BooleanUtils.isTrue(submissionDetail.isCorrection())) {
      submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
      submissionDetailPersistenceService.saveSubmissionDetail(submissionDetail);
    } else {
      submissionDetailPersistenceService.submitSubmission(submissionDetail);

      if (submissionDetail.isUpdateToSubmission()) {
        submissionUpdateService.resolveUpdateOnSubmission(submissionDetail);
      }

      if (canAutoApproveSubmission()) {
        submissionDetailPersistenceService.approveSubmission(submissionDetail);
        if (submissionDetail.isFieldSubmission()) {
          fieldSubmissionStatusService.updateFieldStatusForAcceptance(submissionDetail);
        }
      } else {
        submissionEmailService.sendSubmittedSubmissionEmails(submissionDetail);
        if (submissionDetail.isFieldSubmission()) {
          fieldSubmissionStatusService.updateFieldStatusForSubmission(submissionDetail);
        }
      }
    }

  }

  @Transactional
  public void approveSubmissionOrThrow(SubmissionDetail submissionDetail) {
    if (canApproveSubmission(submissionDetail)) {
      submissionDetailPersistenceService.approveSubmission(submissionDetail);
      if (submissionDetail.isFieldSubmission()) {
        fieldSubmissionStatusService.updateFieldStatusForAcceptance(submissionDetail);
      }

      submissionEmailService.sendSubmissionApprovalEmail(submissionDetail);
    } else {
      throw new ActionNotAllowedException("Submission cannot be accepted by user with ID %s"
          .formatted(userService.getUserWuaId()));
    }
  }

  public boolean canApproveSubmission(SubmissionDetail submissionDetail) {
    return responsibleTeamService
        .isInResponsibleTeam(userService.getUser(), submissionDetail)
        && !submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission());
  }

  private boolean canAutoApproveSubmission() {
    var user = userService.getUser();
    return regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user);
  }

}
