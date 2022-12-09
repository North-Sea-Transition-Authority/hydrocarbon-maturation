package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;

@Service
class RegulatorTeamMemberEditService {

  private final TeamMemberRoleService teamMemberRoleService;

  @Autowired
  RegulatorTeamMemberEditService(
      TeamMemberRoleService teamMemberRoleService) {
    this.teamMemberRoleService = teamMemberRoleService;
  }

  void updateRoles(Team team, TeamMember teamMember, Set<String> newRoles) {
    teamMemberRoleService.updateUserTeamRoles(team, teamMember.wuaId(), newRoles);
  }
}
