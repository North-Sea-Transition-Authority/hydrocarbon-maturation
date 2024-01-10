package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RegulatorRemoveMemberController.class})
class RegulatorRemoveMemberControllerTest extends AbstractControllerTest {

  private static final RegulatorTeamRole ACCESS_MANAGER_ROLE = RegulatorTeamRole.ACCESS_MANAGER;

  @MockBean
  private TeamMemberViewService teamMemberViewService;

  @MockBean
  private TeamMemberRemovalService regulatorTeamMemberRemovalService;

  @Autowired
  private ApplicationContext applicationContext;

  private Team regulatorTeam;
  private WebUserAccountId wuaId;

  private User loggedInUser;

  @BeforeEach
  void setUp() {

    regulatorTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();

    wuaId = new WebUserAccountId(Math.abs(new Random().nextLong()));

    loggedInUser = UserTestUtil.Builder()
        .withWuaId(wuaId.id())
        .build();
  }

  @SecurityTest
  void renderRemoveMember_whenNotLoggedIn_thenNotAuthorized() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(RegulatorRemoveMemberController.class)
        .renderRemoveMember(regulatorTeam.toTeamId(), wuaId)))
    ).andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void renderRemoveMember_whenNotAccessManager_thenForbidden() throws Exception {

    var teamId = regulatorTeam.toTeamId();

    // Interceptor setup
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(
        teamId,
        loggedInUser,
        Set.of(ACCESS_MANAGER_ROLE.name()))
    ).thenReturn(false);

    when(regulatorTeamService.getRegulatorTeamForUser(loggedInUser)).thenReturn(Optional.empty());

    mockMvc.perform(get(ReverseRouter.route(on(RegulatorRemoveMemberController.class)
        .renderRemoveMember(regulatorTeam.toTeamId(), wuaId)))
        .with(user(loggedInUser))
    ).andExpect(status().isForbidden());
  }

  @Test
  void renderRemoveMember_whenAccessManagerAndUserCanBeRemoved_thenOk() throws Exception {

    var teamId = regulatorTeam.toTeamId();

    // Interceptor setup
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(
        teamId,
        loggedInUser,
        Set.of(ACCESS_MANAGER_ROLE.name()))
    ).thenReturn(true);

    // Endpoint setup
    var teamMember = TeamMemberTestUtil.Builder().build();
    var teamMemberView = TeamMemberViewTestUtil.Builder().build();

    var canRemoveTeamMember = true;

    var teamName = applicationContext.getBean(CustomerConfigurationProperties.class).mnemonic();

    when(regulatorTeamService.getTeam(regulatorTeam.toTeamId())).thenReturn(Optional.of(regulatorTeam));
    when(teamMemberService.getTeamMember(regulatorTeam, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(regulatorTeamMemberRemovalService.canRemoveTeamMember(regulatorTeam, teamMember))
        .thenReturn(canRemoveTeamMember);

    mockMvc.perform(get(ReverseRouter.route(on(RegulatorRemoveMemberController.class)
            .renderRemoveMember(teamId, wuaId)))
            .with(user(loggedInUser)))
        .andExpect(status().isOk())
        .andExpect(model().attribute("teamName", teamName))
        .andExpect(model().attribute("teamMember", teamMemberView))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(RegulatorTeamManagementController.class)
                .renderMemberList(regulatorTeam.toTeamId()))
        ))
        .andExpect(model().attribute(
            "removeUrl",
            ReverseRouter.route(on(RegulatorRemoveMemberController.class)
                .removeMember(regulatorTeam.toTeamId(), wuaId, null))
        ))
        .andExpect(model().attribute("canRemoveTeamMember", canRemoveTeamMember))
        .andExpect(model().attribute(
            "pageTitle",
            "Are you sure you want to remove %s from %s?".formatted(teamMemberView.getDisplayName(), teamName)
        ))
        .andReturn()
        .getModelAndView();
  }

  @Test
  void renderRemoveMember_whenLastAccessManager_thenOk() throws Exception {

    var teamId = regulatorTeam.toTeamId();

    // Interceptor setup
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(
        teamId,
        loggedInUser,
        Set.of(ACCESS_MANAGER_ROLE.name()))
    ).thenReturn(true);

    // Endpoint setup
    var teamMember = TeamMemberTestUtil.Builder().build();
    var teamMemberView = TeamMemberViewTestUtil.Builder().build();

    var canRemoveTeamMember = false;

    var teamName = applicationContext.getBean(CustomerConfigurationProperties.class).mnemonic();

    when(regulatorTeamService.getTeam(regulatorTeam.toTeamId())).thenReturn(Optional.of(regulatorTeam));
    when(teamMemberService.getTeamMember(regulatorTeam, wuaId)).thenReturn(Optional.of(teamMember));
    when(teamMemberViewService.getTeamMemberView(teamMember)).thenReturn(Optional.of(teamMemberView));
    when(regulatorTeamMemberRemovalService.canRemoveTeamMember(regulatorTeam, teamMember))
        .thenReturn(canRemoveTeamMember);

    mockMvc.perform(get(ReverseRouter.route(on(RegulatorRemoveMemberController.class)
            .renderRemoveMember(teamId, wuaId)))
            .with(user(loggedInUser)))
        .andExpect(status().isOk())
        .andExpect(model().attribute("teamName", teamName))
        .andExpect(model().attribute("teamMember", teamMemberView))
        .andExpect(model().attribute(
            "backLinkUrl",
            ReverseRouter.route(on(RegulatorTeamManagementController.class)
                .renderMemberList(regulatorTeam.toTeamId()))
        ))
        .andExpect(model().attribute(
            "removeUrl",
            ReverseRouter.route(on(RegulatorRemoveMemberController.class)
                .removeMember(regulatorTeam.toTeamId(), wuaId, null))
        ))
        .andExpect(model().attribute("canRemoveTeamMember", canRemoveTeamMember))
        .andExpect(model().attribute(
            "pageTitle",
            "You are unable to remove %s from %s".formatted(teamMemberView.getDisplayName(), teamName)
        ))
        .andReturn()
        .getModelAndView();
  }
}
