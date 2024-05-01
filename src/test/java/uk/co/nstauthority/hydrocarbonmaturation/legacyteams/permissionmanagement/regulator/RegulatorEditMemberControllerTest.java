package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator;

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
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamMemberRolesForm;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RegulatorEditMemberController.class})
class RegulatorEditMemberControllerTest extends AbstractControllerTest {

  @MockBean
  private TeamMemberViewService teamMemberViewService;

  @MockBean
  RegulatorTeamMemberEditService regulatorTeamMemberEditService;

  @MockBean
  RegulatorTeamMemberEditRolesValidator regulatorTeamMemberEditRolesValidator;

  private LegacyTeam regulatorLegacyTeam;
  private TeamView teamView;
  private User accessManager;
  private User nonAccessManager;

  @BeforeEach
  void setUp() {
    regulatorLegacyTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    teamView = TeamTestUtil.createTeamView(regulatorLegacyTeam);
    accessManager = UserTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
    nonAccessManager = UserTestUtil.Builder()
        .withWuaId(new Random().nextLong())
        .build();
  }

  @SecurityTest
  void renderEditMember_whenNotAuthorised_thenIsUnauthorized() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(RegulatorEditMemberController.class)
            .renderEditMember(teamView.teamId(), new WebUserAccountId(accessManager.wuaId())))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void renderEditMember_whenNotAccessManager_thenForbidden() throws Exception {

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamView.teamId(), nonAccessManager,
        Set.of(RegulatorTeamRole.ACCESS_MANAGER.name()))
    ).thenReturn(false);

    mockMvc.perform(get(ReverseRouter.route(on(RegulatorEditMemberController.class)
            .renderEditMember(teamView.teamId(), new WebUserAccountId(nonAccessManager.wuaId()))))
            .with(user(nonAccessManager)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void renderEditMember_whenAccessManager_thenOk() throws Exception {
    makeValidRenderEditMemberRequest(Set.of(RegulatorTeamRole.ACCESS_MANAGER));
  }

  @Test
  void renderEditMember_whenAccessManager_andOk_thenAssertModelProperties() throws Exception {

    Set<TeamRole> userRoles = Set.of(
        RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER,
        RegulatorTeamRole.ACCESS_MANAGER);

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
            DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(RegulatorTeamRole.class),
            ReverseRouter.route(on(RegulatorTeamManagementController.class).renderMemberList(teamView.teamId()))
        );
  }

  private MvcResult makeValidRenderEditMemberRequest(Set<TeamRole> teamMemberRoles) throws Exception {

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamView.teamId(), accessManager,
        Set.of(RegulatorTeamRole.ACCESS_MANAGER.name()))
    ).thenReturn(true);

    when(regulatorTeamService.getTeam(teamView.teamId())).thenReturn(Optional.of(regulatorLegacyTeam));

    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withTeamId(teamView.teamId())
        .withWebUserAccountId(accessManager.wuaId())
        .withRoles(teamMemberRoles)
        .build();

    when(teamMemberService.getTeamMember(regulatorLegacyTeam, teamMember.wuaId()))
        .thenReturn(Optional.of(teamMember));

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withRoles(teamMemberRoles)
        .withWebUserAccountId(teamMember.wuaId())
        .build();
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));

    return mockMvc.perform(get(ReverseRouter.route(on(RegulatorEditMemberController.class)
            .renderEditMember(teamView.teamId(), teamMember.wuaId())))
            .with(user(accessManager)))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  void editMember_whenNotAuthorized_thenIsForbidden() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(on(RegulatorEditMemberController.class)
            .renderEditMember(teamView.teamId(), new WebUserAccountId(accessManager.wuaId())))))
        .andExpect(status().isForbidden());
  }
}
