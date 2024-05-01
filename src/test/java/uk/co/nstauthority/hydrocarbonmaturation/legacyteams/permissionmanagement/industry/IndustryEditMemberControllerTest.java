package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
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
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamMemberRolesForm;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = IndustryEditMemberController.class)
class IndustryEditMemberControllerTest extends AbstractControllerTest {

  @MockBean
  private IndustryTeamService industryTeamService;

  @MockBean
  private TeamMemberViewService teamMemberViewService;

  @MockBean
  private TeamMemberRoleService teamMemberRoleService;

  @MockBean
  private IndustryTeamMemberEditRolesValidator industryTeamMemberEditRolesValidator;

  private LegacyTeam industryLegacyTeam;
  private TeamView teamView;
  private User accessManager;
  private User nonAccessManager;

  @BeforeEach
  void setUp() {
    industryLegacyTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    teamView = TeamTestUtil.createTeamView(industryLegacyTeam);
    accessManager = UserTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
    nonAccessManager = UserTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
  }

  @SecurityTest
  void renderEditMember_whenAccessManager_thenOk() throws Exception {

    Set<TeamRole> userRoles = Set.of(IndustryTeamRole.ACCESS_MANAGER);

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamView.teamId(), accessManager,
        Set.of(IndustryTeamRole.ACCESS_MANAGER.name()))
    ).thenReturn(true);

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    when(teamMemberService.getTeamMember(industryLegacyTeam, teamMember.wuaId()))
        .thenReturn(Optional.of(teamMember));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(userRoles)
        .withWebUserAccountId(teamMember.wuaId())
        .build();
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));

    mockMvc.perform(get(ReverseRouter.route(on(IndustryEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk());

  }

  @SecurityTest
  void renderEditMember_whenRegulatorIndustryAccessManager_thenOk() throws Exception {

    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER);

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    when(teamMemberService.getTeamMember(industryLegacyTeam, teamMember.wuaId()))
        .thenReturn(Optional.of(teamMember));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(userRoles)
        .withWebUserAccountId(teamMember.wuaId())
        .build();
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));

    mockMvc.perform(get(ReverseRouter.route(on(IndustryEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk());

  }

  @SecurityTest
  void renderEditMember_whenNotAccessManager_andForbidden() throws Exception {
    Set<TeamRole> userRoles = Set.of(IndustryTeamRole.VIEWER);

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryLegacyTeam));

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(nonAccessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), nonAccessManager)).thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(on(IndustryEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(nonAccessManager)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void renderEditMember_whenAccessManager_andOk_thenAssertModelProperties() throws Exception {

    Set<TeamRole> userRoles = Set.of(IndustryTeamRole.ACCESS_MANAGER);

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamView.teamId(), accessManager,
        Set.of(IndustryTeamRole.ACCESS_MANAGER.name()))
    ).thenReturn(true);

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    when(teamMemberService.getTeamMember(industryLegacyTeam, teamMember.wuaId()))
        .thenReturn(Optional.of(teamMember));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(userRoles)
        .withWebUserAccountId(teamMember.wuaId())
        .build();
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(on(IndustryEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();

    var model = modelAndView.getModelMap();

    var expectedRoles = userRoles.stream()
        .map(TeamRole::name)
        .collect(Collectors.toSet());

    assertThat((TeamMemberRolesForm) model.get("form")).extracting("roles")
        .isEqualTo(expectedRoles);

    assertThat(model).containsKeys("pageTitle")
        .extractingByKeys("roles", "backLinkUrl")
        .containsExactly(
            DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(IndustryTeamRole.class),
            ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamView.teamId()))
        );
  }

  @SecurityTest
  void editMember_whenValid_verifyCalls() throws Exception {

    Set<TeamRole> userRoles = Set.of(
        IndustryTeamRole.ACCESS_MANAGER
    );

    when(industryTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(industryLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    when(teamMemberService.getTeamMember(industryLegacyTeam, teamMember.wuaId()))
        .thenReturn(Optional.of(teamMember));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(userRoles)
        .withWebUserAccountId(teamMember.wuaId())
        .build();
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));

    mockMvc.perform(post(ReverseRouter.route(on(IndustryEditMemberController.class)
            .editMember(teamView.teamId(), new WebUserAccountId(accessManager.wuaId()), null, null)))
            .with(csrf())
            .with(user(accessManager))
            .param("roles", IndustryTeamRole.ACCESS_MANAGER.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(IndustryTeamManagementController.class).renderMemberList(teamView.teamId()))));

    verify(teamMemberRoleService).updateUserTeamRoles(industryLegacyTeam, teamMember.wuaId(),
        Set.of(IndustryTeamRole.ACCESS_MANAGER.name()));
  }
}
