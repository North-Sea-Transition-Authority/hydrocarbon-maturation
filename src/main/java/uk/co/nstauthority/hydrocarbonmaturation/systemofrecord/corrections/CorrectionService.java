package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
public class CorrectionService {

  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResponsibleTeamService responsibleTeamService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceLinkService resourceLinkService;
  private final CorrectionCreateService correctionCreateService;

  @Autowired
  public CorrectionService(RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                           ResponsibleTeamService responsibleTeamService,
                           SubmissionDetailService submissionDetailService,
                           ResourceLinkService resourceLinkService, CorrectionCreateService correctionCreateService) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.responsibleTeamService = responsibleTeamService;
    this.submissionDetailService = submissionDetailService;
    this.resourceLinkService = resourceLinkService;
    this.correctionCreateService = correctionCreateService;
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

    if (resource.getType().equals(ResourceType.FIELD)) {
      correctionTypes.remove(CorrectionType.ACCEPTED_DATE_CHANGE.name());
      correctionTypes.remove(CorrectionType.HISTORIC_UPDATE.name());
    } else {
      correctionTypes.remove(CorrectionType.FIELD_HISTORIC_UPDATE.name());
    }

    return correctionTypes;
  }

  ModelAndView startCorrectionForTypeAndRedirect(CorrectionForm form, User user, Resource resource) {
    return switch (form.getCorrectionType()) {
      case HISTORIC_UPDATE -> correctionCreateService.createMissingUpdateAndRedirect(
          UUID.fromString(form.getSubmissionDetailIdForMissingUpdate()),
          form.getMissingUpdateDay(),
          form.getMissingUpdateMonth(),
          form.getMissingUpdateYear(),
          user);
      case FIELD_HISTORIC_UPDATE -> correctionCreateService.createMissingFieldUpdateAndRedirect(
          resource,
          form.getSubmissionTypeForMissingUpdate(),
          form.getMissingUpdateDay(),
          form.getMissingUpdateMonth(),
          form.getMissingUpdateYear(),
          user);
      case SUBMISSION_CORRECTION -> correctionCreateService.createSubmissionCorrectionAndRedirect(
          UUID.fromString(form.getSubmissionDetailIdForCorrectingInformation()),
          user);
      case ACCEPTED_DATE_CHANGE -> correctionCreateService.updateApprovedDateAndRedirect(
          UUID.fromString(form.getSubmissionDetailIdForChangingApprovedDate()),
          form.getApprovalDateChangeDay(),
          form.getApprovalDateChangeMonth(),
          form.getApprovalDateChangeYear(),
          user);
      case REMOVE_SUBMISSION -> correctionCreateService.removeSubmissionAndRedirect(
          UUID.fromString(form.getSubmissionDetailIdForRemoval())
      );
    };
  }

  /** Submissions can only be removed from resources if:
   *  if it's a prospect it's not currently linked to a discovery.
   * @param resource the resource from which submissions are being removed
   * @return true or false
   */
  boolean canRemoveSubmissions(Resource resource) {
    return !resourceLinkService.prospectIsLinkedToDiscovery(resource);
  }
}
