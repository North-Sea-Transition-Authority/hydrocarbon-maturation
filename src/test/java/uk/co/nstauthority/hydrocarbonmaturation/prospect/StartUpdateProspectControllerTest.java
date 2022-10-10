package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;

@WithMockUser
@ContextConfiguration(classes = StartUpdateProspectController.class)
class StartUpdateProspectControllerTest extends AbstractControllerTest {

  @MockBean
  private SubmissionCreateService submissionCreateService;

  @Test
  void getStartUpdatePage() throws Exception {
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartUpdateProspectController.class).getStartUpdatePage())))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView.getModel())
        .extracting(
            "actionUrl",
            "backLinkUrl")
        .containsExactly(
            ReverseRouter.route(on(StartUpdateProspectController.class).updateProspect()),
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()));
  }

  @Test
  void updateProspect() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(StartUpdateProspectController.class).updateProspect()))
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(UpdateProspectController.class).getUpdatePage(null))));
  }
}