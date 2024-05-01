package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area;

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
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamView;

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

  private final LegacyTeam legacyTeam = new LegacyTeam(UUID.randomUUID());
  private final WebUserAccountId wuaId = new WebUserAccountId(42L);
  private final TeamView teamView = new TeamView(new TeamId(UUID.randomUUID()), TeamType.REGULATOR, "team");
  private final TeamMember teamMember = new TeamMember(wuaId, teamView, Set.of());
  private final TeamMemberView teamMemberView = new TeamMemberView(wuaId, teamView, "title", "a", "b", "email", "0", Set.of());
  private final String backLinkUrl = "backLinkUrl";
  private final String removeUrl = "removeUrl";


  @Test
  void renderRemoveMember() {

    when(teamMemberService.getTeamMember(legacyTeam, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(teamMemberRemovalService.canRemoveTeamMember(legacyTeam, teamMember)).thenReturn(true);

    var modelAndView = areaRemoveMemberControllerHelperService.renderRemoveMember(legacyTeam, wuaId, new ModelAndView(), backLinkUrl, removeUrl);
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

    when(teamMemberService.getTeamMember(legacyTeam, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(teamMemberRemovalService.canRemoveTeamMember(legacyTeam, teamMember)).thenReturn(false);

    var modelAndView = areaRemoveMemberControllerHelperService.renderRemoveMember(legacyTeam, wuaId, new ModelAndView(), backLinkUrl, removeUrl);
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/removeTeamMember");
    assertThat(modelAndView.getModel()).containsEntry("pageTitle", "You are unable to remove %s from %s".formatted(teamMemberView.getDisplayName(), teamView.displayName()));
    assertThat(modelAndView.getModel()).containsEntry("canRemoveTeamMember", false);
  }

  @Test
  void renderRemoveMember_emptyTeamMember() {

    when(teamMemberService.getTeamMember(legacyTeam, wuaId)).thenReturn(Optional.empty());

    var redirectModelAndView = new ModelAndView();
    assertThat(areaRemoveMemberControllerHelperService.renderRemoveMember(legacyTeam, wuaId, redirectModelAndView, backLinkUrl, removeUrl))
        .isEqualTo(redirectModelAndView);
  }

  @Test
  void renderRemoveMember_noTeamMemberView() {

    when(teamMemberService.getTeamMember(legacyTeam, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> areaRemoveMemberControllerHelperService
        .renderRemoveMember(legacyTeam, wuaId, new ModelAndView(), backLinkUrl, removeUrl))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("No roles found for user [%s] in team [%s]"
            .formatted(teamMember.wuaId(), teamMember.teamView().teamId()));
  }

  @Test
  void removeMember() {

    when(teamMemberService.getTeamMember(legacyTeam, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(teamMemberRemovalService.canRemoveTeamMember(legacyTeam, teamMember)).thenReturn(true);

    var redirectModelAndView = new ModelAndView();
    var redirectAttributes = new RedirectAttributesModelMap();
    assertThat(areaRemoveMemberControllerHelperService.removeMember(legacyTeam, wuaId, new ModelAndView(), redirectAttributes, redirectModelAndView))
        .isEqualTo(redirectModelAndView);

    verify(teamMemberRemovalService).removeTeamMember(legacyTeam, teamMember);
  }

  @Test
  void removeHeader_cantRemoveMember() {

    when(teamMemberService.getTeamMember(legacyTeam, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberRemovalService.canRemoveTeamMember(legacyTeam, teamMember)).thenReturn(false);

    var renderRemoveMemberRedirectModelAndView = new ModelAndView();
    assertThat(areaRemoveMemberControllerHelperService.removeMember(legacyTeam, wuaId, renderRemoveMemberRedirectModelAndView, null, new ModelAndView()))
        .isEqualTo(renderRemoveMemberRedirectModelAndView);
  }

  @Test
  void removeMember_noTeamMember() {

    when(teamMemberService.getTeamMember(legacyTeam, wuaId)).thenReturn(Optional.empty());

    var redirectModelAndView = new ModelAndView();
    var redirectAttributes = new RedirectAttributesModelMap();

    assertThatThrownBy(() -> areaRemoveMemberControllerHelperService
        .removeMember(legacyTeam, wuaId, new ModelAndView(), redirectAttributes, redirectModelAndView))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("No user [%s] in team [%s]".formatted(wuaId, legacyTeam.toTeamId()));
  }
}