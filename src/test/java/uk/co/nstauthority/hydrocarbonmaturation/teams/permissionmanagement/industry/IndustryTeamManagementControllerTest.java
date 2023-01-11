package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamTypeService;

@ContextConfiguration(classes = IndustryTeamManagementController.class)
class IndustryTeamManagementControllerTest extends AbstractControllerTest {

  @MockBean
  private TeamMemberViewService teamMemberViewService;

  @MockBean
  private IndustryTeamService industryTeamService;

  @MockBean
  private TeamTypeService teamTypeService;

  @MockBean
  private NewTeamFormValidator newTeamFormValidator;

  @MockBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @MockBean
  private PermissionService permissionService;

  @SecurityTest
  void renderTeamList_whenNotAuthenticated_thenUnauthorised() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderTeamList())))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void renderTeamList_whenNoAccessToIndustryTeam_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    when(industryTeamService.getIndustryTeamsForUser(user)).thenReturn(Collections.emptyList());

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderTeamList()))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderTeamList_whenAccessToIndustryTeam_thenOK() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(UUID.randomUUID())
        .build();
    var team2 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(UUID.randomUUID())
        .build();
    when(industryTeamService.getIndustryTeamsForUser(user)).thenReturn(List.of(TeamView.fromTeam(team1), TeamView.fromTeam(team2)));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderTeamList()))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @Test
  void renderTeamList_whenAccessToSingleIndustryTeam_thenRedirect() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(UUID.randomUUID())
        .build();
    when(industryTeamService.getIndustryTeamsForUser(user)).thenReturn(List.of(TeamView.fromTeam(team1)));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderTeamList()))
                .with(user(user)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(IndustryTeamManagementController.class)
            .renderMemberList(TeamId.valueOf(team1.getUuid())))));
  }

  @Test
  void renderTeamList_whenRegulatorAndIndustryAccessManager_thenOk() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(UUID.randomUUID())
        .build();
    var team2 = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(UUID.randomUUID())
        .build();
    when(teamTypeService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(true);
    when(industryTeamService.getAllTeams()).thenReturn(List.of(team1, team2));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderTeamList()))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @Test
  void renderMemberList_whenNotAuthenticated_thenUnauthorised() throws Exception {
    var teamId = new TeamId(UUID.randomUUID());
    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Disabled("Disabled until HM-12 is done")
  void renderMemberList_whenNotMemberOfTeam_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderMemberList_whenMemberOfTeam_thenOk() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = new TeamId(team.getUuid());

    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @Test
  void renderMemberList_whenNoTeamFound_thenNotFound() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @Test
  void renderMemberList_whenNotAccessManager_assertModelProperties() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = new TeamId(team.getUuid());

    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRole(IndustryTeamRole.SUBMITTER)
        .build();

    when(teamMemberViewService.getTeamMemberViewsForTeam(team)).thenReturn(List.of(teamMemberView));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId)))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/teamMembers"))
        .andExpect(model().attribute("pageTitle", "Manage %s".formatted(team.getDisplayName())))
        .andExpect(model().attribute("teamName", team.getDisplayName()))
        .andExpect(model().attribute("teamRoles", IndustryTeamRole.values()))
        .andExpect(model().attributeDoesNotExist("addTeamMemberUrl"));
  }

  @Test
  void renderMemberList_whenAccessManager_assertModelProperties() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = new TeamId(team.getUuid());

    when(industryTeamService.isAccessManager(teamId, user)).thenReturn(true);

    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(Set.of(IndustryTeamRole.ACCESS_MANAGER))
        .build();
    when(teamMemberViewService.getTeamMemberViewsForTeam(team)).thenReturn(List.of(teamMemberView));

    var canRemoveUsers = true;
    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS)))
        .thenReturn(canRemoveUsers);

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
        .andExpect(model().attribute("teamMembers", List.of(teamMemberView)));
  }


  @Test
  void renderNewTeamForm_authorisedUser_isOk() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().build();

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryTeamManagementController.class).renderNewIndustryTeamForm(new NewTeamForm())))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/addTeam"))
    ;
  }

  @Test
  void addNewIndustryTeam_FormValidates_RedirectToTeamMemberList() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().withWuaId(100L).build();

    var orgGroup = new OrganisationGroup();
    orgGroup.setName("Royal Dutch Shell");
    var organisationGroupId = 1000;
    orgGroup.setOrganisationGroupId(organisationGroupId);

    var form = new NewTeamForm();
    form.setOrganisationGroupId("1000");

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    when(industryTeamService.getTeamByOrganisationGroupId((organisationGroupId)))
        .thenReturn(Optional.of(team));

    when(organisationGroupQueryService.getOrganisationGroupById(any(), any())).thenReturn(Optional.of(orgGroup));

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryTeamManagementController.class).addNewIndustryTeam(form, emptyBindingResult(), null)))
                .flashAttr("form", form)
                .with(csrf())
                .with(user(user)))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void addNewIndustryTeam_FormValidatesGroupDoesntExist_RedirectToTeamMemberList() throws Exception {
    var user = ServiceUserDetailTestUtil.Builder().withWuaId(100L).build();

    var orgGroup = new OrganisationGroup();
    orgGroup.setName("Royal Dutch Shell");
    var organisationGroupId = 1000;
    orgGroup.setOrganisationGroupId(organisationGroupId);

    when(organisationGroupQueryService.getOrganisationGroupById(any(), any())).thenReturn(Optional.of(orgGroup));

    var form = new NewTeamForm();
    form.setOrganisationGroupId(String.valueOf(organisationGroupId));

    when(industryTeamService.getTeamByOrganisationGroupId((organisationGroupId)))
        .thenReturn(Optional.empty());

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    when(industryTeamService.createTeam(anyString(), anyInt(), anyLong())).thenReturn(team);

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryTeamManagementController.class).addNewIndustryTeam(form, emptyBindingResult(), null)))
                .flashAttr("form", form)
                .with(csrf())
                .with(user(user)))
        .andExpect(status().is3xxRedirection());
  }
}