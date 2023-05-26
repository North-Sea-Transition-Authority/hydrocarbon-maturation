package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@Service
public class TeamMemberRemovalService {

  public static final String LAST_ACCESS_MANAGER_ERROR_MESSAGE = "You cannot remove the last access manager of a team";
  public static final Set<? extends TeamRole> ACCESS_MANAGER_ROLES = Set.of(
      RegulatorTeamRole.ACCESS_MANAGER,
      IndustryTeamRole.ACCESS_MANAGER,
      NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM,
      OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM
  );

  private final TeamMemberService teamMemberService;

  private final TeamMemberRoleService teamMemberRoleService;

  @Autowired
  TeamMemberRemovalService(TeamMemberService teamMemberService, TeamMemberRoleService teamMemberRoleService) {
    this.teamMemberService = teamMemberService;
    this.teamMemberRoleService = teamMemberRoleService;
  }

  public void removeTeamMember(Team team, TeamMember teamMember) {
    if (canRemoveTeamMember(team, teamMember)) {
      teamMemberRoleService.removeMemberFromTeam(team, teamMember);
    } else {
      throw new IllegalStateException(
          "User [%s] cannot be removed from team [%s] as they are the last access manager"
              .formatted(teamMember.wuaId(), team.getUuid())
      );
    }
  }

  public boolean canRemoveTeamMember(Team team, TeamMember teamMember) {
    var teamMembers = teamMemberService.getTeamMembers(team);
    var accessManagers = filterAccessManagers(teamMembers);
    return accessManagers.stream()
        .anyMatch(tm -> !tm.wuaId().equals(teamMember.wuaId()));
  }

  private List<TeamMember> filterAccessManagers(Collection<TeamMember> teamMembers) {
    return teamMembers.stream()
        .filter(teamMember -> teamMember.roles().stream()
            .anyMatch(ACCESS_MANAGER_ROLES::contains))
        .toList();
  }

}
