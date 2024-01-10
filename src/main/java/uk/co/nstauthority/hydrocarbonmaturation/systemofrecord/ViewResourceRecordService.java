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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class ViewResourceRecordService {

  private final ResourceRecordSummaryQueryService resourceRecordSummaryQueryService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final RegulatorTeamService regulatorTeamService;
  private final IndustryTeamMemberService industryTeamMemberService;
  private final LicenceQueryService licenceQueryService;
  private final ResourceLinkService resourceLinkService;
  private final SubmissionDetailService submissionDetailService;
  private final AreaTeamService areaTeamService;

  @Autowired
  ViewResourceRecordService(ResourceRecordSummaryQueryService resourceRecordSummaryQueryService,
                            SubmissionDetailDisplayService submissionDetailDisplayService,
                            RegulatorTeamService regulatorTeamService,
                            IndustryTeamMemberService industryTeamMemberService,
                            LicenceQueryService licenceQueryService,
                            ResourceLinkService resourceLinkService,
                            SubmissionDetailService submissionDetailService,
                            AreaTeamService areaTeamService) {
    this.resourceRecordSummaryQueryService = resourceRecordSummaryQueryService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.regulatorTeamService = regulatorTeamService;
    this.industryTeamMemberService = industryTeamMemberService;
    this.licenceQueryService = licenceQueryService;
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailService = submissionDetailService;
    this.areaTeamService = areaTeamService;
  }

  public ResourceRecordSummaryView getResourceRecordOrThrow(UUID resourceId) {
    return resourceRecordSummaryQueryService.getResourceRecord(resourceId)
        .map(resourceRecordSummaryQueryResult -> new ResourceRecordSummaryView(
                resourceRecordSummaryQueryResult.submissionDetailId(),
                getResourceNameAndCentreOfInterest(resourceRecordSummaryQueryResult),
                resourceRecordSummaryQueryResult.organisationResponsibleId(),
                getOrganisationResponsible(resourceRecordSummaryQueryResult),
                resourceRecordSummaryQueryResult.licencesTheResourceIsOn(),
                getLicences(resourceRecordSummaryQueryResult),
                getResponsibleTeam(resourceRecordSummaryQueryResult)
            )
        )
        .orElseThrow(() -> new RemiEntityNotFoundException("Resource record not found for resource ID %s".formatted(resourceId)));
  }

  private String getResourceNameAndCentreOfInterest(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getResourceNameAndCentreOfInterestBlockDisplay(
        resourceRecordSummaryQueryResult.resourceName(),
        resourceRecordSummaryQueryResult.centreOfInterestQuadBlock()
    );
  }

  private List<String> getLicences(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getLicencesForDisplay(resourceRecordSummaryQueryResult.licencesTheResourceIsOn());
  }

  private String getOrganisationResponsible(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getOrganisationResponsibleDisplay(
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

  public boolean canViewResource(ResourceRecordSummaryView resourceRecord, User user) {
    if (isRegulatorOrAreaTeamUser(user)) {
      return true;
    }

    var organisationUnitId = resourceRecord.responsibleOrganisationId();
    var memberOfOrganisationUnit = industryTeamMemberService.isMemberOfOrganisationUnit(user, organisationUnitId);

    var resourceIsAssociatedWithAnExtantLicence = isResourceIsAssociatedWithAnExtantLicence(resourceRecord);

    return memberOfOrganisationUnit && resourceIsAssociatedWithAnExtantLicence;
  }

  public boolean isRegulatorOrAreaTeamUser(User user) {
    return regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user);
  }

  private boolean isResourceIsAssociatedWithAnExtantLicence(ResourceRecordSummaryView resourceRecord) {
    if (resourceRecord.licenceIds() == null) {
      return false;
    }
    var extantLicences = licenceQueryService.getExtantLicencesByIds(NumberUtil.toListOfStrings(resourceRecord.licenceIds()));
    return !extantLicences.isEmpty();
  }
}
