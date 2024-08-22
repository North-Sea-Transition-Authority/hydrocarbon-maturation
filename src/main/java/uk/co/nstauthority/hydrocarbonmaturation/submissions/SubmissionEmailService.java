package uk.co.nstauthority.hydrocarbonmaturation.submissions;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.digitalnotificationlibrary.core.notification.MergedTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.email.EmailService;
import uk.co.nstauthority.hydrocarbonmaturation.email.NotifyTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.notify.RemiAbsoluteUrlUtil;
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
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@Service
public class SubmissionEmailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionEmailService.class);

  static final String RESOURCE_NAME_QUAD_BLOCK_KEY = "RESOURCE_NAME_WITH_QUAD_BLOCK";
  static final String LICENCES_KEY = "LICENCES";
  static final String HAS_LICENCES_KEY = "HAS_LICENCES";
  static final String REVIEW_URL_KEY = "REVIEW_PAGE_URL";
  static final String SYSTEM_OF_RECORD_KEY = "SYSTEM_OF_RECORD_RESOURCE_URL";
  static final String SUBMISSION_TYPE_WITH_ACRONYM_KEY = "SUBMISSION_TYPE_WITH_ACRONYM";
  static final String RECIPIENT_KEY = "RECIPIENT_NAME";
  static final String REGULATOR_ORGANISATION_NAME_KEY = "REGULATOR_ORGANISATION_NAME";

  private final EmailService emailService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResponsibleTeamService responsibleTeamService;
  private final SubmissionResourceService submissionResourceService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final LicencePersistenceService licencePersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final EnergyPortalUserService energyPortalUserService;
  private final ServiceBrandingConfigurationProperties brandingConfigurationProperties;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final IndustryTeamService industryTeamService;

  @Autowired
  public SubmissionEmailService(EmailService emailService,
                                RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                                ResponsibleTeamService responsibleTeamService,
                                SubmissionResourceService submissionResourceService,
                                GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                LicencePersistenceService licencePersistenceService,
                                SubmissionDetailService submissionDetailService,
                                EnergyPortalUserService energyPortalUserService,
                                ServiceBrandingConfigurationProperties brandingConfigurationProperties,
                                ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                OrganisationUnitQueryService organisationUnitQueryService,
                                IndustryTeamService industryTeamService) {
    this.emailService = emailService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.responsibleTeamService = responsibleTeamService;
    this.submissionResourceService = submissionResourceService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.licencePersistenceService = licencePersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.energyPortalUserService = energyPortalUserService;
    this.brandingConfigurationProperties = brandingConfigurationProperties;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.industryTeamService = industryTeamService;
  }

  public void sendSubmissionConfirmationEmails(SubmissionDetail submissionDetail) {
    var resource = submissionDetail.getSubmission().getResource();
    var regulatorManagedInformation = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(resource);

    var responsibleTeamUsers = responsibleTeamService
        .getResponsibleTeamUsers(regulatorManagedInformation.getResponsibleTeamType());

    if (responsibleTeamUsers.isEmpty()) {
      LOGGER.warn("Couldn't find responsible team email recipients for resource {}", resource.getId());
      return;
    }

    var emailTemplate = getSubmissionConfirmationEmailTemplate(submissionDetail);
    var mergedTemplateBuilder = getTemplateWithCommonFields(emailTemplate, submissionDetail)
        .withMailMergeField(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail));

    sendEmailToAllRecipients(mergedTemplateBuilder, responsibleTeamUsers, submissionDetail);
  }

  public void sendSubmissionApprovalEmail(SubmissionDetail submissionDetail) {
    var energyPortalUserDtoOptional = getUserFromSubmissionDetail(submissionDetail);

    if (energyPortalUserDtoOptional.isEmpty()) {
      LOGGER.warn("Could not find recipient with WUA_ID {} for submission acceptance email",
          submissionDetail.getSubmittedByWuaId());
      return;
    }

    var energyPortalUser = energyPortalUserDtoOptional.get();
    var templateBuilder = getTemplateWithCommonFields(NotifyTemplate.SUBMISSION_APPROVAL_NOTIFICATION, submissionDetail)
        .withMailMergeField(SYSTEM_OF_RECORD_KEY, RemiAbsoluteUrlUtil.getSystemOfRecordResourceUrl(submissionDetail));

    sendEmailToRecipient(templateBuilder, energyPortalUser, submissionDetail);
  }

  public void sendSubmissionWithdrawalEmail(SubmissionDetail submissionDetail) {
    var energyPortalUserDtoOptional = getUserFromSubmissionDetail(submissionDetail);

    if (energyPortalUserDtoOptional.isEmpty()) {
      LOGGER.warn("Could not find recipient with WUA_ID {} for submission withdrawal email",
          submissionDetail.getSubmittedByWuaId());
      return;
    }

    var energyPortalUser = energyPortalUserDtoOptional.get();
    var templateBuilder = getTemplateWithCommonFields(NotifyTemplate.SUBMISSION_WITHDRAW_CONFIRMATION, submissionDetail)
        .withMailMergeField(REGULATOR_ORGANISATION_NAME_KEY, getCustomerMnemonic());

    sendEmailToRecipient(templateBuilder, energyPortalUser, submissionDetail);
  }

  public void sendReturnedForUpdateEmails(SubmissionDetail submissionDetail) {
    var recipients = getSubmitterRecipientsBySubmissionDetail(submissionDetail);
    if (recipients.isEmpty()) {
      LOGGER.warn("No submitters for responsible organisation found for submission {}", submissionDetail.getId());
      return;
    }

    var templateBuilder = getTemplateWithCommonFields(NotifyTemplate.SUBMISSION_RETURNED_FOR_UPDATE, submissionDetail)
        .withMailMergeField(REVIEW_URL_KEY, RemiAbsoluteUrlUtil.getReviewSubmissionPageUrl(submissionDetail))
        .withMailMergeField(REGULATOR_ORGANISATION_NAME_KEY, getCustomerMnemonic());

    sendEmailToAllRecipients(templateBuilder, recipients, submissionDetail);
  }

  private MergedTemplate.MergedTemplateBuilder getTemplateWithCommonFields(NotifyTemplate notifyTemplate,
                                                                           SubmissionDetail submissionDetail) {
    var licenceNames = getDisplayableLicences(submissionDetail);
    return emailService.getTemplate(notifyTemplate)
        .withMailMergeField(RESOURCE_NAME_QUAD_BLOCK_KEY, getResourceNameQuadBlock(submissionDetail))
        .withMailMergeField(SUBMISSION_TYPE_WITH_ACRONYM_KEY, getLowercaseSubmissionType(submissionDetail))
        .withMailMergeField(LICENCES_KEY, licenceNames)
        .withMailMergeField(HAS_LICENCES_KEY, !licenceNames.isBlank());
  }

  private void sendEmailToAllRecipients(MergedTemplate.MergedTemplateBuilder mergedTemplateBuilder,
                                        List<EnergyPortalUserDto> recipients,
                                        SubmissionDetail submissionDetail) {
    recipients.forEach(recipient -> sendEmailToRecipient(mergedTemplateBuilder, recipient, submissionDetail));
  }

  private void sendEmailToRecipient(MergedTemplate.MergedTemplateBuilder mergedTemplateBuilder,
                                    EnergyPortalUserDto recipient, SubmissionDetail submissionDetail) {
    var mergedTemplate = mergedTemplateBuilder
        .withMailMergeField(RECIPIENT_KEY, getRecipientName(recipient))
        .merge();

    emailService.sendEmail(
        mergedTemplate,
        recipient,
        submissionDetail
    );
  }

  private NotifyTemplate getSubmissionConfirmationEmailTemplate(SubmissionDetail submissionDetail) {
    return submissionDetail.isUpdateToSubmission()
        ? NotifyTemplate.SUBMISSION_UPDATE_CONFIRMATION
        : NotifyTemplate.SUBMISSION_CONFIRMATION_NOTIFICATION;
  }

  private String getResourceNameQuadBlock(SubmissionDetail submissionDetail) {
    return submissionResourceService
        .getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail);
  }

  private String getDisplayableLicences(SubmissionDetail submissionDetail) {
    var licences = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .stream()
        .flatMap(generalDetail -> licencePersistenceService.getLicencesByGeneralDetail(generalDetail).stream())
        .sorted(new LicenceReferenceComparator())
        .map(Licence::getLicenceReference)
        .toList();
    if (licences.isEmpty()) {
      return "";
    } else {
      return String.join(", ", licences);
    }
  }

  private String getLowercaseSubmissionType(SubmissionDetail submissionDetail) {
    return submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail);
  }

  private String getRecipientName(EnergyPortalUserDto recipient) {
    return "%s %s".formatted(recipient.forename(), recipient.surname());
  }

  private String getCustomerMnemonic() {
    return brandingConfigurationProperties.getCustomerConfigurationProperties().mnemonic();
  }

  private Optional<EnergyPortalUserDto> getUserFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return energyPortalUserService
        .findByWuaId(WebUserAccountId.valueOf(submissionDetail.getSubmittedByWuaId().toString()));
  }

  private Optional<Integer> getResponsibleOrganisationId(SubmissionDetail submissionDetail) {
    if (submissionDetail.getSubmissionType() == SubmissionType.CREATE_DISCOVERY
        || submissionDetail.getSubmissionType() == SubmissionType.CREATE_PROSPECT) {
      //if initial submission, take the responsible organisation from the submitted submission
      return resourceDetailsPersistenceService
          .getResourceDetailBySubmissionDetail(submissionDetail)
          .map(ResourceDetail::getOrganisationId);
    } else {
      return submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail)
          .flatMap(latestApprovedSubmissionDetail -> resourceDetailsPersistenceService
              .getResourceDetailBySubmissionDetail(latestApprovedSubmissionDetail)
              .map(ResourceDetail::getOrganisationId)
          );
    }
  }

  private List<EnergyPortalUserDto> getSubmitterRecipientsBySubmissionDetail(SubmissionDetail submissionDetail) {
    var organisationUnitIdOptional = getResponsibleOrganisationId(submissionDetail);
    if (organisationUnitIdOptional.isEmpty()) {
      return List.of();
    }

    var organisationId = organisationUnitIdOptional.get();
    var organisationGroupIds = organisationUnitQueryService.findOrganisationGroupIdsByUnitId(organisationId);
    if (organisationGroupIds.isEmpty()) {
      return List.of();
    }

    return industryTeamService.getWuaIdsOfUsersWithRoleForOrganisationGroupIds(
        organisationGroupIds,
        Set.of(Role.INDUSTRY_SUBMIT)
    );
  }
}
