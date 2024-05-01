package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator;

import static org.mockito.Mockito.verify;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;

@ExtendWith(MockitoExtension.class)
class RegulatorLegacyTeamMemberEditServiceTest {

  @Mock
  private TeamMemberRoleService teamMemberRoleService;

  @InjectMocks
  private RegulatorTeamMemberEditService regulatorTeamMemberEditService;

  @Test
  void updateRoles() {
    var team = new LegacyTeam(UUID.randomUUID());
    var teamView = TeamTestUtil.createTeamView(team);
    var teamMember = new TeamMember(new WebUserAccountId(1L), teamView, Set.of());
    var newRoles = Set.of(RegulatorTeamRole.ACCESS_MANAGER.name());

    regulatorTeamMemberEditService.updateRoles(team, teamMember, newRoles);

    verify(teamMemberRoleService).updateUserTeamRoles(team, teamMember.wuaId(), newRoles);
  }
}
