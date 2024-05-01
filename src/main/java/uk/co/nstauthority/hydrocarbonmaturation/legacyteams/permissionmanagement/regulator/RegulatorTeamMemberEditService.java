package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRoleService;

@Service
class RegulatorTeamMemberEditService {

  private final TeamMemberRoleService teamMemberRoleService;

  @Autowired
  RegulatorTeamMemberEditService(
      TeamMemberRoleService teamMemberRoleService) {
    this.teamMemberRoleService = teamMemberRoleService;
  }

  void updateRoles(LegacyTeam legacyTeam, TeamMember teamMember, Set<String> newRoles) {
    teamMemberRoleService.updateUserTeamRoles(legacyTeam, teamMember.wuaId(), newRoles);
  }
}
