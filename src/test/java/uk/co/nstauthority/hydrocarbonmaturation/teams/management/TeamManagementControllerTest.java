package uk.co.nstauthority.hydrocarbonmaturation.teams.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.EnergyPortalConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.form.AddMemberFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.form.MemberRolesFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.view.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.view.TeamTypeView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.view.TeamView;

@SuppressWarnings({"unchecked", "DataFlowIssue"})
@ContextConfiguration(classes = TeamManagementController.class)
public class TeamManagementControllerTest extends AbstractControllerTest {

  @MockBean
  private MemberRolesFormValidator memberRolesFormValidator;

  @MockBean
  private AddMemberFormValidator addMemberFormValidator;

  @MockBean
  private EnergyPortalConfiguration energyPortalConfiguration;

  private static Team regTeam;
  private static Team organisationTeam;
  private static Team areaTeam;
  private static TeamMemberView regTeamMemberView;
  private static TeamMemberView orgTeamMemberView;
  private static TeamMemberView areaTeamMemberView;
  private static User invokingUser;

  @BeforeAll
  public static void setUp() {
    regTeam = new Team(UUID.randomUUID());
    regTeam.setTeamType(TeamType.REGULATOR);
    regTeam.setName("reg team one");

    organisationTeam = new Team(UUID.randomUUID());
    organisationTeam.setTeamType(TeamType.INDUSTRY);
    organisationTeam.setName("org team");

    areaTeam = new Team(UUID.randomUUID());
    areaTeam.setTeamType(TeamType.NORTHERN_OFFSHORE_AREA);
    areaTeam.setName("area team");

    regTeamMemberView = new TeamMemberView(
        1L,
        "Test",
        "User",
        "test@example.com",
        "020123456",
        regTeam.getId(),
        List.of(Role.MANAGE_TEAM)
    );

    orgTeamMemberView = new TeamMemberView(
        2L,
        "Test",
        "User",
        "test@example.com",
        "020123456",
        organisationTeam.getId(),
        List.of(Role.MANAGE_TEAM)
    );

    areaTeamMemberView = new TeamMemberView(
        3L,
        "Test",
        "User",
        "test@example.com",
        "020123456",
        areaTeam.getId(),
        List.of(Role.MANAGE_TEAM)
    );

    invokingUser = UserTestUtil.Builder()
        .withWuaId(1L)
        .build();
  }

  @SecurityTest
  void renderTeamTypeList() throws Exception {
    when(teamManagementService.getTeamTypesUserIsMemberOf(invokingUser.wuaId()))
        .thenReturn(Set.of(TeamType.INDUSTRY, TeamType.REGULATOR));

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypeList(null)))
        .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andReturn().getModelAndView();

    var teamTypeViews = (List<TeamTypeView>) modelAndView.getModel().get("teamTypeViews");

    assertThat(teamTypeViews)
        .extracting(TeamTypeView::teamTypeName)
        .containsExactly(TeamType.INDUSTRY.getDisplayName(), TeamType.REGULATOR.getDisplayName());
  }

  @Test
  void renderTeamTypeList_singeTypeRedirects() throws Exception {
    when(teamManagementService.getTeamTypesUserIsMemberOf(invokingUser.wuaId()))
        .thenReturn(Set.of(TeamType.INDUSTRY));

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypeList(null)))
        .with(user(invokingUser)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TeamManagementController.class).renderTeamsOfType(TeamType.INDUSTRY.getUrlSlug(), null))));
  }

  @Test
  void renderTeamTypeList_regWithOrgManageCanSeeOrgTeams() throws Exception {
    when(teamManagementService.getTeamTypesUserIsMemberOf(invokingUser.wuaId()))
        .thenReturn(Set.of(TeamType.REGULATOR));

    when(teamQueryService.userHasStaticRole(invokingUser.wuaId(), TeamType.REGULATOR, Role.REGULATOR_MANAGE_ALL_ORGANISATIONS))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypeList(null)))
        .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andReturn().getModelAndView();

    var teamTypeViews = (List<TeamTypeView>) modelAndView.getModel().get("teamTypeViews");

    assertThat(teamTypeViews)
        .extracting(TeamTypeView::teamTypeName)
        .containsExactly(TeamType.INDUSTRY.getDisplayName(), TeamType.REGULATOR.getDisplayName());
  }

  @Test
  void renderTeamTypeList_noManageableTeams() throws Exception {
    when(teamManagementService.getTeamTypesUserIsMemberOf(invokingUser.wuaId()))
        .thenReturn(Set.of());

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamTypeList(null)))
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderTeamsOfType_staticTeamRedirectsToSingleInstance() throws Exception {
    when(teamManagementService.getStaticTeamOfTypeUserIsMemberOf(TeamType.REGULATOR, invokingUser.wuaId()))
        .thenReturn(Optional.of(regTeam));

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamsOfType(TeamType.REGULATOR.getUrlSlug(), null)))
        .with(user(invokingUser)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(regTeam.getId(), null))));
  }

  @Test
  void renderTeamsOfType_singleScopedTeamRedirectsToInstance() throws Exception {
    when(teamManagementService.getScopedTeamsOfTypeUserIsMemberOf(TeamType.INDUSTRY, invokingUser.wuaId()))
        .thenReturn(Set.of(organisationTeam));

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamsOfType(TeamType.INDUSTRY.getUrlSlug(), null)))
        .with(user(invokingUser)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(organisationTeam.getId(), null))));
  }

  @Test
  void renderTeamsOfType_scopedTeamReturnList() throws Exception {

    var firstOrganisationTeamByName = new Team(UUID.randomUUID());
    firstOrganisationTeamByName.setTeamType(TeamType.INDUSTRY);
    firstOrganisationTeamByName.setName("a team name");

    var secondOrganisationTeamByName = new Team(UUID.randomUUID());
    secondOrganisationTeamByName.setTeamType(TeamType.INDUSTRY);
    secondOrganisationTeamByName.setName("b team name");

    var thirdOrganisationTeamByName = new Team(UUID.randomUUID());
    thirdOrganisationTeamByName.setTeamType(TeamType.INDUSTRY);
    thirdOrganisationTeamByName.setName("C team name");

    when(teamManagementService.getScopedTeamsOfTypeUserIsMemberOf(TeamType.INDUSTRY, invokingUser.wuaId()))
        .thenReturn(Set.of(secondOrganisationTeamByName, thirdOrganisationTeamByName, firstOrganisationTeamByName));

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class)
        .renderTeamsOfType(TeamType.INDUSTRY.getUrlSlug(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andReturn().getModelAndView();

    var teamTypeViews = (List<TeamView>) modelAndView.getModel().get("teamViews");

    assertThat(teamTypeViews)
        .extracting(TeamView::teamName)
        .containsExactly(
            firstOrganisationTeamByName.getName(),
            secondOrganisationTeamByName.getName(),
            thirdOrganisationTeamByName.getName()
        );
  }

  @Test
  void renderTeamsOfType_noManageableTeams() throws Exception {
    when(teamManagementService.getScopedTeamsOfTypeUserIsMemberOf(TeamType.INDUSTRY, invokingUser.wuaId()))
        .thenReturn(Set.of());

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamsOfType(TeamType.INDUSTRY.getUrlSlug(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderTeamsOfType_noManageableTeams_orgAdminNotForbidden() throws Exception {
    when(teamManagementService.getScopedTeamsOfTypeUserIsMemberOf(TeamType.INDUSTRY, invokingUser.wuaId()))
        .thenReturn(Set.of());

    when(teamQueryService.userHasStaticRole(invokingUser.wuaId(), TeamType.REGULATOR, Role.REGULATOR_MANAGE_ALL_ORGANISATIONS))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class)
        .renderTeamsOfType(TeamType.INDUSTRY.getUrlSlug(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andReturn().getModelAndView();

    var createNewInstanceUrl = (String) modelAndView.getModel().get("createNewInstanceUrl");

    assertThat(createNewInstanceUrl)
        .isEqualTo(TeamType.INDUSTRY.getCreateNewInstanceRoute());
  }


  @Test
  void renderTeamMemberList_whenNotMemberOfTeam_thenForbidden() throws Exception {

    var team = regTeam;

    when(teamManagementService.getTeam(team.getId()))
        .thenReturn(Optional.of(team));

    when(teamManagementService.isMemberOfTeam(team, invokingUser.wuaId()))
        .thenReturn(false);

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(team.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderTeamMemberList_whenMemberOfTeam_thenOk() throws Exception {

    var team = regTeam;

    when(teamManagementService.getTeam(team.getId()))
        .thenReturn(Optional.of(team));

    when(teamManagementService.isMemberOfTeam(team, invokingUser.wuaId()))
        .thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(team.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isOk());
  }

  @Test
  void renderTeamMemberList_whenOrganisationTeam_andNotMemberOfTeam_andUserHasManageAnyOrganisationRole_thenOk() throws Exception {

    // GIVEN an organisation team
    var team = organisationTeam;

    when(teamManagementService.getTeam(team.getId()))
        .thenReturn(Optional.of(team));

    // AND the invoking user is not a direct member
    when(teamManagementService.isMemberOfTeam(team, invokingUser.wuaId()))
        .thenReturn(false);

    // WHEN the invoking user has the CREATE_MANAGE_ANY_ORGANISATION_TEAM in the regulator team
    when(teamManagementService.userCanManageAnyOrganisationTeam(invokingUser.wuaId()))
        .thenReturn(true);

    // THEN the invoking user will be able to view the team
    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(team.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isOk());
  }

  @Test
  void renderTeamMemberList_whenOrganisationTeam_andNotMemberOfTeam_andUserWithoutManageAnyOrganisationRole_thenForbidden() throws Exception {

    // GIVEN an organisation team
    var team = organisationTeam;

    when(teamManagementService.getTeam(team.getId()))
        .thenReturn(Optional.of(team));

    // AND the invoking user is not a direct member
    when(teamManagementService.isMemberOfTeam(team, invokingUser.wuaId()))
        .thenReturn(false);

    // WHEN the invoking user does not have the CREATE_MANAGE_ANY_ORGANISATION_TEAM in the regulator team
    when(teamManagementService.userCanManageAnyOrganisationTeam(invokingUser.wuaId()))
        .thenReturn(false);

    // THEN the invoking user will not be able to view the team
    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(team.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = TeamType.class, mode = EnumSource.Mode.EXCLUDE, names = "INDUSTRY")
  void renderTeamMemberList_whenNotOrganisationTeam_andNotMemberOfTeam_andCanManageAnyOrganisationRole_thenForbidden(TeamType nonOrganisationTeamType) throws Exception {

    // GIVEN an non-organisation team
    var team = new Team(UUID.randomUUID());
    team.setTeamType(nonOrganisationTeamType);

    when(teamManagementService.getTeam(team.getId()))
        .thenReturn(Optional.of(team));

    // AND the invoking user is not a direct member
    when(teamManagementService.isMemberOfTeam(team, invokingUser.wuaId()))
        .thenReturn(false);

    // WHEN the invoking user has the CREATE_MANAGE_ANY_ORGANISATION_TEAM in the regulator team
    when(teamManagementService.userCanManageAnyOrganisationTeam(invokingUser.wuaId()))
        .thenReturn(true);

    // THEN the invoking user will not be able to view the team
    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(team.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderTeamMemberList_whenIsMemberOfTeamAndTeamManager_thenAssetModelProperties() throws Exception {

    when(teamManagementService.canManageTeam(regTeam, invokingUser.wuaId()))
        .thenReturn(true);

    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.isMemberOfTeam(regTeam, invokingUser.wuaId()))
        .thenReturn(true);

    when(teamManagementService.getTeamMemberViewsForTeam(regTeam))
        .thenReturn(List.of(regTeamMemberView));

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(regTeam.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/teammanagement/teamMembers"))
        .andExpect(model().attribute("teamName", regTeam.getName()))
        .andExpect(model().attribute("teamMemberViews", List.of(regTeamMemberView)))
        .andExpect(model().attribute("canManageTeam", true))
        .andExpect(model().attribute(
            "addMemberUrl",
            ReverseRouter.route(on(TeamManagementController.class).renderAddMemberToTeam(regTeam.getId(), null))
        ))
        .andExpect(model().attribute("rolesInTeam", regTeam.getTeamType().getAllowedRoles()));
  }

  @Test
  void renderTeamMemberList_whenIsMemberOfTeamAndNotTeamManager_thenAssetModelProperties() throws Exception {

    when(teamManagementService.canManageTeam(regTeam, invokingUser.wuaId()))
        .thenReturn(false);

    when(teamManagementService.isMemberOfTeam(regTeam, invokingUser.wuaId()))
        .thenReturn(true);

    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getTeamMemberViewsForTeam(regTeam))
        .thenReturn(List.of(regTeamMemberView));

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(regTeam.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/teammanagement/teamMembers"))
        .andExpect(model().attribute("teamName", regTeam.getName()))
        .andExpect(model().attribute("teamMemberViews", List.of(regTeamMemberView)))
        .andExpect(model().attribute("canManageTeam", false))
        .andExpect(model().attribute(
            "addMemberUrl",
            ReverseRouter.route(on(TeamManagementController.class).renderAddMemberToTeam(regTeam.getId(), null))
        ));
  }

  @Test
  void renderTeamMemberList_noTeamFound() throws Exception {
    var nonExistentTeamId = UUID.randomUUID();
    when(teamManagementService.getTeam(nonExistentTeamId))
        .thenReturn(Optional.empty());

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(nonExistentTeamId, null)))
        .with(user(invokingUser)))
        .andExpect(status().isNotFound());
  }

  @Test
  void renderTeamMemberList_noAccess() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.empty());

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(regTeam.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderAddMemberToTeam() throws Exception {
    when(teamManagementService.getTeam(organisationTeam.getId()))
        .thenReturn(Optional.of(organisationTeam));

    when(teamManagementService.getScopedTeamsOfTypeUserCanManage(TeamType.INDUSTRY, invokingUser.wuaId()))
        .thenReturn(List.of(organisationTeam));

    when(energyPortalConfiguration.registrationUrl())
        .thenReturn("https://example.com");

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderAddMemberToTeam(organisationTeam.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andReturn().getModelAndView();

    var registerUrl = (String) modelAndView.getModel().get("registerUrl");

    assertThat(registerUrl)
        .isEqualTo("https://example.com");
  }

  @Test
  void renderAddMemberToTeam_noTeamFound() throws Exception {
    var nonExistentTeamId = UUID.randomUUID();
    when(teamManagementService.getTeam(nonExistentTeamId))
        .thenReturn(Optional.empty());

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderAddMemberToTeam(nonExistentTeamId, null)))
        .with(user(invokingUser)))
        .andExpect(status().isNotFound());
  }

  @Test
  void renderAddMemberToTeam_noAccess() throws Exception {
    when(teamManagementService.getTeam(organisationTeam.getId()))
        .thenReturn(Optional.of(organisationTeam));

    when(teamManagementService.getScopedTeamsOfTypeUserCanManage(TeamType.INDUSTRY, invokingUser.wuaId()))
        .thenReturn(List.of());

    when(energyPortalConfiguration.registrationUrl())
        .thenReturn("https://example.com");

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderAddMemberToTeam(organisationTeam.getId(), null)))
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void handleAddMemberToTeam() throws Exception {
    var epaUser = new uk.co.fivium.energyportalapi.generated.types.User.Builder()
        .webUserAccountId(999L)
        .isAccountShared(false)
        .canLogin(true)
        .build();

    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.of(regTeam));

    when(addMemberFormValidator.isValid(any(), any()))
        .thenReturn(true);

    when(teamManagementService.getEnergyPortalUser("foo"))
        .thenReturn(List.of(epaUser));

    mockMvc.perform(post(ReverseRouter.route(on(TeamManagementController.class).handleAddMemberToTeam(regTeam.getId(), null, null)))
        .with(csrf())
        .with(user(invokingUser))
        .param("username", "foo"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TeamManagementController.class).renderUserTeamRoles(regTeam.getId(), 999L, null))));
  }

  @Test
  void handleAddMemberToTeam_invalidForm() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.of(regTeam));

    when(addMemberFormValidator.isValid(any(), any()))
        .thenReturn(false);

    when(energyPortalConfiguration.registrationUrl())
        .thenReturn("https://example.com");

    mockMvc.perform(post(ReverseRouter.route(on(TeamManagementController.class).handleAddMemberToTeam(regTeam.getId(), null, null)))
        .with(csrf())
        .with(user(invokingUser)))
        .andExpect(status().isOk()); // No redirect to next page
  }

  @Test
  void handleAddMemberToTeam_invalidUser() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.of(regTeam));

    when(addMemberFormValidator.isValid(any(), any()))
        .thenReturn(true);

    when(teamManagementService.getEnergyPortalUser("foo"))
        .thenReturn(List.of());

    mockMvc.perform(post(ReverseRouter.route(on(TeamManagementController.class).handleAddMemberToTeam(regTeam.getId(), null, null)))
        .with(csrf())
        .with(user(invokingUser))
        .param("username", "foo"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void handleAddMemberToTeam_noAccess() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.empty());

    mockMvc.perform(post(ReverseRouter.route(on(TeamManagementController.class).handleAddMemberToTeam(regTeam.getId(), null, null)))
        .with(csrf())
        .with(user(invokingUser))
        .param("username", "foo"))
        .andExpect(status().isForbidden());
  }

  @Test
  void renderUserTeamRoles_regulatorTeam() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getTeamMemberView(regTeam, 999L))
        .thenReturn(regTeamMemberView);

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderUserTeamRoles(regTeam.getId(), 999L, null)))
            .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andReturn().getModelAndView();

    var roleMap = (Map<String, String>) modelAndView.getModel().get("rolesNamesMap");

    assertThat(roleMap)
        .containsExactly(
            Map.entry(Role.MANAGE_TEAM.name(), Role.MANAGE_TEAM.getName()),
            Map.entry(Role.REGULATOR_MANAGE_ALL_ORGANISATIONS.name(), Role.REGULATOR_MANAGE_ALL_ORGANISATIONS.getName()),
            Map.entry(Role.EXPLORATION.name(), Role.EXPLORATION.getName()),
            Map.entry(Role.VIEW_ANY_RESOURCE.name(), Role.VIEW_ANY_RESOURCE.getName()),
            Map.entry(Role.RESERVES_AND_RESOURCES_ADVISOR.name(), Role.RESERVES_AND_RESOURCES_ADVISOR.getName()),
            Map.entry(Role.PROJECT_ANALYST.name(), Role.PROJECT_ANALYST.getName())
        );

    var teamMemberViewModel = (TeamMemberView) modelAndView.getModel().get("teamMemberView");
    assertThat(teamMemberViewModel).isEqualTo(regTeamMemberView);

    var rolesInTeam = (List<Role>) modelAndView.getModel().get("rolesInTeam");

    assertThat(rolesInTeam)
        .containsExactly(
            Role.MANAGE_TEAM,
            Role.REGULATOR_MANAGE_ALL_ORGANISATIONS,
            Role.EXPLORATION,
            Role.VIEW_ANY_RESOURCE,
            Role.RESERVES_AND_RESOURCES_ADVISOR,
            Role.PROJECT_ANALYST
        );
  }

  @Test
  void renderUserTeamRoles_areaTeam() throws Exception {
    when(teamManagementService.getTeam(areaTeam.getId()))
        .thenReturn(Optional.of(areaTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(areaTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.of(areaTeam));

    when(teamManagementService.getTeamMemberView(areaTeam, 999L))
        .thenReturn(areaTeamMemberView);

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderUserTeamRoles(areaTeam.getId(), 999L, null)))
            .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andReturn().getModelAndView();

    var roleMap = (Map<String, String>) modelAndView.getModel().get("rolesNamesMap");

    assertThat(roleMap)
        .containsExactly(
            Map.entry(Role.MANAGE_TEAM.name(), Role.MANAGE_TEAM.getName()),
            Map.entry(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM.name(), Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM.getName())
        );

    var teamMemberViewModel = (TeamMemberView) modelAndView.getModel().get("teamMemberView");
    assertThat(teamMemberViewModel).isEqualTo(areaTeamMemberView);

    var rolesInTeam = (List<Role>) modelAndView.getModel().get("rolesInTeam");

    assertThat(rolesInTeam)
        .containsExactly(
            Role.MANAGE_TEAM,
            Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM
        );
  }

  @Test
  void renderUserTeamRoles_industryTeam() throws Exception {
    when(teamManagementService.getTeam(organisationTeam.getId()))
        .thenReturn(Optional.of(organisationTeam));

    when(teamManagementService.getScopedTeamsOfTypeUserCanManage(organisationTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(List.of(organisationTeam));

    when(teamManagementService.getTeamMemberView(organisationTeam, 999L))
        .thenReturn(orgTeamMemberView);

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderUserTeamRoles(organisationTeam.getId(), 999L, null)))
            .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andReturn().getModelAndView();

    var roleMap = (Map<String, String>) modelAndView.getModel().get("rolesNamesMap");

    assertThat(roleMap)
        .containsExactly(
            Map.entry(Role.MANAGE_TEAM.name(), Role.MANAGE_TEAM.getName()),
            Map.entry(Role.INDUSTRY_SUBMIT.name(), Role.INDUSTRY_SUBMIT.getName()),
            Map.entry(Role.INDUSTRY_VIEWER.name(), Role.INDUSTRY_VIEWER.getName())
        );

    var teamMemberViewModel = (TeamMemberView) modelAndView.getModel().get("teamMemberView");
    assertThat(teamMemberViewModel).isEqualTo(orgTeamMemberView);

    var rolesInTeam = (List<Role>) modelAndView.getModel().get("rolesInTeam");

    assertThat(rolesInTeam)
        .containsExactly(
            Role.MANAGE_TEAM,
            Role.INDUSTRY_SUBMIT,
            Role.INDUSTRY_VIEWER
        );
  }

  @Test
  void renderUserTeamRoles_noAccess() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.empty());


    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderUserTeamRoles(regTeam.getId(), 999L, null)))
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateUserTeamRoles() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.of(regTeam));

    when(memberRolesFormValidator.isValid(any(), eq(999L), eq(regTeam), any()))
        .thenReturn(true);

    mockMvc.perform(post(ReverseRouter.route(on(TeamManagementController.class).updateUserTeamRoles(regTeam.getId(), 999L, null, null)))
        .with(csrf())
        .with(user(invokingUser))
        .param("roles", "MANAGE_TEAM"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(regTeam.getId(), null))));

    verify(teamManagementService).setUserTeamRoles(999L, regTeam, List.of(Role.MANAGE_TEAM));
  }

  @Test
  void updateUserTeamRoles_invalidForm() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.of(regTeam));

    when(memberRolesFormValidator.isValid(any(), eq(999L), eq(regTeam), any()))
        .thenReturn(false);

    when(teamManagementService.getTeamMemberView(regTeam, 999L))
        .thenReturn(regTeamMemberView);

    mockMvc.perform(post(ReverseRouter.route(on(TeamManagementController.class).updateUserTeamRoles(regTeam.getId(), 999L, null, null)))
        .with(csrf())
        .with(user(invokingUser))
        .param("roles", "MANAGE_TEAM"))
        .andExpect(status().isOk()); // No redirect to next page

    verify(teamManagementService, never()).setUserTeamRoles(any(), any(), any());
  }

  @Test
  void updateUserTeamRoles_noAccess() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.empty());

    mockMvc.perform(post(ReverseRouter.route(on(TeamManagementController.class).updateUserTeamRoles(regTeam.getId(), 999L, null, null)))
        .with(csrf())
        .with(user(invokingUser))
        .param("roles", "MANAGE_TEAM"))
        .andExpect(status().isForbidden());

    verify(teamManagementService, never()).setUserTeamRoles(any(), any(), any());
  }

  @Test
  void renderRemoveTeamMember() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getTeamMemberView(regTeam, 999L))
        .thenReturn(regTeamMemberView);

    when(teamManagementService.willManageTeamRoleBePresentAfterMemberRemoval(regTeam, 999L))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderRemoveTeamMember(regTeam.getId(), 999L)))
        .with(user(invokingUser)))
        .andExpect(status().isOk())
        .andReturn().getModelAndView();

    var teamMemberViewModel = (TeamMemberView) modelAndView.getModel().get("teamMemberView");
    var teamName = (String) modelAndView.getModel().get("teamName");
    var canRemoveTeamMember = (boolean) modelAndView.getModel().get("canRemoveTeamMember");

    assertThat(teamMemberViewModel).isEqualTo(regTeamMemberView);
    assertThat(teamName).isEqualTo(regTeam.getName());
    assertThat(canRemoveTeamMember).isTrue();
  }

  @Test
  void renderRemoveTeamMember_noAccess() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.empty());

    mockMvc.perform(get(ReverseRouter.route(on(TeamManagementController.class).renderRemoveTeamMember(regTeam.getId(), 999L)))
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  void handleRemoveTeamMember() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.of(regTeam));

    mockMvc.perform(post(ReverseRouter.route(on(TeamManagementController.class).handleRemoveTeamMember(regTeam.getId(), 999L)))
        .with(csrf())
        .with(user(invokingUser)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TeamManagementController.class).renderTeamMemberList(regTeam.getId(), null))));

    verify(teamManagementService).removeUserFromTeam(999L, regTeam);
  }

  @Test
  void handleRemoveTeamMember_noAccess() throws Exception {
    when(teamManagementService.getTeam(regTeam.getId()))
        .thenReturn(Optional.of(regTeam));

    when(teamManagementService.getStaticTeamOfTypeUserCanManage(regTeam.getTeamType(), invokingUser.wuaId()))
        .thenReturn(Optional.empty());

    mockMvc.perform(post(ReverseRouter.route(on(TeamManagementController.class).handleRemoveTeamMember(regTeam.getId(), 999L)))
        .with(csrf())
        .with(user(invokingUser)))
        .andExpect(status().isForbidden());

    verify(teamManagementService, never()).removeUserFromTeam(999L, regTeam);
  }

}