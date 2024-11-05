package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@Service
public class CorrectionCreateService {

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionCreateService submissionCreateService;
  private final SubmissionPersistenceService submissionPersistenceService;
  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final OrganisationCorrectionService organisationCorrectionService;
  private final PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;
  private final ResourceLinkService resourceLinkService;
  private final ResourceGroupService resourceGroupService;
  private final Clock clock;

  public CorrectionCreateService(SubmissionDetailService submissionDetailService,
                                 SubmissionCreateService submissionCreateService,
                                 SubmissionPersistenceService submissionPersistenceService,
                                 SubmissionDetailPersistenceService submissionDetailPersistenceService,
                                 OrganisationCorrectionService organisationCorrectionService,
                                 PostFieldDiscoveryLinkService postFieldDiscoveryLinkService,
                                 ResourceLinkService resourceLinkService,
                                 ResourceGroupService resourceGroupService,
                                 Clock clock) {
    this.submissionDetailService = submissionDetailService;
    this.submissionCreateService = submissionCreateService;
    this.submissionPersistenceService = submissionPersistenceService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.organisationCorrectionService = organisationCorrectionService;
    this.postFieldDiscoveryLinkService = postFieldDiscoveryLinkService;
    this.resourceLinkService = resourceLinkService;
    this.resourceGroupService = resourceGroupService;
    this.clock = clock;
  }

  @Transactional
  public ModelAndView createSubmissionCorrectionAndRedirect(UUID submissionDetailId, User user) {
    var previousSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var correctionSubmissionDetail = submissionCreateService.createCorrectionSubmissionDetail(
        previousSubmissionDetail,
        user,
        CorrectionType.SUBMISSION_CORRECTION
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
    } else {
      submissionType = SubmissionType.UPDATE_DISCOVERY;
    }

    var newSubmissionDetail = submissionCreateService.createCorrectionSubmissionDetail(
        previousSubmissionDetail,
        user,
        CorrectionType.HISTORIC_UPDATE
    );

    return createCorrectionSubmissionDetailAndRedirect(newSubmissionDetail, submissionType, submission,
        getLocalDateFromForm(day, month, year), user);
  }

  @Transactional
  public ModelAndView createMissingFieldUpdateAndRedirect(Resource resource,
                                                          SubmissionType submissionType,
                                                          String day,
                                                          String month,
                                                          String year,
                                                          User user) {
    var historicDate = getLocalDateFromForm(day, month, year);
    var historicDateInstant = historicDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

    var previousSubmissionDetail = submissionDetailService.getExtantSubmissionDetailsForResource(resource).stream()
        .filter(submissionDetail -> submissionDetail.getAcceptedTimestamp().isBefore(historicDateInstant))
        .max(Comparator.comparing(SubmissionDetail::getAcceptedTimestamp));

    var submission = submissionPersistenceService.createSubmission(resource);

    SubmissionDetail newSubmissionDetail;

    if (previousSubmissionDetail.isPresent()) {
      newSubmissionDetail = submissionCreateService.createCorrectionSubmissionDetail(
          previousSubmissionDetail.get(),
          user,
          CorrectionType.HISTORIC_UPDATE
      );
    } else {
      newSubmissionDetail = submissionDetailPersistenceService.createInitialSubmissionDetail(
          submission,
          submissionType,
          user
      );

      setCorrectionDetails(newSubmissionDetail, user, CorrectionType.HISTORIC_UPDATE);
    }

    return createCorrectionSubmissionDetailAndRedirect(newSubmissionDetail, submissionType, submission, historicDate, user);
  }

  private ModelAndView createCorrectionSubmissionDetailAndRedirect(SubmissionDetail newSubmissionDetail,
                                                                  SubmissionType submissionType,
                                                                  Submission submission,
                                                                  LocalDate acceptedDate,
                                                                  User user) {
    newSubmissionDetail.setSubmission(submission);
    newSubmissionDetail.setSubmissionType(submissionType);
    newSubmissionDetail.setVersionNumber(SubmissionDetailService.FIRST_VERSION);

    organisationCorrectionService.updateOrganisationNameForDate(newSubmissionDetail, acceptedDate);

    var acceptedInstant = acceptedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

    setAcceptedDateAndUser(newSubmissionDetail, acceptedInstant, user);

    submissionDetailPersistenceService.saveSubmissionDetail(newSubmissionDetail);

    return getCorrectionRedirectForType(CorrectionType.HISTORIC_UPDATE, newSubmissionDetail);
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

  private ModelAndView getCorrectionRedirectForType(CorrectionType correctionType, SubmissionDetail submissionDetail) {
    return switch (correctionType) {
      case HISTORIC_UPDATE, SUBMISSION_CORRECTION, FIELD_HISTORIC_UPDATE -> ReverseRouter.redirect(on(TaskListController.class)
          .getTaskList(submissionDetail.getId(), null));
      case ACCEPTED_DATE_CHANGE -> ReverseRouter.redirect(on(ViewResourceRecordController.class)
          .showSubmissionSummary(submissionDetail.getSubmission().getResource().getId(), submissionDetail.getId(), null, null));
      case REMOVE_SUBMISSION -> ReverseRouter.redirect(on(DeleteSubmissionFromCorrectionController.class)
          .showDeleteConfirmationPage(submissionDetail.getId(), null));
    };
  }

  ModelAndView removeSubmissionAndRedirect(UUID submissionDetailId) {
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
