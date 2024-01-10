package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@Service
public class CorrectionService {

  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResponsibleTeamService responsibleTeamService;
  private final SubmissionCreateService submissionCreateService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionPersistenceService submissionPersistenceService;
  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @Autowired
  public CorrectionService(RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                           ResponsibleTeamService responsibleTeamService,
                           SubmissionCreateService submissionCreateService,
                           SubmissionDetailService submissionDetailService,
                           SubmissionPersistenceService submissionPersistenceService,
                           SubmissionDetailPersistenceService submissionDetailPersistenceService) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.responsibleTeamService = responsibleTeamService;
    this.submissionCreateService = submissionCreateService;
    this.submissionDetailService = submissionDetailService;
    this.submissionPersistenceService = submissionPersistenceService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
  }


  /** Can start a correction on a resource if there are no existing draft corrections for it.
   * @param resource the resource we want to start a correction on
   * @return true if a correction can be started
   */
  public boolean canStartCorrection(Resource resource, User user) {
    var regulatorManagedInformation = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResource(resource);

    if (regulatorManagedInformation.isEmpty()) {
      return false;
    }

    var responsibleTeam = regulatorManagedInformation.get().getResponsibleTeamType();
    if (Objects.isNull(responsibleTeam)) {
      return false;
    }

    if (!responsibleTeamService.isInResponsibleTeam(user, responsibleTeam)) {
      return false;
    }

    return !submissionDetailService.openCorrectionExists(Set.of(SubmissionDetailStatus.DRAFT), resource);
  }

  ModelAndView startCorrectionForTypeAndRedirect(CorrectionForm form, User user) {
    return switch (form.getCorrectionType()) {
      case HISTORIC_UPDATE -> createMissingUpdateAndRedirect(
          UUID.fromString(form.getSubmissionIdForMissingUpdate()),
          form.getMissingUpdateDay(),
          form.getMissingUpdateMonth(),
          form.getMissingUpdateYear(),
          user);
      case SUBMISSION_CORRECTION -> createSubmissionCorrectionAndRedirect(
          UUID.fromString(form.getSubmissionIdForCorrectingInformation()),
          user);
      case ACCEPTED_DATE_CHANGE -> updateApprovedDateAndRedirect(
          UUID.fromString(form.getSubmissionIdForChangingApprovedDate()),
          form.getApprovalDateChangeDay(),
          form.getApprovalDateChangeMonth(),
          form.getApprovalDateChangeYear(),
          user);
      case REMOVE_SUBMISSION -> null;
    };
  }

  private ModelAndView getCorrectionRedirectForType(CorrectionType correctionType, SubmissionDetail submissionDetail) {
    return switch (correctionType) {
      case HISTORIC_UPDATE, SUBMISSION_CORRECTION -> ReverseRouter.redirect(on(TaskListController.class)
          .getTaskList(submissionDetail.getId()));
      case ACCEPTED_DATE_CHANGE -> ReverseRouter.redirect(on(ViewResourceRecordController.class)
          .showSubmissionSummary(submissionDetail.getSubmission().getResource().getId(), submissionDetail.getId(), null));
      case REMOVE_SUBMISSION -> null;
    };
  }

  @Transactional
  public ModelAndView createSubmissionCorrectionAndRedirect(UUID submissionDetailId, User user) {
    var previousSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var correctionSubmissionDetail = submissionCreateService.createCorrectionSubmissionDetail(
        previousSubmissionDetail, user
    );
    correctionSubmissionDetail.setSubmittedByWuaId(user.wuaId().intValue());
    correctionSubmissionDetail.setSubmittedTimestamp(previousSubmissionDetail.getSubmittedTimestamp());
    correctionSubmissionDetail.setAcceptedByWuaId(user.wuaId().intValue());
    correctionSubmissionDetail.setAcceptedTimestamp(previousSubmissionDetail.getAcceptedTimestamp());
    setCorrectionDetails(correctionSubmissionDetail, user);
    submissionDetailPersistenceService.saveSubmissionDetail(correctionSubmissionDetail);
    return getCorrectionRedirectForType(CorrectionType.SUBMISSION_CORRECTION, correctionSubmissionDetail);
  }

  @Transactional
  public ModelAndView updateApprovedDateAndRedirect(UUID submissionDetailId,
                                                    String day,
                                                    String month,
                                                    String year,
                                                    User user) {
    var previousSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var correctionSubmissionDetail = submissionCreateService.createCorrectionSubmissionDetail(
        previousSubmissionDetail,
        user
    );

    correctionSubmissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);

    setApprovalDateAndUser(correctionSubmissionDetail, day, month, year, user);

    submissionDetailPersistenceService.saveSubmissionDetail(correctionSubmissionDetail);

    return getCorrectionRedirectForType(CorrectionType.ACCEPTED_DATE_CHANGE, correctionSubmissionDetail);
  }

  @Transactional
  public ModelAndView createMissingUpdateAndRedirect(UUID submissionDetailId,
                                                     String day,
                                                     String month,
                                                     String year,
                                                     User user) {
    var previousSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resource = previousSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);
    SubmissionType submissionType;
    if (previousSubmissionDetail.isProspectSubmission()) {
      submissionType = SubmissionType.PROSPECT_UPDATE;
    } else if (previousSubmissionDetail.isDiscoverySubmission()) {
      submissionType = SubmissionType.UPDATE_DISCOVERY;
    } else {
      throw new ActionNotAllowedException("Corrections can only be done for prospects and discoveries");
    }

    var newSubmissionDetail = submissionCreateService.createCorrectionSubmissionDetail(
        previousSubmissionDetail,
        user
    );
    newSubmissionDetail.setSubmission(submission);
    newSubmissionDetail.setSubmissionType(submissionType);
    newSubmissionDetail.setVersionNumber(SubmissionDetailService.FIRST_VERSION);

    setApprovalDateAndUser(newSubmissionDetail, day, month, year, user);

    submissionDetailPersistenceService.saveSubmissionDetail(newSubmissionDetail);

    return getCorrectionRedirectForType(CorrectionType.HISTORIC_UPDATE, newSubmissionDetail);

  }

  private void setApprovalDateAndUser(SubmissionDetail submissionDetail,
                                      String day,
                                      String month,
                                      String year,
                                      User user) {
    var approvedDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day))
        .atStartOfDay(ZoneId.systemDefault()).toInstant();
    submissionDetail.setSubmittedTimestamp(approvedDate);
    submissionDetail.setSubmittedByWuaId(user.wuaId().intValue());
    submissionDetail.setAcceptedTimestamp(approvedDate);
    submissionDetail.setAcceptedByWuaId(user.wuaId().intValue());
  }

  private void setCorrectionDetails(SubmissionDetail submissionDetail, User user) {
    submissionDetail.setCorrection(true);
    submissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    submissionDetail.setCorrectionTimestamp(Instant.now());
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
  }
}
