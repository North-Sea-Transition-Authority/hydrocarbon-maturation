package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.offshore;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddTeamMemberForm;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddTeamMemberValidator;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaAddMemberControllerHelperService;

@ContextConfiguration(classes = {
    OffshoreAreaAddMemberController.class,
    AddTeamMemberValidator.class
})
class OffshoreAreaAddMemberControllerTest extends AbstractControllerTest {

  @MockBean
  private OffshoreAreaTeamService offshoreAreaTeamService;

  @MockBean
  private EnergyPortalUserService energyPortalUserService;

  @Autowired
  AddTeamMemberValidator addTeamMemberValidator;

  @MockBean
  protected PermissionService permissionService;

  @MockBean
  private AreaAddMemberControllerHelperService areaAddMemberControllerHelperService;

  @SecurityTest
  void renderAddTeamMember_whenNotAccessManager_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(OffshoreAreaTeamRole.OFFSHORE_AREA_TEAM_MEMBER)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(OffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void renderAddTeamMember_whenAccessManager_thenOk() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(OffshoreAreaTeamRole.OFFSHORE_AREA_MANAGE_TEAM)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(offshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    mockMvc.perform(
            get(ReverseRouter.route(on(OffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @Test
  void renderAddTeamMember_whenNoTeamFound_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var teamId = new TeamId(UUID.randomUUID());

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.OFFSHORE_AREA)
        .withRole(OffshoreAreaTeamRole.OFFSHORE_AREA_TEAM_MEMBER)
        .build();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(false);
    when(permissionService.hasPermission(user, Set.of())).thenReturn(true);
    when(offshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(OffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderAddTeamMember_whenTeamIdIsNotAreaTeam_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(OffshoreAreaTeamRole.OFFSHORE_AREA_MANAGE_TEAM)
        .build();

    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(offshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(OffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void addMemberToTeamSubmission_whenTeamIdNotAreaType_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(OffshoreAreaTeamRole.OFFSHORE_AREA_MANAGE_TEAM)
        .build();

    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(offshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(OffshoreAreaAddMemberController.class)
                .addMemberToTeamSubmission(teamId, null, null)))
                .with(csrf())
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void addMemberToTeamSubmission_whenNotAccessManager_thenForbidden() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(OffshoreAreaTeamRole.OFFSHORE_AREA_TEAM_MEMBER)
        .build();
    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(OffshoreAreaAddMemberController.class)
                .addMemberToTeamSubmission(teamId, null, null)))
                .with(csrf())
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void addMemberToTeamSubmission_whenAccessManager_thenOk() throws Exception {

    var user = ServiceUserDetailTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.OFFSHORE_AREA)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(OffshoreAreaTeamRole.OFFSHORE_AREA_MANAGE_TEAM)
        .build();

    var teamId = team.toTeamId();

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(offshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var userToAdd = EnergyPortalUserDtoTestUtil.Builder().build();
    when(energyPortalUserService.findUserByUsername(any()))
        .thenReturn(List.of(userToAdd));

    var modelAndViewOk = new ModelAndView();
    modelAndViewOk.setStatus(HttpStatus.OK);
    when(areaAddMemberControllerHelperService.addMemberToTeamSubmission(
        any(BindingResult.class),
        any(AddTeamMemberForm.class),
        eq(team),
        eq(ReverseRouter.redirect(on(OffshoreAreaAddRolesController.class)
            .renderAddTeamMemberRoles(teamId, new WebUserAccountId(userToAdd.webUserAccountId())))),
        eq(ReverseRouter.route(on(OffshoreAreaAddMemberController.class)
            .addMemberToTeamSubmission(teamId, new AddTeamMemberForm(), ReverseRouter.emptyBindingResult()))),
        eq(ReverseRouter.route(on(OffshoreAreaTeamManagementController.class).renderMemberList(teamId)))))
        .thenReturn(modelAndViewOk);

    mockMvc.perform(
            post(ReverseRouter.route(on(OffshoreAreaAddMemberController.class)
                .addMemberToTeamSubmission(teamId, null, null)))
                .with(csrf())
                .with(user(user))
        )
        .andExpect(status().isOk());
  }
}