package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
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
  private final ResourceGroupService resourceGroupService;
  private final ResourceLinkService resourceLinkService;
  private final OrganisationCorrectionService organisationCorrectionService;
  private final Clock clock;
  private final PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;

  @Autowired
  public CorrectionService(RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                           ResponsibleTeamService responsibleTeamService,
                           SubmissionCreateService submissionCreateService,
                           SubmissionDetailService submissionDetailService,
                           SubmissionPersistenceService submissionPersistenceService,
                           SubmissionDetailPersistenceService submissionDetailPersistenceService,
                           ResourceGroupService resourceGroupService,
                           ResourceLinkService resourceLinkService,
                           OrganisationCorrectionService organisationCorrectionService,
                           Clock clock,
                           PostFieldDiscoveryLinkService postFieldDiscoveryLinkService) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.responsibleTeamService = responsibleTeamService;
    this.submissionCreateService = submissionCreateService;
    this.submissionDetailService = submissionDetailService;
    this.submissionPersistenceService = submissionPersistenceService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.resourceGroupService = resourceGroupService;
    this.resourceLinkService = resourceLinkService;
    this.organisationCorrectionService = organisationCorrectionService;
    this.clock = clock;
    this.postFieldDiscoveryLinkService = postFieldDiscoveryLinkService;
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

  Map<String, String> getCorrectionTypes(Resource resource) {
    var correctionTypes = CorrectionType.getCorrectionTypes();
    if (!canRemoveSubmissions(resource)) {
      correctionTypes.remove(CorrectionType.REMOVE_SUBMISSION.name());
    }
    return correctionTypes;
  }

  ModelAndView startCorrectionForTypeAndRedirect(CorrectionForm form, User user) {
    return switch (form.getCorrectionType()) {
      case HISTORIC_UPDATE -> createMissingUpdateAndRedirect(
          UUID.fromString(form.getSubmissionDetailIdForMissingUpdate()),
          form.getMissingUpdateDay(),
          form.getMissingUpdateMonth(),
          form.getMissingUpdateYear(),
          user);
      case SUBMISSION_CORRECTION -> createSubmissionCorrectionAndRedirect(
          UUID.fromString(form.getSubmissionDetailIdForCorrectingInformation()),
          user);
      case ACCEPTED_DATE_CHANGE -> updateApprovedDateAndRedirect(
          UUID.fromString(form.getSubmissionDetailIdForChangingApprovedDate()),
          form.getApprovalDateChangeDay(),
          form.getApprovalDateChangeMonth(),
          form.getApprovalDateChangeYear(),
          user);
      case REMOVE_SUBMISSION -> removeSubmissionAndRedirect(
          UUID.fromString(form.getSubmissionDetailIdForRemoval())
      );
    };
  }

  private ModelAndView getCorrectionRedirectForType(CorrectionType correctionType, SubmissionDetail submissionDetail) {
    return switch (correctionType) {
      case HISTORIC_UPDATE, SUBMISSION_CORRECTION -> ReverseRouter.redirect(on(TaskListController.class)
          .getTaskList(submissionDetail.getId(), null));
      case ACCEPTED_DATE_CHANGE -> ReverseRouter.redirect(on(ViewResourceRecordController.class)
          .showSubmissionSummary(submissionDetail.getSubmission().getResource().getId(), submissionDetail.getId(), null, null));
      case REMOVE_SUBMISSION -> ReverseRouter.redirect(on(DeleteSubmissionFromCorrectionController.class)
          .showDeleteConfirmationPage(submissionDetail.getId(), null));
    };
  }

  @Transactional
  public ModelAndView createSubmissionCorrectionAndRedirect(UUID submissionDetailId, User user) {
    var previousSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var correctionSubmissionDetail = submissionCreateService.createCorrectionSubmissionDetail(
        previousSubmissionDetail, user, CorrectionType.SUBMISSION_CORRECTION
    );
    correctionSubmissionDetail.setSubmittedByWuaId(user.wuaId().intValue());
    correctionSubmissionDetail.setSubmittedTimestamp(previousSubmissionDetail.getSubmittedTimestamp());
    correctionSubmissionDetail.setAcceptedByWuaId(user.wuaId().intValue());
    correctionSubmissionDetail.setAcceptedTimestamp(previousSubmissionDetail.getAcceptedTimestamp());
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
        user,
        CorrectionType.ACCEPTED_DATE_CHANGE
    );

    correctionSubmissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);

    var acceptedDate = getLocalDateFromForm(day, month, year);

    organisationCorrectionService.updateOrganisationNameForDate(correctionSubmissionDetail, acceptedDate);

    var acceptedInstant = acceptedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

    setAcceptedDateAndUser(correctionSubmissionDetail, acceptedInstant, user);

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
        user,
        CorrectionType.HISTORIC_UPDATE
    );
    newSubmissionDetail.setSubmission(submission);
    newSubmissionDetail.setSubmissionType(submissionType);
    newSubmissionDetail.setVersionNumber(SubmissionDetailService.FIRST_VERSION);

    var acceptedDate = getLocalDateFromForm(day, month, year);

    organisationCorrectionService.updateOrganisationNameForDate(newSubmissionDetail, acceptedDate);

    var acceptedInstant = acceptedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

    setAcceptedDateAndUser(newSubmissionDetail, acceptedInstant, user);

    submissionDetailPersistenceService.saveSubmissionDetail(newSubmissionDetail);

    return getCorrectionRedirectForType(CorrectionType.HISTORIC_UPDATE, newSubmissionDetail);

  }

  /** Submissions can only be removed from resources if:
   *  it's not the preferred view for a group
   *  and if it's a prospect it's not currently linked to a discovery.
   * @param resource the resource from which submissions are being removed
   * @return true or false
   */
  boolean canRemoveSubmissions(Resource resource) {
    var isPreferredView = resourceGroupService.isPreferredView(resource);
    var prospectHasLinkedDiscovery = resourceLinkService.prospectIsLinkedToDiscovery(resource);
    return !isPreferredView && !prospectHasLinkedDiscovery;
  }

  @Transactional
  public void deleteSubmission(SubmissionDetail submissionDetail, User user) {
    var submission = submissionDetail.getSubmission();
    var resource = submission.getResource();
    submissionDetailService.getSubmissionDetailsBySubmission(submission).forEach(submissionDetailForDelete -> {
      setCorrectionDetails(submissionDetailForDelete, user, CorrectionType.REMOVE_SUBMISSION);
      submissionDetailForDelete.setStatus(SubmissionDetailStatus.DELETED);
    });
    if (submissionDetail.getSubmissionType() == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT) {
      resourceLinkService.deleteDiscoveryToProspectLink(resource);
    }
    if (submissionDetail.isInitialResourceSubmission()) {
      resourceGroupService.removeResourceFromGroup(resource);
      postFieldDiscoveryLinkService.deletePostFieldDiscoveryLinkIfExists(resource);
    }
  }

  private ModelAndView removeSubmissionAndRedirect(UUID submissionDetailId) {
    var removedSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getCorrectionRedirectForType(CorrectionType.REMOVE_SUBMISSION, removedSubmissionDetail);
  }

  private void setAcceptedDateAndUser(SubmissionDetail submissionDetail,
                                      Instant approvedDate,
                                      User user) {
    submissionDetail.setSubmittedTimestamp(approvedDate);
    submissionDetail.setSubmittedByWuaId(user.wuaId().intValue());
    submissionDetail.setAcceptedTimestamp(approvedDate);
    submissionDetail.setAcceptedByWuaId(user.wuaId().intValue());
  }

  private void setCorrectionDetails(SubmissionDetail submissionDetail, User user, CorrectionType correctionType) {
    submissionDetail.setCorrection(true);
    submissionDetail.setCorrectionType(correctionType);
    submissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    submissionDetail.setCorrectionTimestamp(clock.instant());
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
  }

  private LocalDate getLocalDateFromForm(String day, String month, String year) {
    return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
  }
}
