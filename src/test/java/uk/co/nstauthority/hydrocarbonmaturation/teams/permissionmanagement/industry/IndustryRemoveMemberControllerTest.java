package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@ContextConfiguration(classes = IndustryRemoveMemberController.class)
class IndustryRemoveMemberControllerTest extends AbstractControllerTest {

  private static final IndustryTeamRole ACCESS_MANAGER_ROLE = IndustryTeamRole.ACCESS_MANAGER;

  @MockBean
  private TeamMemberViewService teamMemberViewService;

  @MockBean
  private TeamMemberRemovalService industryTeamMemberRemovalService;

  @MockBean
  private IndustryTeamService industryTeamService;

  private Team industryTeam;
  private TeamView teamView;

  private ServiceUserDetail accessManager;
  private ServiceUserDetail nonAccessManager;

  @BeforeEach
  void setUp() {

    industryTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    teamView = TeamTestUtil.createTeamView(industryTeam);
    accessManager = ServiceUserDetailTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
    nonAccessManager = ServiceUserDetailTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
  }

  @SecurityTest
  void renderRemoveMember_whenNotAccessManager_thenForbidden() throws Exception {

    Set<TeamRole> userRoles = Set.of(IndustryTeamRole.VIEWER);

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryTeam));

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(nonAccessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(nonAccessManager)).thenReturn(List.of(teamMember));

    mockMvc.perform(get(ReverseRouter.route(on(IndustryRemoveMemberController.class)
        .renderRemoveMember(industryTeam.toTeamId(), teamMember.wuaId())))
        .with(user(nonAccessManager))
    ).andExpect(status().isForbidden());
  }

  @SecurityTest
  void renderRemoveMember_whenAccessManagerAndUserCanBeRemoved_thenOk() throws Exception {

    Set<TeamRole> userRoles = Set.of(IndustryTeamRole.ACCESS_MANAGER);

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryTeam));

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    var canRemoveTeamMember = true;

    when(teamMemberService.getTeamMember(industryTeam, teamMember.wuaId())).thenReturn(Optional.of(teamMember));

    var teamMemberView = TeamMemberViewTestUtil.Builder().build();
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(industryTeamMemberRemovalService.canRemoveTeamMember(industryTeam, teamMember))
        .thenReturn(canRemoveTeamMember);

    mockMvc.perform(get(ReverseRouter.route(on(IndustryRemoveMemberController.class)
            .renderRemoveMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk())
        .andExpect(model().attribute("teamName", industryTeam.getDisplayName()))
        .andExpect(model().attribute("teamMember", teamMemberView))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(IndustryTeamManagementController.class)
                .renderMemberList(industryTeam.toTeamId()))
        ))
        .andExpect(model().attribute(
            "removeUrl",
            ReverseRouter.route(on(IndustryRemoveMemberController.class)
                .removeMember(industryTeam.toTeamId(), teamMember.wuaId(), null))
        ))
        .andExpect(model().attribute("canRemoveTeamMember", canRemoveTeamMember))
        .andExpect(model().attribute(
            "pageTitle",
            "Are you sure you want to remove %s from %s?".formatted(teamMemberView.getDisplayName(), industryTeam.getDisplayName())
        ))
        .andReturn()
        .getModelAndView();
  }

  @SecurityTest
  void renderRemoveMember_whenLastAccessManager_thenOk() throws Exception {

    Set<TeamRole> userRoles = Set.of(IndustryTeamRole.ACCESS_MANAGER);

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryTeam));

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    var canRemoveTeamMember = false;

    when(teamMemberService.getTeamMember(industryTeam, teamMember.wuaId())).thenReturn(Optional.of(teamMember));

    var teamMemberView = TeamMemberViewTestUtil.Builder().build();
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(industryTeamMemberRemovalService.canRemoveTeamMember(industryTeam, teamMember))
        .thenReturn(canRemoveTeamMember);

    mockMvc.perform(get(ReverseRouter.route(on(IndustryRemoveMemberController.class)
            .renderRemoveMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk())
        .andExpect(model().attribute("teamName", industryTeam.getDisplayName()))
        .andExpect(model().attribute("teamMember", teamMemberView))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(IndustryTeamManagementController.class)
                .renderMemberList(industryTeam.toTeamId()))
        ))
        .andExpect(model().attribute(
            "removeUrl",
            ReverseRouter.route(on(IndustryRemoveMemberController.class)
                .removeMember(industryTeam.toTeamId(), teamMember.wuaId(), null))
        ))
        .andExpect(model().attribute("canRemoveTeamMember", canRemoveTeamMember))
        .andExpect(model().attribute(
            "pageTitle",
            "You are unable to remove %s from %s".formatted(teamMemberView.getDisplayName(), industryTeam.getDisplayName())
        ))
        .andReturn()
        .getModelAndView();
  }

  @SecurityTest
  void renderEditMember_whenRegulatorIndustryAccessManager_thenOk() throws Exception {

    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER);

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    when(teamMemberService.getTeamMember(industryTeam, teamMember.wuaId()))
        .thenReturn(Optional.of(teamMember));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(userRoles)
        .withWebUserAccountId(teamMember.wuaId())
        .build();
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));

    mockMvc.perform(get(ReverseRouter.route(on(IndustryRemoveMemberController.class)
            .renderRemoveMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk());

  }
}
