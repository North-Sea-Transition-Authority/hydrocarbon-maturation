package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.StartUpdateProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;

@ContextConfiguration(classes = StartUpdateProspectController.class)
class StartUpdateProspectControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @SecurityTest
  void getStartUpdatePage() throws Exception {
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartUpdateProspectController.class).getStartUpdatePage()))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "actionUrl",
            "backLinkUrl")
        .containsExactly(
            ReverseRouter.route(on(StartUpdateProspectController.class).updateProspect()),
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()));
  }

  @SecurityTest
  void updateProspect() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(StartUpdateProspectController.class).updateProspect()))
            .with(user(USER))
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(UpdateProspectController.class).getUpdatePage(null))));
  }
}