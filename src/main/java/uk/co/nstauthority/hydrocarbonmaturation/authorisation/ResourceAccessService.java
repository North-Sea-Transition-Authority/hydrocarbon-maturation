package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;

@Service
public class ResourceAccessService {

  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final TeamQueryService teamQueryService;

  @Autowired
  public ResourceAccessService(OrganisationUnitQueryService organisationUnitQueryService,
                               TeamQueryService teamQueryService) {
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.teamQueryService = teamQueryService;
  }

  public boolean userHasAccessToResourceForOrganisation(User user, Set<Role> roles, Integer organisationUnitId) {
    var portalOrganisationGroups = organisationUnitQueryService.findOrganisationGroupIdsByUnitId(organisationUnitId)
        .stream()
        .map(String::valueOf)
        .toList();
    var responsibleOrganisationGroups = teamQueryService.getOrganisationScopedTeams(portalOrganisationGroups)
        .stream()
        .map(Team::getId)
        .collect(Collectors.toSet());
    var userSubmitterTeams = teamQueryService.getTeamRolesForUser(user.wuaId())
        .stream()
        .filter(role -> roles.contains(role.getRole()))
        .map(TeamRole::getTeam)
        .map(Team::getId)
        .collect(Collectors.toSet());

    return userSubmitterTeams.stream().anyMatch(responsibleOrganisationGroups::contains);
  }
}
