package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = SystemOfRecordLandingPageController.class)
class SystemOfRecordLandingPageControllerTest extends AbstractControllerTest {
  private static final User USER = UserTestUtil.Builder().build();

  @SecurityTest
  void getLandingPage_noUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(SystemOfRecordLandingPageController.class).getLandingPage())))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getLandingPage_whenServiceUser_assertStatusOk() throws Exception {
    var modelMap = mockMvc.perform(
            get(ReverseRouter.route(on(SystemOfRecordLandingPageController.class).getLandingPage()))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView()
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", SystemOfRecordLandingPageController.PAGE_TITLE),
            entry("prospectAndDiscoverySearchUrl",
                ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
                    .showSearchResourceRecordsScreen(null, null)))
        );
  }
}