package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.EnergyPortalConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddTeamMemberValidator;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = {
    IndustryAddMemberController.class,
    AddTeamMemberValidator.class
})
class IndustryAddMemberControllerTest extends AbstractControllerTest {

  @MockBean
  private IndustryTeamService industryTeamService;

  @MockBean
  private EnergyPortalUserService energyPortalUserService;

  @Autowired
  AddTeamMemberValidator addTeamMemberValidator;

  @Autowired
  private ApplicationContext applicationContext;

  @MockBean
  protected PermissionService permissionService;

  @SecurityTest
  void renderAddTeamMember_whenNotAccessManager_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddMemberController.class).renderAddTeamMember(teamId)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void renderAddTeamMember_whenAccessManager_thenOk() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(true);

    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddMemberController.class).renderAddTeamMember(teamId)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @Test
  void renderAddTeamMember_whenNoTeamFound_thenNotFound() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var teamId = new TeamId(UUID.randomUUID());

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(true);

    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddMemberController.class).renderAddTeamMember(teamId)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @Test
  void renderAddTeamMember_whenTeamIdIsNotIndustryTeam_then404() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(true);

    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddMemberController.class).renderAddTeamMember(teamId)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @Test
  void renderAddTeamMember_assertModelProperties() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(true);

    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var registrationUrl = applicationContext.getBean(EnergyPortalConfiguration.class).registrationUrl();

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddMemberController.class).renderAddTeamMember(teamId)))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/addTeamMember"))
        .andExpect(model().attribute("htmlTitle", "Add user to %s".formatted(team.getDisplayName())))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamId))
        ))
        .andExpect(model().attribute("registrationUrl", registrationUrl))
        .andExpect(model().attribute(
            "submitUrl",
            ReverseRouter.route(on(IndustryAddMemberController.class).addMemberToTeamSubmission(teamId, null, null))
        ));
  }

  @Test
  void addMemberToTeamSubmission_whenTeamIdNotIndustryType_thenNotFound() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(true);

    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddMemberController.class)
                .addMemberToTeamSubmission(teamId, null, null)))
                .with(csrf())
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @Test
  void addMemberToTeamSubmission_whenNotAccessManager_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddMemberController.class)
                .addMemberToTeamSubmission(teamId, null, null)))
                .with(csrf())
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void addMemberToTeamSubmission_whenAccessManager_thenOk() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(true);

    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddMemberController.class)
                .addMemberToTeamSubmission(teamId, null, null)))
                .with(csrf())
                .with(user(user))
        )
        .andExpect(status().isOk());
  }

  @Test
  void addMemberToTeamSubmission_whenInvalidForm_thenUserStaysOnFormPage() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(true);

    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddMemberController.class)
                .addMemberToTeamSubmission(teamId, null, null)))
                .with(csrf())
                .with(user(user))
                .param("username", "")
        )
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/addTeamMember"));
  }

  @Test
  void addMemberToTeamSubmission_whenValidForm_thenUserTakenToRolesSelection() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(permissionService.hasPermission(user, Set.of(RolePermission.MANAGE_ORGANISATIONS))).thenReturn(true);

    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var username = "username";
    var userToAdd = EnergyPortalUserDtoTestUtil.Builder().build();

    when(energyPortalUserService.findUserByUsername(username))
        .thenReturn(List.of(userToAdd));

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddMemberController.class)
                .addMemberToTeamSubmission(teamId, null, null)))
                .with(csrf())
                .with(user(user))
                .param("username", username)
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(IndustryAddRolesController.class)
            .renderAddTeamMemberRoles(teamId, new WebUserAccountId(userToAdd.webUserAccountId())))));
  }
}
