package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;

@Service
public class IndustryTeamMemberService {

  private final IndustryTeamService industryTeamService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  public IndustryTeamMemberService(IndustryTeamService industryTeamService,
                                   OrganisationUnitQueryService organisationUnitQueryService) {
    this.industryTeamService = industryTeamService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  public boolean isMemberOfOrganisationUnit(ServiceUserDetail userDetail, Integer organisationUnitId) {
    var organisationGroups = organisationUnitQueryService.findOrganisationGroupIdsByUnitId(organisationUnitId);
    return industryTeamService.isTeamMemberForOrganisationGroups(userDetail, organisationGroups);
  }
}