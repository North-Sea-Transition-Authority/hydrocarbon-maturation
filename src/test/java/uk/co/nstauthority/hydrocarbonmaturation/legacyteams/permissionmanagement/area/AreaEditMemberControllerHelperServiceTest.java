package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamView;

@ExtendWith(MockitoExtension.class)
class AreaEditMemberControllerHelperServiceTest {

  @Mock
  private TeamMemberService teamMemberService;

  @Mock
  private TeamMemberViewService teamMemberViewService;

  @InjectMocks
  private AreaEditMemberControllerHelperService areaEditMemberControllerHelperService;

  @Test
  void renderEditMember() {

    var team = new LegacyTeam();
    var wuaId = new WebUserAccountId(42L);
    var teamView = new TeamView(new TeamId(UUID.randomUUID()), TeamType.REGULATOR, "team");
    var teamMember = new TeamMember(wuaId, teamView, Set.of());
    var teamMemberView = new TeamMemberView(wuaId, teamView, "title", "a", "b", "email", "0", Set.of());
    var roleMap = Map.of("role", "description");
    var backLinkUrl = "backLinkUrl";

    when(teamMemberService.getTeamMember(team, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));

    var modelAndView = areaEditMemberControllerHelperService.renderEditMember(team, wuaId, roleMap, backLinkUrl);
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/addTeamMemberRoles");
    assertThat(modelAndView.getModel()).containsEntry("pageTitle", teamMemberView.getDisplayName());
    assertThat(modelAndView.getModel()).containsEntry("roles", roleMap);
    assertThat(modelAndView.getModel()).containsEntry("backLinkUrl", backLinkUrl);
  }

  @Test
  void getTeamMemberOrThrow_throws() {
    var team = new LegacyTeam(UUID.randomUUID());
    var wuaId = new WebUserAccountId(42L);

    when(teamMemberService.getTeamMember(team, wuaId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> areaEditMemberControllerHelperService.getTeamMemberOrThrow(team, wuaId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("No user [%s] in team [%s]".formatted(wuaId, team.toTeamId()));
  }

  @Test
  void getTeamMemberViewOrThrow_throws() {
    var wuaId = new WebUserAccountId(42L);
    var teamView = new TeamView(new TeamId(UUID.randomUUID()), TeamType.REGULATOR, "team");
    var teamMember = new TeamMember(wuaId, teamView, Set.of());

    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> areaEditMemberControllerHelperService.getTeamMemberViewOrThrow(teamMember))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("No roles found for user [%s] in team [%s]"
            .formatted(teamMember.wuaId(), teamMember.teamView().teamId()));
  }
}