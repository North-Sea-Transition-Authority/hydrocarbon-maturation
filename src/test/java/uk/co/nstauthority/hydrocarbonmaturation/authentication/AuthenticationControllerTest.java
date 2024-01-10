package uk.co.nstauthority.hydrocarbonmaturation.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@WebMvcTest
@ContextConfiguration(classes = {
    AuthenticationControllerTest.TestAuthenticationController.class,
    UserService.class
})
class AuthenticationControllerTest extends AbstractSubmissionControllerTest {

  @Controller
  @RequestMapping("/auth")
  static class TestAuthenticationController {

    private final UserService userService;

    @Autowired
    TestAuthenticationController(UserService userService) {
      this.userService = userService;
    }

    @GetMapping("/secured")
    public ModelAndView renderSecured() {
      return new ModelAndView().addObject("user", userService.getUser());
    }

  }

  @SecurityTest
  void authenticationRequired() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(TestAuthenticationController.class).renderSecured())))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void authorisedRequest() throws Exception {
    var expectedUser = UserTestUtil.Builder()
        .withWuaId(100L)
        .build();

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(TestAuthenticationController.class).renderSecured()))
        .with(user(expectedUser)))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();

    assertThat((User) modelAndView.getModel().get("user"))
        .extracting(User::wuaId)
        .isEqualTo(expectedUser.wuaId());
  }

}