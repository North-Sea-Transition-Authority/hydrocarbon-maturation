package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;

@ExtendWith(MockitoExtension.class)
class AreaRemoveMemberControllerHelperServiceTest {

  @Mock
  private TeamMemberService teamMemberService;

  @Mock
  private TeamMemberViewService teamMemberViewService;

  @Mock
  private TeamMemberRemovalService teamMemberRemovalService;

  @InjectMocks
  private AreaRemoveMemberControllerHelperService areaRemoveMemberControllerHelperService;

  private final Team team = new Team(UUID.randomUUID());
  private final WebUserAccountId wuaId = new WebUserAccountId(42L);
  private final TeamView teamView = new TeamView(new TeamId(UUID.randomUUID()), TeamType.REGULATOR, "team");
  private final TeamMember teamMember = new TeamMember(wuaId, teamView, Set.of());
  private final TeamMemberView teamMemberView = new TeamMemberView(wuaId, teamView, "title", "a", "b", "email", "0", Set.of());
  private final String backLinkUrl = "backLinkUrl";
  private final String removeUrl = "removeUrl";


  @Test
  void renderRemoveMember() {

    when(teamMemberService.getTeamMember(team, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(teamMemberRemovalService.canRemoveTeamMember(team, teamMember)).thenReturn(true);

    var modelAndView = areaRemoveMemberControllerHelperService.renderRemoveMember(team, wuaId, new ModelAndView(), backLinkUrl, removeUrl);
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/removeTeamMember");
    assertThat(modelAndView.getModel()).containsEntry("pageTitle", "Are you sure you want to remove %s from %s?".formatted(teamMemberView.getDisplayName(), teamView.displayName()));
    assertThat(modelAndView.getModel()).containsEntry("teamName", teamView.displayName());
    assertThat(modelAndView.getModel()).containsEntry("teamMember", teamMemberView);
    assertThat(modelAndView.getModel()).containsEntry("canRemoveTeamMember", true);
    assertThat(modelAndView.getModel()).containsEntry("backLinkUrl", backLinkUrl);
    assertThat(modelAndView.getModel()).containsEntry("removeUrl", removeUrl);
  }

  @Test
  void renderRemoveMember_cantRemoveMember() {

    when(teamMemberService.getTeamMember(team, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(teamMemberRemovalService.canRemoveTeamMember(team, teamMember)).thenReturn(false);

    var modelAndView = areaRemoveMemberControllerHelperService.renderRemoveMember(team, wuaId, new ModelAndView(), backLinkUrl, removeUrl);
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/removeTeamMember");
    assertThat(modelAndView.getModel()).containsEntry("pageTitle", "You are unable to remove %s from %s".formatted(teamMemberView.getDisplayName(), teamView.displayName()));
    assertThat(modelAndView.getModel()).containsEntry("canRemoveTeamMember", false);
  }

  @Test
  void renderRemoveMember_emptyTeamMember() {

    when(teamMemberService.getTeamMember(team, wuaId)).thenReturn(Optional.empty());

    var redirectModelAndView = new ModelAndView();
    assertThat(areaRemoveMemberControllerHelperService.renderRemoveMember(team, wuaId, redirectModelAndView, backLinkUrl, removeUrl))
        .isEqualTo(redirectModelAndView);
  }

  @Test
  void renderRemoveMember_noTeamMemberView() {

    when(teamMemberService.getTeamMember(team, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> areaRemoveMemberControllerHelperService
        .renderRemoveMember(team, wuaId, new ModelAndView(), backLinkUrl, removeUrl))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("No roles found for user [%s] in team [%s]"
            .formatted(teamMember.wuaId(), teamMember.teamView().teamId()));
  }

  @Test
  void removeMember() {

    when(teamMemberService.getTeamMember(team, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(teamMemberRemovalService.canRemoveTeamMember(team, teamMember)).thenReturn(true);

    var redirectModelAndView = new ModelAndView();
    var redirectAttributes = new RedirectAttributesModelMap();
    assertThat(areaRemoveMemberControllerHelperService.removeMember(team, wuaId, new ModelAndView(), redirectAttributes, redirectModelAndView))
        .isEqualTo(redirectModelAndView);

    verify(teamMemberRemovalService).removeTeamMember(team, teamMember);
  }

  @Test
  void removeHeader_cantRemoveMember() {

    when(teamMemberService.getTeamMember(team, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberRemovalService.canRemoveTeamMember(team, teamMember)).thenReturn(false);

    var renderRemoveMemberRedirectModelAndView = new ModelAndView();
    assertThat(areaRemoveMemberControllerHelperService.removeMember(team, wuaId, renderRemoveMemberRedirectModelAndView, null, new ModelAndView()))
        .isEqualTo(renderRemoveMemberRedirectModelAndView);
  }

  @Test
  void removeMember_noTeamMember() {

    when(teamMemberService.getTeamMember(team, wuaId)).thenReturn(Optional.empty());

    var redirectModelAndView = new ModelAndView();
    var redirectAttributes = new RedirectAttributesModelMap();

    assertThatThrownBy(() -> areaRemoveMemberControllerHelperService
        .removeMember(team, wuaId, new ModelAndView(), redirectAttributes, redirectModelAndView))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("No user [%s] in team [%s]".formatted(wuaId, team.toTeamId()));
  }
}