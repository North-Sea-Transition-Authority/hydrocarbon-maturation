package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IndustryRemoveMemberController.class})
class IndustryRemoveMemberControllerTest extends AbstractControllerTest {

  private static final IndustryTeamRole ACCESS_MANAGER_ROLE = IndustryTeamRole.ACCESS_MANAGER;

  @MockBean
  private TeamMemberViewService teamMemberViewService;

  @MockBean
  private TeamMemberRemovalService industryTeamMemberRemovalService;

  @MockBean
  private IndustryTeamService industryTeamService;

  private Team industryTeam;
  private WebUserAccountId wuaId;

  private ServiceUserDetail loggedInUser;

  @BeforeEach
  void setUp() {

    industryTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(UUID.randomUUID())
        .build();

    wuaId = new WebUserAccountId(Math.abs(new Random().nextLong()));

    loggedInUser = ServiceUserDetailTestUtil.Builder()
        .withWuaId(wuaId.id())
        .build();
  }

  @SecurityTest
  void renderRemoveMember_whenNotLoggedIn_thenNotAuthorized() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(IndustryRemoveMemberController.class)
        .renderRemoveMember(new TeamId(industryTeam.getUuid()), wuaId)))
    ).andExpect(status().isUnauthorized());
  }

  @SecurityTest
  @Disabled("Disabled until HM-12 is done")
  void renderRemoveMember_whenNotAccessManager_thenForbidden() throws Exception {

    var teamId = new TeamId(industryTeam.getUuid());

    // Interceptor setup
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(
        teamId,
        loggedInUser,
        Set.of(ACCESS_MANAGER_ROLE.name()))
    ).thenReturn(false);

    mockMvc.perform(get(ReverseRouter.route(on(IndustryRemoveMemberController.class)
        .renderRemoveMember(new TeamId(industryTeam.getUuid()), wuaId)))
        .with(user(loggedInUser))
    ).andExpect(status().isForbidden());
  }

  @Test
  void renderRemoveMember_whenAccessManagerAndUserCanBeRemoved_thenOk() throws Exception {

    var teamId = new TeamId(industryTeam.getUuid());

    // Interceptor setup
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(
        teamId,
        loggedInUser,
        Set.of(ACCESS_MANAGER_ROLE.name()))
    ).thenReturn(true);

    // Endpoint setup
    var teamMember = TeamMemberTestUtil.Builder().build();
    var teamMemberView = TeamMemberViewTestUtil.Builder().build();

    var canRemoveTeamMember = true;

    when(industryTeamService.getTeam(new TeamId(industryTeam.getUuid()))).thenReturn(Optional.of(industryTeam));
    when(teamMemberService.getTeamMember(industryTeam, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(industryTeamMemberRemovalService.canRemoveTeamMember(industryTeam, teamMember))
        .thenReturn(canRemoveTeamMember);

    mockMvc.perform(get(ReverseRouter.route(on(IndustryRemoveMemberController.class)
            .renderRemoveMember(teamId, wuaId)))
            .with(user(loggedInUser)))
        .andExpect(status().isOk())
        .andExpect(model().attribute("teamName", industryTeam.getDisplayName()))
        .andExpect(model().attribute("teamMember", teamMemberView))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(IndustryTeamManagementController.class)
                .renderMemberList(new TeamId(industryTeam.getUuid())))
        ))
        .andExpect(model().attribute(
            "removeUrl",
            ReverseRouter.route(on(IndustryRemoveMemberController.class)
                .removeMember(new TeamId(industryTeam.getUuid()), wuaId, null))
        ))
        .andExpect(model().attribute("canRemoveTeamMember", canRemoveTeamMember))
        .andExpect(model().attribute(
            "pageTitle",
            "Are you sure you want to remove %s from %s?".formatted(teamMemberView.getDisplayName(), industryTeam.getDisplayName())
        ))
        .andReturn()
        .getModelAndView();
  }

  @Test
  void renderRemoveMember_whenLastAccessManager_thenOk() throws Exception {

    var teamId = new TeamId(industryTeam.getUuid());

    // Interceptor setup
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(
        teamId,
        loggedInUser,
        Set.of(ACCESS_MANAGER_ROLE.name()))
    ).thenReturn(true);

    // Endpoint setup
    var teamMember = TeamMemberTestUtil.Builder().build();
    var teamMemberView = TeamMemberViewTestUtil.Builder().build();

    var canRemoveTeamMember = false;

    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(industryTeam));
    when(teamMemberService.getTeamMember(industryTeam, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(industryTeamMemberRemovalService.canRemoveTeamMember(industryTeam, teamMember))
        .thenReturn(canRemoveTeamMember);

    mockMvc.perform(get(ReverseRouter.route(on(IndustryRemoveMemberController.class)
            .renderRemoveMember(teamId, wuaId)))
            .with(user(loggedInUser)))
        .andExpect(status().isOk())
        .andExpect(model().attribute("teamName", industryTeam.getDisplayName()))
        .andExpect(model().attribute("teamMember", teamMemberView))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(IndustryTeamManagementController.class)
                .renderMemberList(teamId))
        ))
        .andExpect(model().attribute(
            "removeUrl",
            ReverseRouter.route(on(IndustryRemoveMemberController.class)
                .removeMember(teamId, wuaId, null))
        ))
        .andExpect(model().attribute("canRemoveTeamMember", canRemoveTeamMember))
        .andExpect(model().attribute(
            "pageTitle",
            "You are unable to remove %s from %s".formatted(teamMemberView.getDisplayName(), industryTeam.getDisplayName())
        ))
        .andReturn()
        .getModelAndView();
  }
}
