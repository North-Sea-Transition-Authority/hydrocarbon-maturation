package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
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

  public boolean isMemberOfOrganisationUnit(User user, Integer organisationUnitId) {
    var organisationGroups = organisationUnitQueryService.findOrganisationGroupIdsByUnitId(organisationUnitId);
    return industryTeamService.isTeamMemberForOrganisationGroups(user, organisationGroups);
  }

  public boolean isSubmitterOfOrganisationUnit(User user, Integer organisationUnitId) {
    var organisationGroups = organisationUnitQueryService.findOrganisationGroupIdsByUnitId(organisationUnitId);
    return industryTeamService.isSubmitterForOrganisationGroups(user, organisationGroups);
  }
}