package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@WithMockUser
@ContextConfiguration(classes = StartPostDrillDiscoveryController.class)
class StartPostDrillDiscoveryControllerTest extends AbstractControllerTest {

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void getStartPostDrillDiscoveryPage() throws Exception {
    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage())))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView.getModel())
        .extracting(
            "actionUrl",
            "backLinkUrl")
        .containsExactly(
            ReverseRouter.route(on(StartPostDrillDiscoveryController.class).startPostDrillDiscovery()),
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()));
  }

  @Test
  void startPostDrillDiscovery() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(StartPostDrillDiscoveryController.class).startPostDrillDiscovery()))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(PostDrillDiscoveryController.class).getStartDiscoveryPage(null))));
  }
}