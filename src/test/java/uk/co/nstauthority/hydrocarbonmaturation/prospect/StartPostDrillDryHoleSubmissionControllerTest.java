package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsFormService;

@ContextConfiguration(classes = StartPostDrillDryHoleSubmissionController.class)
@WithMockUser
class StartPostDrillDryHoleSubmissionControllerTest extends AbstractControllerTest {

  @MockBean
  GeneralDetailsFormService generalDetailsFormService;

  @MockBean
  StartProspectService startProspectService;

  @Test
  void getStartPostDrillDryHolePage_assertStatusOk() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage())))
        .andExpect(status().isOk());
  }

  @Test
  void startPostDrillDryHole_assertRedirection() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).startPostDrillDryHole()))
            .with(csrf()))
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrl(ReverseRouter.route(on(PostDrillDryHoleController.class).getPostDrillDryHolePage(null))));
  }

  @Test
  void getStartPostDrillDryHolePage_assertModelProperties() throws Exception {
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage())))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();
    assertEquals(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).startPostDrillDryHole()), model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()), model.get("backLinkUrl"));
  }
}