package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Service
public class CorrectionService {

  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResponsibleTeamService responsibleTeamService;
  private final SubmissionCreateService submissionCreateService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionDetailRepository submissionDetailRepository;

  @Autowired
  public CorrectionService(RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                           ResponsibleTeamService responsibleTeamService,
                           SubmissionCreateService submissionCreateService,
                           SubmissionDetailService submissionDetailService,
                           SubmissionDetailRepository submissionDetailRepository) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.responsibleTeamService = responsibleTeamService;
    this.submissionCreateService = submissionCreateService;
    this.submissionDetailService = submissionDetailService;
    this.submissionDetailRepository = submissionDetailRepository;
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

  SubmissionDetail startCorrectionForType(CorrectionForm form, ServiceUserDetail user) {
    return switch (form.getCorrectionType()) {
      case HISTORIC_UPDATE -> null;
      case SUBMISSION_CORRECTION -> startCorrection(UUID.fromString(form.getSubmissionIdForCorrectingInformation()), user);
      case APPROVAL_DATE_CHANGE -> null;
      case REMOVE_SUBMISSION -> null;
    };
  }

  ModelAndView getCorrectionRedirectForType(CorrectionForm form, SubmissionDetail submissionDetail) {
    return switch (form.getCorrectionType()) {
      case HISTORIC_UPDATE -> null;
      case SUBMISSION_CORRECTION -> ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
      case APPROVAL_DATE_CHANGE -> null;
      case REMOVE_SUBMISSION -> null;
    };
  }

  @Transactional
  public SubmissionDetail startCorrection(UUID submissionDetailId, ServiceUserDetail serviceUserDetail) {
    var previousSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var correctionSubmissionDetail = submissionCreateService.createUpdateSubmissionDetail(previousSubmissionDetail);
    correctionSubmissionDetail.setCorrection(true);
    correctionSubmissionDetail.setCorrectionByWuaId(serviceUserDetail.wuaId().intValue());
    correctionSubmissionDetail.setCorrectionTimestamp(Instant.now());
    correctionSubmissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    submissionDetailRepository.save(correctionSubmissionDetail);
    return correctionSubmissionDetail;
  }
}
