package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = HasPermissionInterceptorTest.TestController.class)
class HasPermissionInterceptorTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Autowired
  private PermissionService permissionService;

  @Test
  void preHandle_whenMethodHasNoSupportedAnnotations_thenOkResponse() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(HasPermissionInterceptorTest.TestController.class)
            .noSupportedAnnotations()
        ))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void preHandle_whenTeamMembershipIsNull_thenForbidden() throws Exception {

    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(null);

    mockMvc.perform(get(ReverseRouter.route(on(HasPermissionInterceptorTest.TestController.class)
            .withExplorationPermissionRequired()
        ))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void preHandle_whenMemberNotInRegulatorTeam_thenForbidden() throws Exception {

    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(Collections.emptyList());

    mockMvc.perform(get(ReverseRouter.route(on(HasPermissionInterceptorTest.TestController.class)
            .withExplorationPermissionRequired()
        ))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void preHandle_whenUserHasRequiredPermission_thenOk() throws Exception {

    var roleWithRequiredPermission = TestTeamRole.EXPLORATION_ROLE;

    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(roleWithRequiredPermission)
        .build();

    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(List.of(teamMember));

    mockMvc.perform(get(ReverseRouter.route(on(HasPermissionInterceptorTest.TestController.class)
            .withExplorationPermissionRequired()
        ))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void preHandle_whenUserDoesNotHaveRequiredPermission_thenForbidden() throws Exception {

    var roleWithoutRequiredPermission = TestTeamRole.NOT_CREATE_NOMINATION_ROLE;

    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(roleWithoutRequiredPermission)
        .build();

    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(List.of(teamMember));

    mockMvc.perform(get(ReverseRouter.route(on(HasPermissionInterceptorTest.TestController.class)
            .withExplorationPermissionRequired()
        ))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Controller
  static class TestController {

    private static final String VIEW_NAME = "test-view";

    @GetMapping("/no-supported-annotation")
    ModelAndView noSupportedAnnotations() {
      return new ModelAndView(VIEW_NAME);
    }

    @GetMapping("/with-exploration-permission")
    @HasPermission(permissions = RolePermission.EXPLORATION)
    ModelAndView withExplorationPermissionRequired() {
      return new ModelAndView(VIEW_NAME);
    }
  }

  enum TestTeamRole implements TeamRole {

    EXPLORATION_ROLE(
        EnumSet.of(RolePermission.EXPLORATION)
    ),
    NOT_CREATE_NOMINATION_ROLE(
        EnumSet.of(RolePermission.GRANT_REGULATOR_ROLES)
    );

    private final Set<RolePermission> rolePermissions;

    TestTeamRole(Set<RolePermission> rolePermissions) {
      this.rolePermissions = rolePermissions;
    }

    @Override
    public String getDescription() {
      return null;
    }

    @Override
    public int getDisplayOrder() {
      return 0;
    }

    @Override
    public String getDisplayName() {
      return null;
    }

    @Override
    public Set<RolePermission> getRolePermissions() {
      return rolePermissions;
    }
  }
}
