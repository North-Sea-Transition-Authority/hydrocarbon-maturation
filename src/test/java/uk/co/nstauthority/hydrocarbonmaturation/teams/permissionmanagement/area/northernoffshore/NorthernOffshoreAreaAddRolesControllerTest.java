package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaAddRolesControllerHelperService;

@ContextConfiguration(classes = {
    NorthernOffshoreAreaAddRolesController.class,
    NorthernOffshoreAreaTeamMemberRolesValidator.class
})
class NorthernOffshoreAreaAddRolesControllerTest extends AbstractControllerTest {

  @MockBean
  private NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;

  @Autowired
  NorthernOffshoreAreaTeamMemberRolesValidator northernOffshoreAreaTeamMemberRolesValidator;

  @MockBean
  protected PermissionService permissionService;

  @MockBean
  private AreaAddRolesControllerHelperService areaAddRolesControllerHelperService;

  @BeforeEach
  void setUp() {
    doCallRealMethod().when(userService).getUser();
  }

  @SecurityTest
  void renderAddTeamMemberRoles_whenAccessManager_thenOk() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(northernOffshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var modelAndViewOk = new ModelAndView();
    modelAndViewOk.setStatus(HttpStatus.OK);
    when(areaAddRolesControllerHelperService.renderAddTeamMemberRoles(webUserAccountIdToAdd,
        DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(NorthernOffshoreAreaTeamRole.class),
        ReverseRouter.route(on(NorthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId))))
        .thenReturn(modelAndViewOk);

    mockMvc.perform(
            get(ReverseRouter.route(on(NorthernOffshoreAreaAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void renderAddTeamMemberRoles_whenNotAccessManager_thenForbidden() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(NorthernOffshoreAreaAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderAddTeamMemberRoles_whenNotAreaTeam_thenForbidden() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM)
        .build();

    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(northernOffshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(NorthernOffshoreAreaAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void saveAddTeamMemberRoles_whenNotAccessManager_thenForbidden() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(NorthernOffshoreAreaAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void saveAddTeamMemberRoles_whenAccessManager_thenOk() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(northernOffshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(areaAddRolesControllerHelperService.getEnergyPortalUser(webUserAccountIdToAdd))
        .thenReturn(EnergyPortalUserDtoTestUtil.Builder().build());
    when(areaAddRolesControllerHelperService.getAddTeamMemberRolesModelAndView(any(), any(),
        eq(ReverseRouter.route(on(NorthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId))),
        eq(DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(NorthernOffshoreAreaTeamRole.class))))
        .thenReturn(new ModelAndView());

    mockMvc.perform(
            post(ReverseRouter.route(on(NorthernOffshoreAreaAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
        )
        .andExpect(status().isOk());
  }

  @Test
  void saveAddTeamMemberRoles_whenNotAreaTeam_thenForbidden() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM)
        .build();

    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(northernOffshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(NorthernOffshoreAreaAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void saveAddTeamMemberRoles_whenInvalidTeamMemberRolesForm_thenStayOnFormPage() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM)
        .build();

    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(northernOffshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(areaAddRolesControllerHelperService.getEnergyPortalUser(webUserAccountIdToAdd))
        .thenReturn(EnergyPortalUserDtoTestUtil.Builder().build());
    when(areaAddRolesControllerHelperService.getAddTeamMemberRolesModelAndView(any(), any(),
        eq(ReverseRouter.route(on(NorthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId))),
        eq(DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(NorthernOffshoreAreaTeamRole.class))))
        .thenReturn(new ModelAndView());

    mockMvc.perform(
            post(ReverseRouter.route(on(NorthernOffshoreAreaAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
                .param("roles", "")
        )
        .andExpect(status().isOk());
  }

  @Test
  void saveAddTeamMemberRoles_whenValidTeamMemberRolesForm_thenRedirectionToMembersPage() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM)
        .build();

    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(northernOffshoreAreaTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(areaAddRolesControllerHelperService.getEnergyPortalUser(webUserAccountIdToAdd))
        .thenReturn(EnergyPortalUserDtoTestUtil.Builder().build());
    when(areaAddRolesControllerHelperService.getAddTeamMemberRolesModelAndView(any(), any(),
        eq(ReverseRouter.route(on(NorthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId))),
        eq(DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(NorthernOffshoreAreaTeamRole.class))))
        .thenReturn(new ModelAndView());

    mockMvc.perform(
            post(ReverseRouter.route(on(NorthernOffshoreAreaAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
                .param("roles", NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM.name())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(NorthernOffshoreAreaTeamManagementController.class)
            .renderMemberList(teamId))));
  }
}