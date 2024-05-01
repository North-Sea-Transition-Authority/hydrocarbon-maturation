package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore;

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
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaEditMemberControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = OnshoreAreaEditMemberController.class)
class OnshoreAreaEditMemberControllerTest extends AbstractControllerTest {

  @MockBean
  private OnshoreAreaTeamService onshoreAreaTeamService;

  @MockBean
  private TeamMemberRoleService teamMemberRoleService;

  @MockBean
  private OnshoreAreaTeamMemberEditRolesValidator onshoreAreaTeamMemberEditRolesValidator;

  @MockBean
  private AreaEditMemberControllerHelperService areaEditMemberControllerHelperService;

  private LegacyTeam areaLegacyTeam;
  private TeamView teamView;
  private User accessManager;
  private User nonAccessManager;

  private static final Map<String, String> ROLE_MAP = DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(OnshoreAreaTeamRole.class);

  @BeforeEach
  void setUp() {
    areaLegacyTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .build();
    teamView = TeamTestUtil.createTeamView(areaLegacyTeam);
    accessManager = UserTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
    nonAccessManager = UserTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
  }

  @SecurityTest
  void renderEditMember_whenAccessManager_thenOk() throws Exception {

    Set<TeamRole> userRoles = Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM);

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamView.teamId(), accessManager,
        Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM.name()))
    ).thenReturn(true);

    when(onshoreAreaTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(areaLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    var modelAndViewOk = new ModelAndView();
    modelAndViewOk.setStatus(HttpStatus.OK);
    when(areaEditMemberControllerHelperService.renderEditMember(areaLegacyTeam, teamMember.wuaId(), ROLE_MAP,
        ReverseRouter.route(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamView.teamId()))))
        .thenReturn(modelAndViewOk);

    mockMvc.perform(get(ReverseRouter.route(on(OnshoreAreaEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk());

  }

  @SecurityTest
  void renderEditMember_whenNotAccessManager_andForbidden() throws Exception {
    Set<TeamRole> userRoles = Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER);

    when(onshoreAreaTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(areaLegacyTeam));

    when(onshoreAreaTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(areaLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(nonAccessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), nonAccessManager)).thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(on(OnshoreAreaEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(nonAccessManager)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void editMember_whenValid_verifyCalls() throws Exception {

    Set<TeamRole> userRoles = Set.of(
        OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM
    );

    when(onshoreAreaTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(areaLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    when(areaEditMemberControllerHelperService.getTeamMemberOrThrow(areaLegacyTeam, teamMember.wuaId())).thenReturn(teamMember);

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(userRoles)
        .withWebUserAccountId(teamMember.wuaId())
        .build();
    when(areaEditMemberControllerHelperService.getTeamMemberViewOrThrow(teamMember)).thenReturn(teamMemberView);

    mockMvc.perform(post(ReverseRouter.route(on(OnshoreAreaEditMemberController.class)
            .editMember(teamView.teamId(), new WebUserAccountId(accessManager.wuaId()), null, null)))
            .with(csrf())
            .with(user(accessManager))
            .param("roles", OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamView.teamId()))));

    verify(teamMemberRoleService).updateUserTeamRoles(areaLegacyTeam, teamMember.wuaId(),
        Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM.name()));
  }
}