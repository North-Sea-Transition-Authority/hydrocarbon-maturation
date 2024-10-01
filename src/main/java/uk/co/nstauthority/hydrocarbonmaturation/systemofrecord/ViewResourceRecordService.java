package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.FieldDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class ViewResourceRecordService {

  private final ResourceRecordSummaryQueryService resourceRecordSummaryQueryService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final LicenceQueryService licenceQueryService;
  private final ResourceLinkService resourceLinkService;
  private final SubmissionDetailService submissionDetailService;
  private final FieldDisplayService fieldDisplayService;
  private final IndustryTeamService industryTeamService;
  private final InternalTeamService internalTeamService;

  @Autowired
  ViewResourceRecordService(ResourceRecordSummaryQueryService resourceRecordSummaryQueryService,
                            SubmissionDetailDisplayService submissionDetailDisplayService,
                            LicenceQueryService licenceQueryService,
                            ResourceLinkService resourceLinkService,
                            SubmissionDetailService submissionDetailService,
                            FieldDisplayService fieldDisplayService,
                            IndustryTeamService industryTeamService, InternalTeamService internalTeamService) {
    this.resourceRecordSummaryQueryService = resourceRecordSummaryQueryService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.licenceQueryService = licenceQueryService;
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailService = submissionDetailService;
    this.fieldDisplayService = fieldDisplayService;
    this.industryTeamService = industryTeamService;
    this.internalTeamService = internalTeamService;
  }

  public ResourceRecordSummaryView getResourceRecordOrThrow(UUID resourceId) {
    return resourceRecordSummaryQueryService.getResourceRecord(resourceId)
        .map(resourceRecordSummaryQueryResult -> new ResourceRecordSummaryView(
                resourceRecordSummaryQueryResult.submissionDetailId(),
                resourceRecordSummaryQueryResult.resourceName(),
                resourceRecordSummaryQueryResult.centreOfInterestQuadBlock(),
                resourceRecordSummaryQueryResult.licenceApplicationReference(),
                resourceRecordSummaryQueryResult.organisationResponsibleId(),
                getOrganisationResponsible(resourceRecordSummaryQueryResult),
                resourceRecordSummaryQueryResult.licencesTheResourceIsOn(),
                getLicences(resourceRecordSummaryQueryResult),
                getResponsibleTeam(resourceRecordSummaryQueryResult),
                fieldDisplayService.getParentFieldNameOrNoneFound(resourceRecordSummaryQueryResult.parentFieldId()),
                resourceRecordSummaryQueryResult.fieldStatus() != null
                    ? resourceRecordSummaryQueryResult.fieldStatus().getDisplayName()
                    : null
            )
        )
        .orElseThrow(() -> new RemiEntityNotFoundException("Resource record not found for resource ID %s".formatted(resourceId)));
  }

  private List<String> getLicences(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getLicencesForDisplay(resourceRecordSummaryQueryResult.licencesTheResourceIsOn());
  }

  private String getOrganisationResponsible(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getOrganisationResponsibleDisplayById(
        resourceRecordSummaryQueryResult.organisationResponsibleId());
  }

  private static ResponsibleTeamType getResponsibleTeam(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return resourceRecordSummaryQueryResult.responsibleTeam();
  }

  public boolean canViewSubmissionDetail(SubmissionDetail selectedSubmissionDetail, Resource resource) {
    //can only view submitted or approved submissions on the same resource, or linked resource
    //the submission detail to be viewed also has to be the latest approved on submission
    return selectedSubmissionDetail.getStatus() == SubmissionDetailStatus.ACCEPTED
        && BooleanUtils.isNotTrue(selectedSubmissionDetail.getSubmission().isWithdrawn())
        && resourceLinkService.isResourceLinkedOrSameResource(
            resource, selectedSubmissionDetail.getSubmission().getResource())
        && submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
            selectedSubmissionDetail, Set.of(SubmissionDetailStatus.ACCEPTED));
  }

  public boolean canViewResource(ResourceRecordSummaryView resourceRecord, Resource resource, User user) {
    if (isRegulatorOrAreaTeamUser(user)) {
      return true;
    }

    var organisationUnitId = resourceRecord.responsibleOrganisationId();
    var memberOfOrganisationUnit = industryTeamService.getOrganisationUnitIdsForUser(user).contains(organisationUnitId);

    var resourceIsAssociatedWithAnExtantLicence = isResourceAssociatedWithAnExtantLicence(resourceRecord);

    return memberOfOrganisationUnit && resourceIsAssociatedWithAnExtantLicence && !resource.getArchived();
  }

  public boolean isRegulatorOrAreaTeamUser(User user) {
    return internalTeamService.isRegulatorOrAreaTeamUser(user);
  }

  public boolean isAreaTeamUser(User user) {
    return internalTeamService.isAreaTeamUser(user);
  }

  private boolean isResourceAssociatedWithAnExtantLicence(ResourceRecordSummaryView resourceRecord) {
    if (resourceRecord.licenceIds() == null) {
      return false;
    }
    var extantLicences = licenceQueryService.getExtantLicencesByIds(NumberUtil.toListOfStrings(resourceRecord.licenceIds()));
    return !extantLicences.isEmpty();
  }
}
