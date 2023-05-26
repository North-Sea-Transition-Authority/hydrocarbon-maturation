package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.offshore;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaEditMemberControllerHelperService;

@ContextConfiguration(classes = OffshoreAreaEditMemberController.class)
class OffshoreAreaEditMemberControllerTest extends AbstractControllerTest {

  @MockBean
  private OffshoreAreaTeamService offshoreAreaTeamService;

  @MockBean
  private TeamMemberRoleService teamMemberRoleService;

  @MockBean
  private OffshoreAreaTeamMemberEditRolesValidator offshoreAreaTeamMemberEditRolesValidator;

  @MockBean
  private AreaEditMemberControllerHelperService areaEditMemberControllerHelperService;

  private Team areaTeam;
  private TeamView teamView;
  private ServiceUserDetail accessManager;
  private ServiceUserDetail nonAccessManager;

  private static final Map<String, String> ROLE_MAP = DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(OffshoreAreaTeamRole.class);


  @BeforeEach
  void setUp() {
    areaTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.OFFSHORE_AREA)
        .build();
    teamView = TeamTestUtil.createTeamView(areaTeam);
    accessManager = ServiceUserDetailTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
    nonAccessManager = ServiceUserDetailTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
  }

  @SecurityTest
  void renderEditMember_whenAccessManager_thenOk() throws Exception {

    Set<TeamRole> userRoles = Set.of(OffshoreAreaTeamRole.OFFSHORE_AREA_MANAGE_TEAM);

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamView.teamId(), accessManager,
        Set.of(OffshoreAreaTeamRole.OFFSHORE_AREA_MANAGE_TEAM.name()))
    ).thenReturn(true);

    when(offshoreAreaTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(areaTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.OFFSHORE_AREA)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    var modelAndViewOk = new ModelAndView();
    modelAndViewOk.setStatus(HttpStatus.OK);
    when(areaEditMemberControllerHelperService.renderEditMember(areaTeam, teamMember.wuaId(), ROLE_MAP,
        ReverseRouter.route(on(OffshoreAreaTeamManagementController.class).renderMemberList(teamView.teamId()))))
        .thenReturn(modelAndViewOk);

    mockMvc.perform(get(ReverseRouter.route(on(OffshoreAreaEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk());

  }

  @SecurityTest
  void renderEditMember_whenNotAccessManager_andForbidden() throws Exception {
    Set<TeamRole> userRoles = Set.of(OffshoreAreaTeamRole.OFFSHORE_AREA_TEAM_MEMBER);

    when(offshoreAreaTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(areaTeam));

    when(offshoreAreaTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(areaTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.OFFSHORE_AREA)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(nonAccessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), nonAccessManager)).thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(on(OffshoreAreaEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(nonAccessManager)))
        .andExpect(status().isForbidden());
  }
}