package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.southernoffshore;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaRemoveMemberControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = SouthernOffshoreAreaRemoveMemberController.class)
class SouthernOffshoreAreaRemoveMemberControllerTest extends AbstractControllerTest {

  @MockBean
  private AreaRemoveMemberControllerHelperService areaRemoveMemberControllerHelperService;

  @MockBean
  private SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;

  private LegacyTeam areaLegacyTeam;
  private TeamView teamView;

  private User accessManager;
  private User nonAccessManager;

  @BeforeEach
  void setUp() {

    areaLegacyTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.SOUTHERN_OFFSHORE_AREA)
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
  void renderRemoveMember_whenNotAccessManager_thenForbidden() throws Exception {

    Set<TeamRole> userRoles = Set.of(SouthernOffshoreAreaTeamRole.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER);

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.SOUTHERN_OFFSHORE_AREA)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(nonAccessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(nonAccessManager)).thenReturn(List.of(teamMember));

    mockMvc.perform(get(ReverseRouter.route(on(SouthernOffshoreAreaRemoveMemberController.class)
        .renderRemoveMember(areaLegacyTeam.toTeamId(), teamMember.wuaId())))
        .with(user(nonAccessManager))
    ).andExpect(status().isForbidden());
  }

  @SecurityTest
  void renderRemoveMember_whenAccessManager_thenOk() throws Exception {

    Set<TeamRole> userRoles = Set.of(SouthernOffshoreAreaTeamRole.SOUTHERN_OFFSHORE_AREA_MANAGE_TEAM);

    when(southernOffshoreAreaTeamService.getTeam(teamView.teamId()))
        .thenReturn(Optional.of(areaLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.SOUTHERN_OFFSHORE_AREA)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(userRoles)
        .build();

    when(teamMemberService.getUserAsTeamMembers(accessManager)).thenReturn(List.of(teamMember));
    when(teamMemberService.isMemberOfTeam(teamView.teamId(), accessManager)).thenReturn(true);

    var modelAndViewOk = new ModelAndView();
    modelAndViewOk.setStatus(HttpStatus.OK);
    when(areaRemoveMemberControllerHelperService.renderRemoveMember(areaLegacyTeam, teamMember.wuaId(),
        ReverseRouter.redirect(on(SouthernOffshoreAreaTeamManagementController.class).renderMemberList(areaLegacyTeam.toTeamId())),
        ReverseRouter.route(on(SouthernOffshoreAreaTeamManagementController.class).renderMemberList(areaLegacyTeam.toTeamId())),
        ReverseRouter.route(on(SouthernOffshoreAreaRemoveMemberController.class).removeMember(areaLegacyTeam.toTeamId(), teamMember.wuaId(), null))))
        .thenReturn(modelAndViewOk);

    mockMvc.perform(get(ReverseRouter.route(on(SouthernOffshoreAreaRemoveMemberController.class)
            .renderRemoveMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk());
  }
}