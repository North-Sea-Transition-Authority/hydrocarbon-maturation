package uk.co.nstauthority.hydrocarbonmaturation.notify;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.utils.RemiEmailUrlUtil;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.LicencePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.LicenceReferenceComparator;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class SubmissionEmailService {

  private static final String LICENCES = "LICENCES";
  private static final String LICENCES_WITHOUT_LEADING_COLON = "LICENCES_WITHOUT_LEADING_COLON";
  private static final String RESOURCE_NAME_WITH_QUAD_BLOCK = "RESOURCE_NAME_WITH_QUAD_BLOCK";
  private static final String SUBMISSION_TYPE = "SUBMISSION_TYPE";
  private static final String SUBMISSION_TYPE_CAPITALISED = "SUBMISSION_TYPE_CAPITALISED";
  private static final String SYSTEM_OF_RECORD_RESOURCE_URL = "SYSTEM_OF_RECORD_RESOURCE_URL";
  private static final String REVIEW_PAGE_URL = "REVIEW_PAGE_URL";
  private static final String RECIPIENT_NAME = "RECIPIENT_NAME";

  private final NotifyEmailService notifyEmailService;
  private final EnergyPortalUserService energyPortalUserService;
  private final SubmissionResourceService submissionResourceService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final LicencePersistenceService licencePersistenceService;
  private final ResponsibleTeamPersistenceService responsibleTeamPersistenceService;
  private final RegulatorTeamService regulatorTeamService;

  SubmissionEmailService(NotifyEmailService notifyEmailService,
                         EnergyPortalUserService energyPortalUserService,
                         SubmissionResourceService submissionResourceService,
                         GeneralDetailsPersistenceService generalDetailsPersistenceService,
                         LicencePersistenceService licencePersistenceService,
                         ResponsibleTeamPersistenceService responsibleTeamPersistenceService,
                         RegulatorTeamService regulatorTeamService) {
    this.notifyEmailService = notifyEmailService;
    this.energyPortalUserService = energyPortalUserService;
    this.submissionResourceService = submissionResourceService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.licencePersistenceService = licencePersistenceService;
    this.responsibleTeamPersistenceService = responsibleTeamPersistenceService;
    this.regulatorTeamService = regulatorTeamService;
  }

  public void sendSubmissionApprovalEmail(SubmissionDetail submissionDetail) {
    var emailProperties = new EmailProperties(NotifyTemplate.SUBMISSION_APPROVAL_NOTIFICATION);
    var personalisations = emailProperties.getEmailPersonalisations();
    var licencesString = getDisplayableLicences(submissionDetail);
    addCommonSubmissionPersonalisations(personalisations, submissionDetail, licencesString);
    personalisations.put(SYSTEM_OF_RECORD_RESOURCE_URL,
        RemiEmailUrlUtil.getSystemOfRecordResourceUrl(submissionDetail));
    var recipient = energyPortalUserService.findByWuaId(
            WebUserAccountId.valueOf(submissionDetail.getSubmittedByWuaId().toString()))
        .orElseThrow(() -> new ActionNotAllowedException("Could not find a user with id %s"
            .formatted(submissionDetail.getSubmittedByWuaId())));

    sendEmailToRecipient(emailProperties, recipient);
  }

  public void sendSubmittedSubmissionEmails(SubmissionDetail submissionDetail) {
    var emailProperties = new EmailProperties(NotifyTemplate.SUBMISSION_CONFIRMATION_NOTIFICATION);
    var personalisations = emailProperties.getEmailPersonalisations();
    var responsibleTeam = responsibleTeamPersistenceService
        .getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId());

    if (responsibleTeam.getResponsibleTeamType() == ResponsibleTeamType.EXPLORATION) {
      var licencesString = getDisplayableLicences(submissionDetail);

      personalisations.put(LICENCES_WITHOUT_LEADING_COLON, licencesString);
      personalisations.put(REVIEW_PAGE_URL,
          RemiEmailUrlUtil.getReviewSubmissionPageUrl(submissionDetail));

      addCommonSubmissionPersonalisations(personalisations, submissionDetail, licencesString);
      sendEmailToRecipients(emailProperties, regulatorTeamService.getExplorationTeamEnergyPortalUsers());
    }
  }

  private void addCommonSubmissionPersonalisations(Map<String, String> personalisations,
                                                   SubmissionDetail submissionDetail,
                                                   String licencesString) {
    var submissionType = submissionDetail.getSubmissionType();

    if (!licencesString.isEmpty()) {
      personalisations.put(LICENCES, licencesString + ":");
    } else {
      personalisations.put(LICENCES, licencesString);
    }

    personalisations.put(RESOURCE_NAME_WITH_QUAD_BLOCK,
        submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail));
    personalisations.put(SUBMISSION_TYPE, submissionType.getTypeName().toLowerCase());
    personalisations.put(SUBMISSION_TYPE_CAPITALISED, submissionDetail.getSubmissionType().getTypeName());
  }

  private void sendEmailToRecipients(EmailProperties emailProperties, List<EnergyPortalUserDto> recipients) {
    recipients.forEach(recipient -> sendEmailToRecipient(emailProperties, recipient));
  }

  private void sendEmailToRecipient(EmailProperties emailProperties, EnergyPortalUserDto recipient) {
    emailProperties.getEmailPersonalisations().put(RECIPIENT_NAME, "%s %s"
        .formatted(recipient.forename(), recipient.surname()));
    notifyEmailService.sendEmail(emailProperties, recipient.emailAddress());
  }

  private String getDisplayableLicences(SubmissionDetail submissionDetail) {
    var licences = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(licencePersistenceService::getLicencesByGeneralDetail)
        .orElse(List.of())
        .stream()
        .sorted(new LicenceReferenceComparator())
        .map(Licence::getLicenceReference)
        .toList();
    if (licences.isEmpty()) {
      return "";
    } else {
      return String.join(", ", licences);
    }
  }
}
