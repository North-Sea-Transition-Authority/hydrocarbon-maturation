package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = HasAnyRoleInterceptorTest.TestController.class)
class HasAnyRoleInterceptorTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Test
  void preHandle_whenMethodHasNoSupportedAnnotations_thenOkResponse() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(HasAnyRoleInterceptorTest.TestController.class)
            .noSupportedAnnotations()
        ))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void preHandle_whenUserDoesNotHaveRole_thenForbidden() throws Exception {

    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(get(ReverseRouter.route(on(HasAnyRoleInterceptorTest.TestController.class)
            .withSingleAllowedRole()
        ))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void preHandle_whenUserHasRole_thenOk() throws Exception {

    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(on(HasAnyRoleInterceptorTest.TestController.class)
            .withSingleAllowedRole()
        ))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void preHandle_whenUserHasRoleMultipleAllowed_thenOk() throws Exception {

    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);

    mockMvc.perform(get(ReverseRouter.route(on(HasAnyRoleInterceptorTest.TestController.class)
            .withMultipleAllowedRoles()
        ))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Controller
  static class TestController {

    private static final String VIEW_NAME = "test-view";

    @GetMapping("/no-supported-annotation")
    ModelAndView noSupportedAnnotations() {
      return new ModelAndView(VIEW_NAME);
    }

    @HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
    @GetMapping("/with-single-role")
    ModelAndView withSingleAllowedRole() {
      return new ModelAndView(VIEW_NAME);
    }

    @HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
    @HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
    @GetMapping("/with-multiple-roles")
    ModelAndView withMultipleAllowedRoles() {
      return new ModelAndView(VIEW_NAME);
    }
  }
}
