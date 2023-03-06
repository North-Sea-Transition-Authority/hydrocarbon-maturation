package uk.co.nstauthority.hydrocarbonmaturation.notify;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.utils.RemiEmailUrlUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.LicencePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.LicenceReferenceComparator;

@Service
public class SubmissionEmailService {

  private static final String LICENCES = "LICENCES";
  private static final String RESOURCE_NAME_WITH_QUAD_BLOCK = "RESOURCE_NAME_WITH_QUAD_BLOCK";
  private static final String SUBMISSION_TYPE = "SUBMISSION_TYPE";
  private static final String SUBMISSION_TYPE_CAPITALISED = "SUBMISSION_TYPE_CAPITALISED";
  private static final String SYSTEM_OF_RECORD_RESOURCE_URL = "SYSTEM_OF_RECORD_RESOURCE_URL";
  private static final String RECIPIENT_NAME = "RECIPIENT_NAME";

  private final NotifyEmailService notifyEmailService;
  private final EnergyPortalUserService energyPortalUserService;
  private final SubmissionResourceService submissionResourceService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final LicencePersistenceService licencePersistenceService;

  SubmissionEmailService(NotifyEmailService notifyEmailService,
                         EnergyPortalUserService energyPortalUserService,
                         SubmissionResourceService submissionResourceService,
                         GeneralDetailsPersistenceService generalDetailsPersistenceService,
                         LicencePersistenceService licencePersistenceService) {
    this.notifyEmailService = notifyEmailService;
    this.energyPortalUserService = energyPortalUserService;
    this.submissionResourceService = submissionResourceService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.licencePersistenceService = licencePersistenceService;
  }

  public void sendSubmissionApprovalEmail(SubmissionDetail submissionDetail) {
    var submissionType = submissionDetail.getSubmissionType();
    var emailProperties = new EmailProperties(NotifyTemplate.SUBMISSION_APPROVAL_NOTIFICATION);
    var personalisations = emailProperties.getEmailPersonalisations();
    personalisations.put(RESOURCE_NAME_WITH_QUAD_BLOCK,
        submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail));
    var displayableLicences = getDisplayableLicences(submissionDetail);
    if (!displayableLicences.isBlank()) {
      displayableLicences = displayableLicences + ":";
    }
    personalisations.put(LICENCES, displayableLicences);
    personalisations.put(SUBMISSION_TYPE_CAPITALISED, submissionType.getTypeName());
    personalisations.put(SUBMISSION_TYPE, submissionType.getTypeName().toLowerCase());
    personalisations.put(SYSTEM_OF_RECORD_RESOURCE_URL, RemiEmailUrlUtil
        .getSystemOfRecordResourceUrl(submissionDetail));
    var recipient = energyPortalUserService.findByWuaId(
        WebUserAccountId.valueOf(String.valueOf(submissionDetail.getSubmittedByWuaId())))
        .orElseThrow(() -> new ActionNotAllowedException("Could not find a user with id %s"
            .formatted(submissionDetail.getSubmittedByWuaId())));

    sendEmailToRecipient(emailProperties, recipient);
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
