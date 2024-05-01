package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = HasLegacyTeamPermissionInterceptorTest.TestController.class)
class HasLegacyTeamPermissionInterceptorTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @SecurityTest
  void noSecurity() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(TestController.class).noSecurity()))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void noTeamIdOnEndpoint() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(TestController.class).noTeamIdOnEndpoint()))
            .with(user(USER)))
        .andExpect(status().isBadRequest());
  }

  @SecurityTest
  void noPermissionsSpecified_whenIsMemberOfTeam_thenBadRequest() throws Exception {
    var team = TeamTestUtil.Builder().build();
    when(teamMemberService.isMemberOfTeam(team.toTeamId(), USER)).thenReturn(true);
    mockMvc.perform(get(ReverseRouter.route(on(TestController.class).noPermissionsSpecified(team.toTeamId())))
            .with(user(USER)))
        .andExpect(status().isBadRequest());
  }

  @SecurityTest
  void noPermissionsSpecified_whenNotMemberOfTeam_thenBadRequest() throws Exception {
    var team = TeamTestUtil.Builder().build();
    when(teamMemberService.isMemberOfTeam(team.toTeamId(), USER)).thenReturn(false);
    mockMvc.perform(get(ReverseRouter.route(on(TestController.class).noPermissionsSpecified(team.toTeamId())))
            .with(user(USER)))
        .andExpect(status().isBadRequest());
  }

  @SecurityTest
  void teamPermissions_whenHasMatchingPermission_thenOk() throws Exception {
    var team = TeamTestUtil.Builder().build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(IndustryTeamRole.ACCESS_MANAGER)
        .build();
    when(teamMemberService.isMemberOfTeam(team.toTeamId(), USER)).thenReturn(true);
    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(List.of(teamMember));
    mockMvc.perform(get(ReverseRouter.route(on(TestController.class).teamPermissions(team.toTeamId())))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void teamPermissions_whenNoMatchingPermission_thenForbidden() throws Exception {
    var team = TeamTestUtil.Builder().build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(RegulatorTeamRole.VIEWER)
        .build();
    when(teamMemberService.isMemberOfTeam(team.toTeamId(), USER)).thenReturn(true);
    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(List.of(teamMember));
    mockMvc.perform(get(ReverseRouter.route(on(TestController.class).teamPermissions(team.toTeamId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void anyNonTeamPermissions_whenHasMatchingPermission_thenOk() throws Exception {
    var team = TeamTestUtil.Builder().build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)
        .build();
    when(teamMemberService.isMemberOfTeam(team.toTeamId(), USER)).thenReturn(false);
    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(List.of(teamMember));
    mockMvc.perform(get(ReverseRouter.route(on(TestController.class).anyNonTeamPermissions(team.toTeamId())))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void anyNonTeamPermissions_whenNoMatchingPermission_thenForbidden() throws Exception {
    var team = TeamTestUtil.Builder().build();
    var teamMember = TeamMemberTestUtil.Builder()
        .withTeamId(team.toTeamId())
        .withRole(RegulatorTeamRole.VIEWER)
        .build();
    when(teamMemberService.isMemberOfTeam(team.toTeamId(), USER)).thenReturn(false);
    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(List.of(teamMember));
    mockMvc.perform(get(ReverseRouter.route(on(TestController.class).anyNonTeamPermissions(team.toTeamId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @RequestMapping("/permission-management/test")
  @Controller
  static class TestController {

    public TestController() {
    }

    private static final String VIEW_NAME = "test-view";

    @GetMapping("/no-security")
    public ModelAndView noSecurity() {
      return new ModelAndView(VIEW_NAME);
    }

    @GetMapping("/no-team-id")
    @HasTeamPermission
    public ModelAndView noTeamIdOnEndpoint() {
      return new ModelAndView(VIEW_NAME);
    }

    @GetMapping("/no-permissions-specified/{teamId}")
    @HasTeamPermission
    public ModelAndView noPermissionsSpecified(@PathVariable("teamId") TeamId teamId) {
      return new ModelAndView(VIEW_NAME);
    }

    @GetMapping("/team-permissions/{teamId}")
    @HasTeamPermission(anyTeamPermissionOf = RolePermission.INDUSTRY_MANAGE_ORGANISATION)
    public ModelAndView teamPermissions(@PathVariable("teamId") TeamId teamId) {
      return new ModelAndView(VIEW_NAME);
    }

    @GetMapping("/non-team-permissions/{teamId}")
    @HasTeamPermission(anyNonTeamPermissionOf = RolePermission.REGULATOR_MANAGE_ALL_ORGANISATIONS)
    public ModelAndView anyNonTeamPermissions(@PathVariable("teamId") TeamId teamId) {
      return new ModelAndView(VIEW_NAME);
    }
  }

}
