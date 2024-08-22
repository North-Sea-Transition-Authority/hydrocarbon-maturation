package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSubmissionStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Service
public class SubmissionApprovalService {

  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final SubmissionEmailService submissionEmailService;
  private final SubmissionUpdateService submissionUpdateService;
  private final ResponsibleTeamService responsibleTeamService;
  private final ReservoirTotalsCleanAndCopyService reservoirTotalsCleanAndCopyService;
  private final ReservoirSubmissionSectionService reservoirSubmissionSectionService;
  private final FieldSubmissionStatusService fieldSubmissionStatusService;
  private final InternalTeamService internalTeamService;

  @Autowired
  public SubmissionApprovalService(
      SubmissionDetailPersistenceService submissionDetailPersistenceService,
      SubmissionEmailService submissionEmailService,
      SubmissionUpdateService submissionUpdateService,
      ResponsibleTeamService responsibleTeamService,
      ReservoirTotalsCleanAndCopyService reservoirTotalsCleanAndCopyService,
      ReservoirSubmissionSectionService reservoirSubmissionSectionService,
      FieldSubmissionStatusService fieldSubmissionStatusService, InternalTeamService internalTeamService) {
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.submissionEmailService = submissionEmailService;
    this.submissionUpdateService = submissionUpdateService;
    this.responsibleTeamService = responsibleTeamService;
    this.reservoirTotalsCleanAndCopyService = reservoirTotalsCleanAndCopyService;
    this.reservoirSubmissionSectionService = reservoirSubmissionSectionService;
    this.fieldSubmissionStatusService = fieldSubmissionStatusService;
    this.internalTeamService = internalTeamService;
  }

  @Transactional
  public void submitSubmission(SubmissionDetail submissionDetail, User user) {
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
      submissionDetailPersistenceService.submitSubmission(submissionDetail, user);

      if (submissionDetail.isUpdateToSubmission()) {
        submissionUpdateService.resolveUpdateOnSubmission(submissionDetail, user);
      }

      if (canAutoApproveSubmission(user)) {
        submissionDetailPersistenceService.approveSubmission(submissionDetail, user);
        if (submissionDetail.isFieldSubmission()) {
          fieldSubmissionStatusService.updateFieldStatusForAcceptance(submissionDetail);
        }
      } else {
        submissionEmailService.sendSubmissionConfirmationEmails(submissionDetail);
        if (submissionDetail.isFieldSubmission()) {
          fieldSubmissionStatusService.updateFieldStatusForSubmission(submissionDetail);
        }
      }
    }

  }

  @Transactional
  public void approveSubmissionOrThrow(SubmissionDetail submissionDetail, User user) {
    if (canApproveSubmission(submissionDetail, user)) {
      submissionDetailPersistenceService.approveSubmission(submissionDetail, user);
      if (submissionDetail.isFieldSubmission()) {
        fieldSubmissionStatusService.updateFieldStatusForAcceptance(submissionDetail);
      }

      submissionEmailService.sendSubmissionApprovalEmail(submissionDetail);
    } else {
      throw new ActionNotAllowedException("Submission cannot be accepted by user with ID %s"
          .formatted(user.wuaId().intValue()));
    }
  }

  public boolean canApproveSubmission(SubmissionDetail submissionDetail, User user) {
    return responsibleTeamService
        .isInResponsibleTeam(user, submissionDetail)
        && !submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission());
  }

  private boolean canAutoApproveSubmission(User user) {
    return internalTeamService.isRegulatorOrAreaTeamUser(user);
  }

}
