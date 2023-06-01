package uk.co.nstauthority.hydrocarbonmaturation.notify;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.notify.utils.RemiAbsoluteUrlUtil;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.LicencePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.LicenceReferenceComparator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Service
public class SubmissionEmailService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionEmailService.class);
  private static final String LICENCES = "LICENCES";
  private static final String LICENCES_WITHOUT_LEADING_COLON = "LICENCES_WITHOUT_LEADING_COLON";
  private static final String RESOURCE_NAME_WITH_QUAD_BLOCK = "RESOURCE_NAME_WITH_QUAD_BLOCK";
  private static final String SUBMISSION_TYPE = "SUBMISSION_TYPE";
  private static final String SUBMISSION_TYPE_CAPITALISED = "SUBMISSION_TYPE_CAPITALISED";
  private static final String SYSTEM_OF_RECORD_RESOURCE_URL = "SYSTEM_OF_RECORD_RESOURCE_URL";
  private static final String REVIEW_PAGE_URL = "REVIEW_PAGE_URL";
  private static final String RECIPIENT_NAME = "RECIPIENT_NAME";
  private static final String REGULATOR_ORGANISATION_NAME = "REGULATOR_ORGANISATION_NAME";
  private static final String SERVICE_NAME_MNEMONIC = "SERVICE_NAME_MNEMONIC";

  private final NotifyEmailService notifyEmailService;
  private final EnergyPortalUserService energyPortalUserService;
  private final SubmissionResourceService submissionResourceService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final LicencePersistenceService licencePersistenceService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final IndustryTeamService industryTeamService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final SubmissionDetailService submissionDetailService;
  private final ResponsibleTeamService responsibleTeamService;

  SubmissionEmailService(NotifyEmailService notifyEmailService,
                         EnergyPortalUserService energyPortalUserService,
                         SubmissionResourceService submissionResourceService,
                         GeneralDetailsPersistenceService generalDetailsPersistenceService,
                         LicencePersistenceService licencePersistenceService,
                         RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                         ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties,
                         ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                         IndustryTeamService industryTeamService,
                         OrganisationUnitQueryService organisationUnitQueryService,
                         SubmissionDetailService submissionDetailService,
                         ResponsibleTeamService responsibleTeamService) {
    this.notifyEmailService = notifyEmailService;
    this.energyPortalUserService = energyPortalUserService;
    this.submissionResourceService = submissionResourceService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.licencePersistenceService = licencePersistenceService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.responsibleTeamService = responsibleTeamService;
    this.serviceBrandingConfigurationProperties = serviceBrandingConfigurationProperties;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.industryTeamService = industryTeamService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.submissionDetailService = submissionDetailService;
  }

  public void sendSubmissionWithdrawalEmail(SubmissionDetail submissionDetail) {
    var energyPortalUserDtoOptional = energyPortalUserService.findByWuaId(
        WebUserAccountId.valueOf(submissionDetail.getSubmittedByWuaId().toString()));

    energyPortalUserDtoOptional.ifPresentOrElse(recipient -> {
          var emailProperties = new EmailProperties(NotifyTemplate.SUBMISSION_WITHDRAW_CONFIRMATION);
          var personalisations = emailProperties.getEmailPersonalisations();
          var licencesString = getDisplayableLicences(submissionDetail);
          personalisations.put(REGULATOR_ORGANISATION_NAME,
              serviceBrandingConfigurationProperties.getCustomerConfigurationProperties().mnemonic());
          addCommonSubmissionPersonalisations(personalisations, submissionDetail, licencesString);
          sendEmailToRecipient(emailProperties, recipient);
        },
        () ->
          LOGGER.warn("Could not find recipient with WUA_ID %s for submission withdrawal email"
              .formatted(submissionDetail.getSubmittedByWuaId().toString()))
    );
  }

  public void sendSubmissionApprovalEmail(SubmissionDetail submissionDetail) {
    var energyPortalUserDtoOptional = energyPortalUserService.findByWuaId(
        WebUserAccountId.valueOf(submissionDetail.getSubmittedByWuaId().toString()));

    energyPortalUserDtoOptional.ifPresentOrElse(recipient -> {
          var emailProperties = new EmailProperties(NotifyTemplate.SUBMISSION_APPROVAL_NOTIFICATION);
          var personalisations = emailProperties.getEmailPersonalisations();
          var licencesString = getDisplayableLicences(submissionDetail);
          addCommonSubmissionPersonalisations(personalisations, submissionDetail, licencesString);
          personalisations.put(SYSTEM_OF_RECORD_RESOURCE_URL,
              RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(submissionDetail));
          sendEmailToRecipient(emailProperties, recipient);
        },
        () -> LOGGER.warn("Could not find recipient with WUA_ID %s for submission approval email"
            .formatted(submissionDetail.getSubmittedByWuaId()))
    );
  }

  public void sendSubmittedSubmissionEmails(SubmissionDetail submissionDetail) {
    var responsibleTeam = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getSubmission().getResource());

    var responsibleTeamUsers = responsibleTeamService
        .getResponsibleTeamUsers(responsibleTeam.getResponsibleTeamType());

    if (responsibleTeamUsers.isEmpty()) {
      LOGGER.warn("Could not find responsible team email recipients for submission %s submitted notification email"
          .formatted(submissionDetail.getId()));
      return;
    }
    var emailTemplate = getSubmittedSubmissionConfirmationTemplate(submissionDetail);
    var emailProperties = new EmailProperties(emailTemplate);
    var personalisations = emailProperties.getEmailPersonalisations();
    var licencesString = getDisplayableLicences(submissionDetail);
    personalisations.put(LICENCES_WITHOUT_LEADING_COLON, licencesString);
    personalisations.put(REVIEW_PAGE_URL,
        RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail));
    addCommonSubmissionPersonalisations(personalisations, submissionDetail, licencesString);
    sendEmailToRecipients(emailProperties, responsibleTeamUsers);
  }

  public void sendReturnedForUpdateEmails(SubmissionDetail submissionDetail) {
    var recipients = getSubmitterRecipientsBySubmissionDetail(submissionDetail);
    if (recipients.isEmpty()) {
      LOGGER.warn("Could not send email, no submitters for the responsible organisation for submission {} found",
          submissionDetail.getId());
      return;
    }
    var emailProperties = new EmailProperties(NotifyTemplate.SUBMISSION_RETURNED_FOR_UPDATE);
    var personalisations = emailProperties.getEmailPersonalisations();
    var licencesString = getDisplayableLicences(submissionDetail);
    personalisations.put(LICENCES_WITHOUT_LEADING_COLON, licencesString);
    personalisations.put(REVIEW_PAGE_URL,
        RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail));
    personalisations.put(REGULATOR_ORGANISATION_NAME,
        serviceBrandingConfigurationProperties.getCustomerConfigurationProperties().mnemonic());
    addCommonSubmissionPersonalisations(personalisations, submissionDetail, licencesString);
    sendEmailToRecipients(emailProperties, recipients);
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
    personalisations.put(SERVICE_NAME_MNEMONIC,
        serviceBrandingConfigurationProperties.getServiceConfigurationProperties().mnemonic());
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

  private List<EnergyPortalUserDto> getSubmitterRecipientsBySubmissionDetail(SubmissionDetail submissionDetail) {
    var organisationUnitIdOpt = getResponsibleOrganisationId(submissionDetail);
    if (organisationUnitIdOpt.isEmpty()) {
      return List.of();
    }

    var organisationGroupIds = organisationUnitQueryService.findOrganisationGroupIdsByUnitId(
        organisationUnitIdOpt.get());
    if (organisationGroupIds.isEmpty()) {
      return List.of();
    }

    var submitterWuaIds = industryTeamService.getWuaIdsOfUsersWithRoleForOrganisationGroupIds(
            organisationGroupIds, Set.of(IndustryTeamRole.SUBMITTER))
        .stream()
        .map(id -> WebUserAccountId.valueOf(id.toString()))
        .toList();
    return energyPortalUserService.findByWuaIds(submitterWuaIds);
  }

  private Optional<Integer> getResponsibleOrganisationId(SubmissionDetail submissionDetail) {
    if (submissionDetail.getSubmissionType() == SubmissionType.CREATE_DISCOVERY
        || submissionDetail.getSubmissionType() == SubmissionType.CREATE_PROSPECT) {
      //if its the initial submission, takes the responsible organisation from the submitted submission
      return resourceDetailsPersistenceService
            .getResourceDetailBySubmissionDetail(submissionDetail)
            .map(ResourceDetail::getOrganisationId);
    } else {
      return submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail)
          .flatMap(latestApprovedSubmissionDetail ->
              resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestApprovedSubmissionDetail)
                  .map(ResourceDetail::getOrganisationId));
    }
  }

  private NotifyTemplate getSubmittedSubmissionConfirmationTemplate(SubmissionDetail submissionDetail) {
    return submissionDetail.isUpdateSubmission()
        ? NotifyTemplate.SUBMISSION_UPDATE_SUBMITTED_CONFIRMATION
        : NotifyTemplate.SUBMISSION_CONFIRMATION_NOTIFICATION;
  }
}
