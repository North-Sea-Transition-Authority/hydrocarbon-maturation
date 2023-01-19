package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Service
public class IndustryTeamService {

  private final TeamService teamService;
  private final TeamMemberService teamMemberService;
  private final TeamMemberRoleService teamMemberRoleService;

  @Autowired
  IndustryTeamService(TeamService teamService,
                      TeamMemberService teamMemberService,
                      TeamMemberRoleService teamMemberRoleService) {
    this.teamService = teamService;
    this.teamMemberService = teamMemberService;
    this.teamMemberRoleService = teamMemberRoleService;
  }

  public boolean isAccessManager(TeamId teamId, ServiceUserDetail user) {
    return teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(IndustryTeamRole.ACCESS_MANAGER.name()));
  }

  Optional<Team> getTeam(TeamId teamId) {
    return teamService.getTeam(teamId, TeamType.INDUSTRY);
  }

  void addUserTeamRoles(Team team, EnergyPortalUserDto userToAdd, Set<IndustryTeamRole> roles) {
    var rolesAsStrings = roles.stream()
        .map(IndustryTeamRole::name)
        .collect(Collectors.toSet());

    teamMemberRoleService.addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }

  @Transactional
  public Team createTeam(String groupName, int organisationGroupId, long wuaId) {
    var team = new Team();
    team.setTeamType(TeamType.INDUSTRY);
    team.setDisplayName(groupName);
    team.setOrganisationGroupId(organisationGroupId);
    teamService.createTeam(team);

    teamMemberRoleService.updateUserTeamRoles(team, new WebUserAccountId(wuaId), Set.of("ACCESS_MANAGER"));
    return team;
  }

  public Optional<Team> getTeamByOrganisationGroupId(int organisationGroupId) {
    return teamService.getTeamByOrganisationGroupId(organisationGroupId);
  }

}
