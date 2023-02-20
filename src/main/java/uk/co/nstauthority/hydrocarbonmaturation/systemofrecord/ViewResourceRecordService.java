package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
class ViewResourceRecordService {

  private final ResourceRecordSummaryQueryService resourceRecordSummaryQueryService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final RegulatorTeamService regulatorTeamService;
  private final IndustryTeamMemberService industryTeamMemberService;
  private final LicenceQueryService licenceQueryService;

  @Autowired
  ViewResourceRecordService(ResourceRecordSummaryQueryService resourceRecordSummaryQueryService,
                            SubmissionDetailDisplayService submissionDetailDisplayService,
                            RegulatorTeamService regulatorTeamService,
                            IndustryTeamMemberService industryTeamMemberService,
                            LicenceQueryService licenceQueryService) {
    this.resourceRecordSummaryQueryService = resourceRecordSummaryQueryService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.regulatorTeamService = regulatorTeamService;
    this.industryTeamMemberService = industryTeamMemberService;
    this.licenceQueryService = licenceQueryService;
  }

  ResourceRecordSummaryView getResourceRecordOrThrow(UUID resourceId) {
    return resourceRecordSummaryQueryService.getResourceRecord(resourceId)
        .map(resourceRecordSummaryQueryResult -> new ResourceRecordSummaryView(
                resourceRecordSummaryQueryResult.getSubmissionDetailId(),
                getResourceNameAndCentreOfInterest(resourceRecordSummaryQueryResult),
                resourceRecordSummaryQueryResult.getOrganisationResponsibleId(),
                getOrganisationResponsible(resourceRecordSummaryQueryResult),
                resourceRecordSummaryQueryResult.getLicencesTheResourceIsOn(),
                getLicences(resourceRecordSummaryQueryResult),
                getResponsibleTeam(resourceRecordSummaryQueryResult)
            )
        )
        .orElseThrow(() -> new RemiEntityNotFoundException("Resource record not found for resource ID %s".formatted(resourceId)));
  }

  private String getResourceNameAndCentreOfInterest(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getResourceNameAndCentreOfInterestBlockDisplay(
        resourceRecordSummaryQueryResult.getResourceName(),
        resourceRecordSummaryQueryResult.getCentreOfInterestQuadBlock(),
        resourceRecordSummaryQueryResult.getCreatedOn()
    );
  }

  private List<String> getLicences(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getLicencesForDisplay(resourceRecordSummaryQueryResult.getLicencesTheResourceIsOn());
  }

  private String getOrganisationResponsible(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getOrganisationResponsibleDisplay(
        resourceRecordSummaryQueryResult.getOrganisationResponsibleId());
  }

  private static String getResponsibleTeam(ResourceRecordSummaryQueryResult resourceRecordSummaryQueryResult) {
    return resourceRecordSummaryQueryResult.getResponsibleTeam().getDisplayName();
  }

  public boolean canViewResource(ResourceRecordSummaryView resourceRecord, ServiceUserDetail user) {
    var isRegulatorUser = regulatorTeamService.isRegulatorUser(user);

    if (isRegulatorUser) {
      return true;
    }

    var organisationUnitId = resourceRecord.responsibleOrganisationId();
    var memberOfOrganisationUnit = industryTeamMemberService.isMemberOfOrganisationUnit(user, organisationUnitId);

    var resourceIsAssociatedWithAnExtantLicence = isResourceIsAssociatedWithAnExtantLicence(resourceRecord);

    return memberOfOrganisationUnit && resourceIsAssociatedWithAnExtantLicence;
  }

  private boolean isResourceIsAssociatedWithAnExtantLicence(ResourceRecordSummaryView resourceRecord) {
    var extantLicences = licenceQueryService.getExtantLicencesByIds(NumberUtil.toListOfStrings(resourceRecord.licenceIds()));
    return !extantLicences.isEmpty();
  }


}
