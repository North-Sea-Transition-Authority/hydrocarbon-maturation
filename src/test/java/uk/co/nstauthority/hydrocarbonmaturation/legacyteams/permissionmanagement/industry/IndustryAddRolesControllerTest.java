package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry;

import static org.mockito.Mockito.doCallRealMethod;
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

@ContextConfiguration(classes = {
    IndustryAddRolesController.class,
    IndustryTeamMemberRolesValidator.class
})
class IndustryAddRolesControllerTest extends AbstractControllerTest {

  @MockBean
  private IndustryTeamService industryTeamService;

  @MockBean
  private EnergyPortalUserService energyPortalUserService;

  @Autowired
  IndustryTeamMemberRolesValidator industryTeamMemberRolesValidator;

  @MockBean
  protected PermissionService permissionService;

  @BeforeEach
  void setUp() {
    doCallRealMethod().when(userService).getUser();
  }

  @SecurityTest
  void renderAddTeamMemberRoles_whenAccessManager_thenOk() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.of(EnergyPortalUserDtoTestUtil.Builder().build()));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void renderAddTeamMember_whenRegulatorIndustryAccessManager_thenOk() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.of(EnergyPortalUserDtoTestUtil.Builder().build()));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void renderAddTeamMemberRoles_whenNotAccessManager_thenForbidden() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.VIEWER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderAddTeamMemberRoles_whenNotIndustryTeam_thenForbidden() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();

    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderAddTeamMemberRoles_whenNoEnergyPortalUser_thenNotFound() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @MethodSource("getEnergyPortalUserThatShouldResultInBadRequest")
  void renderAddTeamMemberRoles_whenEnergyPortalUserNotValid_thenBadRequest(EnergyPortalUserDto energyPortalUser) throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.of(energyPortalUser));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void renderAddTeamMemberRoles_assertModelProperties() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();

    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    var energyPortalUserToAdd = EnergyPortalUserDtoTestUtil.Builder().build();

    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.of(energyPortalUserToAdd));

    mockMvc.perform(
            get(ReverseRouter.route(on(IndustryAddRolesController.class)
                .renderAddTeamMemberRoles(teamId, webUserAccountIdToAdd)))
                .with(user(user)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/addTeamMemberRoles"))
        .andExpect(model().attribute(
            "pageTitle",
            "What actions does %s perform?".formatted(energyPortalUserToAdd.displayName())
        ))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(IndustryAddMemberController.class).renderAddTeamMember(teamId))
        ))
        .andExpect(model().attributeExists("form"))
        .andExpect(model().attribute("roles", getDisplayableIndustryRoles()));
  }

  @Test
  void saveAddTeamMemberRoles_whenNotAccessManager_thenForbidden() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.VIEWER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddRolesController.class)
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
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));
    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.of(EnergyPortalUserDtoTestUtil.Builder().build()));

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
        )
        .andExpect(status().isOk());
  }

  @Test
  void saveAddTeamMemberRoles_whenRegulatorIndustryAccessManager_thenOk() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));
    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.of(EnergyPortalUserDtoTestUtil.Builder().build()));

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
        )
        .andExpect(status().isOk());
  }

  @Test
  void saveAddTeamMemberRoles_whenNotIndustryTeam_thenForbidden() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();

    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void saveAddTeamMemberRoles_whenEnergyPortalUserNotFound_thenNotFound() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(energyPortalUserService.findByWuaId(teamMember.wuaId()))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
        )
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @MethodSource("getEnergyPortalUserThatShouldResultInBadRequest")
  void saveAddTeamMemberRoles_whenEnergyPortalUserNotValid_thenBadRequest(EnergyPortalUserDto energyPortalUser) throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.of(energyPortalUser));

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  void saveAddTeamMemberRoles_whenInvalidTeamMemberRolesForm_thenStayOnFormPage() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();

    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.of(EnergyPortalUserDtoTestUtil.Builder().build()));

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
                .param("roles", "")
        )
        .andExpect(status().isOk())
        .andExpect(view().name("remi/permissionmanagement/addTeamMemberRoles"));
  }

  @Test
  void saveAddTeamMemberRoles_whenValidTeamMemberRolesForm_thenRedirectionToMembersPage() throws Exception {

    var user = UserTestUtil.Builder().build();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();

    var teamId = team.toTeamId();

    var webUserAccountIdToAdd = new WebUserAccountId(123);

    when(teamMemberService.getUserAsTeamMembers(user)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamId, user)).thenReturn(true);
    when(industryTeamService.getTeam(teamId)).thenReturn(Optional.of(team));

    when(energyPortalUserService.findByWuaId(webUserAccountIdToAdd))
        .thenReturn(Optional.of(EnergyPortalUserDtoTestUtil.Builder().build()));

    mockMvc.perform(
            post(ReverseRouter.route(on(IndustryAddRolesController.class)
                .saveAddTeamMemberRoles(teamId, webUserAccountIdToAdd, null, null)))
                .with(user(user))
                .with(csrf())
                .param("roles", IndustryTeamRole.ACCESS_MANAGER.name())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(IndustryTeamManagementController.class)
            .renderMemberList(teamId))));
  }

  private Map<String, String> getDisplayableIndustryRoles() {
    return Arrays.stream(IndustryTeamRole.values())
        .sorted(Comparator.comparing(IndustryTeamRole::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(
            IndustryTeamRole::name,
            role -> "%s (%s)".formatted(role.getDescription(), role.getDisplayName())
        ));
  }

  private static Stream<Arguments> getEnergyPortalUserThatShouldResultInBadRequest() {

    var noLoginEnergyPortalUser = EnergyPortalUserDtoTestUtil.Builder()
        .canLogin(false)
        .build();

    var sharedAccountEnergyPortalUser = EnergyPortalUserDtoTestUtil.Builder()
        .hasSharedAccount(true)
        .build();

    return Stream.of(
        Arguments.of(noLoginEnergyPortalUser),
        Arguments.of(sharedAccountEnergyPortalUser)
    );
  }
}