package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@ContextConfiguration(classes = IndustryTeamManagementController.class)
class IndustryTeamManagementControllerTest extends AbstractControllerTest {

  @MockBean
  private TeamMemberViewService teamMemberViewService;

  @MockBean
  private IndustryTeamService industryTeamService;

  @MockBean
  protected PermissionService permissionService;

  @MockBean
  private TeamManagementService teamManagementService;

  @SecurityTest
  void renderMemberList_whenNotAuthenticated_thenUnauthorised() throws Exception {
    var teamId = new TeamId(UUID.randomUUID());
    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void renderMemberList_whenNotMemberOfTeam_thenForbidden() throws Exception {
    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.VIEWER)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = IndustryTeamRole.class)
  void renderMemberList_whenMemberOfTeam_thenOk(IndustryTeamRole industryTeamRole) throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(industryTeamRole)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void renderMemberList_whenNoTeamFound_thenNotFound() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @Test
  void renderMemberList_whenNotAccessManager_assertModelProperties() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRole(IndustryTeamRole.SUBMITTER)
        .build();

    when(teamMemberViewService.getTeamMemberViewsForTeam(team)).thenReturn(List.of(teamMemberView));
    when(teamService.isUserInMultipleTeams(user)).thenReturn(false);
    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/teamMembers"))
        .andExpect(model().attribute("pageTitle", "Manage %s".formatted(team.getDisplayName())))
        .andExpect(model().attribute("teamName", team.getDisplayName()))
        .andExpect(model().attribute("teamRoles", IndustryTeamRole.values()))
        .andExpect(model().attributeDoesNotExist("addTeamMemberUrl"))
        .andExpect(model().attributeDoesNotExist("backLinkUrl"));
  }

  @Test
  void renderMemberList_whenAccessManager_assertModelProperties() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(Set.of(IndustryTeamRole.ACCESS_MANAGER))
        .build();
    when(teamMemberViewService.getTeamMemberViewsForTeam(team)).thenReturn(List.of(teamMemberView));

    var canRemoveUsers = true;
    when(permissionService.hasPermission(
        user,
        Set.of(
            RolePermission.INDUSTRY_MANAGE_ORGANISATION,
            RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS)
        )
    ).thenReturn(canRemoveUsers);

    when(teamService.isUserInMultipleTeams(user)).thenReturn(true);
    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/teamMembers"))
        .andExpect(model().attribute("pageTitle", "Manage %s".formatted(team.getDisplayName())))
        .andExpect(model().attribute("teamName", team.getDisplayName()))
        .andExpect(model().attribute("teamRoles", IndustryTeamRole.values()))
        .andExpect(model().attribute(
            "addTeamMemberUrl",
            ReverseRouter.route(on(IndustryAddMemberController.class).renderAddTeamMember(teamId))
        ))
        .andExpect(model().attribute("canRemoveUsers", canRemoveUsers))
        .andExpect(model().attribute("teamMembers", List.of(teamMemberView)))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect())));
  }

  @Test
  void renderMemberList_whenIndustryManager_assertModelProperties() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER))
        .build();
    when(teamMemberViewService.getTeamMemberViewsForTeam(team)).thenReturn(List.of(teamMemberView));

    var canRemoveUsers = false;
    when(permissionService.hasPermission(
            user,
            Set.of(
                RolePermission.INDUSTRY_MANAGE_ORGANISATION,
                RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS)
        )
    ).thenReturn(canRemoveUsers);

    when(teamService.isUserInMultipleTeams(user)).thenReturn(false);
    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/teamMembers"))
        .andExpect(model().attribute("pageTitle", "Manage %s".formatted(team.getDisplayName())))
        .andExpect(model().attribute("teamName", team.getDisplayName()))
        .andExpect(model().attribute("teamRoles", IndustryTeamRole.values()))
        .andExpect(model().attribute("teamMembers", List.of(teamMemberView)))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect())));
  }
}