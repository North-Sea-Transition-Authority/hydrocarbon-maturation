package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberRolesForm;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IndustryEditMemberController.class})
class IndustryEditMemberControllerTest extends AbstractControllerTest {

  @MockBean
  private IndustryTeamService industryTeamService;

  @MockBean
  private TeamMemberViewService teamMemberViewService;

  @MockBean
  private TeamMemberRoleService teamMemberRoleService;

  @MockBean
  private TeamService teamService;

  @MockBean
  IndustryTeamMemberEditRolesValidator industryTeamMemberEditRolesValidator;

  private Team industryTeam;
  private TeamView teamView;
  private ServiceUserDetail accessManager;
  private ServiceUserDetail nonAccessManager;

  @BeforeEach
  void setUp() {
    industryTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(UUID.randomUUID())
        .build();
    teamView = TeamTestUtil.createTeamView(industryTeam);
    accessManager = ServiceUserDetailTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
    nonAccessManager = ServiceUserDetailTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
  }

  @SecurityTest
  void renderEditMember_whenNotAuthorised_thenIsUnauthorized() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(IndustryEditMemberController.class)
            .renderEditMember(teamView.teamId(), new WebUserAccountId(accessManager.wuaId())))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  @Disabled("Disabled until HM-12 is done")
  void renderEditMember_whenNotAccessManager_thenForbidden() throws Exception {

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamView.teamId(), nonAccessManager,
        Set.of(IndustryTeamRole.ACCESS_MANAGER.name()))
    ).thenReturn(false);

    mockMvc.perform(get(ReverseRouter.route(on(IndustryEditMemberController.class)
            .renderEditMember(teamView.teamId(), new WebUserAccountId(nonAccessManager.wuaId()))))
            .with(user(nonAccessManager)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void renderEditMember_whenAccessManager_thenOk() throws Exception {
    makeValidRenderEditMemberRequest(Set.of(IndustryTeamRole.ACCESS_MANAGER));
  }

  @Test
  void renderEditMember_whenAccessManager_andOk_thenAssertModelProperties() throws Exception {

    Set<TeamRole> userRoles = Set.of(IndustryTeamRole.ACCESS_MANAGER);

    var modelAndView = makeValidRenderEditMemberRequest(userRoles)
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

  private MvcResult makeValidRenderEditMemberRequest(Set<TeamRole> teamMemberRoles) throws Exception {

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamView.teamId(), accessManager,
        Set.of(IndustryTeamRole.ACCESS_MANAGER.name()))
    ).thenReturn(true);

    when(industryTeamService.getTeam(teamView.teamId())).thenReturn(Optional.of(industryTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(teamMemberRoles)
        .build();

    when(teamMemberService.getTeamMember(industryTeam, teamMember.wuaId()))
        .thenReturn(Optional.of(teamMember));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(teamMemberRoles)
        .withWebUserAccountId(teamMember.wuaId())
        .build();
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));

    return mockMvc.perform(get(ReverseRouter.route(on(IndustryEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  void editMember_whenNotAuthorized_thenIsForbidden() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(on(IndustryEditMemberController.class)
            .renderEditMember(teamView.teamId(), new WebUserAccountId(accessManager.wuaId())))))
        .andExpect(status().isForbidden());
  }
}
