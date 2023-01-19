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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementService;

@ContextConfiguration(classes = IndustryTeamManagementController.class)
class IndustryTeamManagementControllerTest extends AbstractControllerTest {

  @MockBean
  private TeamMemberViewService teamMemberViewService;

  @MockBean
  private IndustryTeamService industryTeamService;

  @MockBean
  private NewTeamFormValidator newTeamFormValidator;

  @MockBean
  private OrganisationGroupQueryService organisationGroupQueryService;

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

    var user = ServiceUserDetailTestUtil.Builder().build();

    var teamId = new TeamId(UUID.randomUUID());

    when(teamManagementService.isMemberOfTeamOrIndustryAccessManager(teamId, user)).thenReturn(false);

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

    var teamId = team.toTeamId();

    when(teamManagementService.isMemberOfTeamOrIndustryAccessManager(teamId, user)).thenReturn(true);
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

    when(teamManagementService.isMemberOfTeamOrIndustryAccessManager(teamId, user)).thenReturn(true);
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

    var teamId = team.toTeamId();

    when(teamManagementService.isMemberOfTeamOrIndustryAccessManager(teamId, user)).thenReturn(true);
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

    var teamId = team.toTeamId();

    when(industryTeamService.isAccessManager(teamId, user)).thenReturn(true);

    when(teamManagementService.isMemberOfTeamOrIndustryAccessManager(teamId, user)).thenReturn(true);
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

    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(true);

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
    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(true);

    var organisationGroupId = 10000;
    var orgGroup = OrganisationGroupTestUtil.createOrganisationGroupDto(organisationGroupId, "Royal Dutch Shell");

    var form = new NewTeamForm();
    form.setOrganisationGroupId(String.valueOf(organisationGroupId));

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    when(industryTeamService.getTeamByOrganisationGroupId((organisationGroupId)))
        .thenReturn(Optional.of(team));

    when(organisationGroupQueryService.getOrganisationGroupById(any())).thenReturn(Optional.of(orgGroup));

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
    when(teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)).thenReturn(true);

    var organisationGroupId = 10000;
    var orgGroup = OrganisationGroupTestUtil.createOrganisationGroupDto(organisationGroupId, "Royal Dutch Shell");

    when(organisationGroupQueryService.getOrganisationGroupById(any())).thenReturn(Optional.of(orgGroup));

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