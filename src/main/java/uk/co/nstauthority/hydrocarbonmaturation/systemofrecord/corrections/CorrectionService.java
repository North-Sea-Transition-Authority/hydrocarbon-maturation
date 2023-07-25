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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailDuplicationService;
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
  private final SubmissionDetailDuplicationService submissionDetailDuplicationService;

  @Autowired
  public CorrectionService(RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                           ResponsibleTeamService responsibleTeamService,
                           SubmissionCreateService submissionCreateService,
                           SubmissionDetailService submissionDetailService,
                           SubmissionPersistenceService submissionPersistenceService,
                           SubmissionDetailPersistenceService submissionDetailPersistenceService,
                           SubmissionDetailDuplicationService submissionDetailDuplicationService) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.responsibleTeamService = responsibleTeamService;
    this.submissionCreateService = submissionCreateService;
    this.submissionDetailService = submissionDetailService;
    this.submissionPersistenceService = submissionPersistenceService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.submissionDetailDuplicationService = submissionDetailDuplicationService;
  }


  /** Can start a correction on a resource if there are no existing draft corrections for it.
   * @param resource the resource we want to start a correction on
   * @return true if a correction can be started
   */
  public boolean canStartCorrection(Resource resource, ServiceUserDetail user) {
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

  ModelAndView startCorrectionForTypeAndRedirect(CorrectionForm form, ServiceUserDetail user) {
    return switch (form.getCorrectionType()) {
      case HISTORIC_UPDATE -> createMissingUpdateAndRedirect(
          UUID.fromString(form.getSubmissionIdForMissingUpdate()),
          form.getMissingUpdateDay(),
          form.getMissingUpdateMonth(),
          form.getMissingUpdateYear(),
          user);
      case SUBMISSION_CORRECTION -> createCorrectionAndRedirect(
          UUID.fromString(form.getSubmissionIdForCorrectingInformation()),
          user);
      case APPROVAL_DATE_CHANGE -> updateApprovedDateAndRedirect(
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
      case APPROVAL_DATE_CHANGE -> ReverseRouter.redirect(on(ViewResourceRecordController.class)
          .showSubmissionSummary(submissionDetail.getSubmission().getResource().getId(), submissionDetail.getId(), null));
      case REMOVE_SUBMISSION -> null;
    };
  }

  @Transactional
  public ModelAndView createCorrectionAndRedirect(UUID submissionDetailId, ServiceUserDetail serviceUserDetail) {
    var previousSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var correctionSubmissionDetail = submissionCreateService.createUpdateSubmissionDetail(previousSubmissionDetail);
    correctionSubmissionDetail.setSubmittedByWuaId(serviceUserDetail.wuaId().intValue());
    correctionSubmissionDetail.setSubmittedTimestamp(previousSubmissionDetail.getSubmittedTimestamp());
    correctionSubmissionDetail.setApprovedByWuaId(serviceUserDetail.wuaId().intValue());
    correctionSubmissionDetail.setApprovedTimestamp(previousSubmissionDetail.getApprovedTimestamp());
    setCorrectionDetails(correctionSubmissionDetail, serviceUserDetail);
    submissionDetailPersistenceService.saveSubmissionDetail(correctionSubmissionDetail);
    return getCorrectionRedirectForType(CorrectionType.SUBMISSION_CORRECTION, correctionSubmissionDetail);
  }

  @Transactional
  public ModelAndView updateApprovedDateAndRedirect(UUID submissionDetailId,
                                                    String day,
                                                    String month,
                                                    String year,
                                                    ServiceUserDetail serviceUserDetail) {
    var previousSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var correctionSubmissionDetail = submissionCreateService.createUpdateSubmissionDetail(previousSubmissionDetail);

    correctionSubmissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    setApprovalDateAndUser(correctionSubmissionDetail, day, month, year, serviceUserDetail);

    setCorrectionDetails(correctionSubmissionDetail, serviceUserDetail);

    submissionDetailPersistenceService.saveSubmissionDetail(correctionSubmissionDetail);

    return getCorrectionRedirectForType(CorrectionType.APPROVAL_DATE_CHANGE, correctionSubmissionDetail);
  }

  @Transactional
  public ModelAndView createMissingUpdateAndRedirect(UUID submissionDetailId,
                                                     String day,
                                                     String month,
                                                     String year,
                                                     ServiceUserDetail serviceUserDetail) {
    var previousSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resource = previousSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);
    SubmissionDetail newSubmissionDetail;
    if (previousSubmissionDetail.isProspectSubmission()) {
      newSubmissionDetail =
          submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.PROSPECT_UPDATE);
    } else if (previousSubmissionDetail.isDiscoverySubmission()) {
      newSubmissionDetail =
          submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.UPDATE_DISCOVERY);
    } else {
      throw new ActionNotAllowedException("Corrections can only be done for prospects and discoveries");
    }

    submissionDetailDuplicationService.duplicateSubmissionDetailSections(previousSubmissionDetail, newSubmissionDetail);

    setApprovalDateAndUser(newSubmissionDetail, day, month, year, serviceUserDetail);

    setCorrectionDetails(newSubmissionDetail, serviceUserDetail);

    submissionDetailPersistenceService.saveSubmissionDetail(newSubmissionDetail);

    return getCorrectionRedirectForType(CorrectionType.HISTORIC_UPDATE, newSubmissionDetail);

  }

  private void setApprovalDateAndUser(SubmissionDetail submissionDetail,
                                      String day,
                                      String month,
                                      String year,
                                      ServiceUserDetail serviceUserDetail) {
    var approvedDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day))
        .atStartOfDay(ZoneId.systemDefault()).toInstant();
    submissionDetail.setSubmittedTimestamp(approvedDate);
    submissionDetail.setSubmittedByWuaId(serviceUserDetail.wuaId().intValue());
    submissionDetail.setApprovedTimestamp(approvedDate);
    submissionDetail.setApprovedByWuaId(serviceUserDetail.wuaId().intValue());
  }

  private void setCorrectionDetails(SubmissionDetail submissionDetail, ServiceUserDetail serviceUserDetail) {
    submissionDetail.setCorrection(true);
    submissionDetail.setCorrectionByWuaId(serviceUserDetail.wuaId().intValue());
    submissionDetail.setCorrectionTimestamp(Instant.now());
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
  }
}
