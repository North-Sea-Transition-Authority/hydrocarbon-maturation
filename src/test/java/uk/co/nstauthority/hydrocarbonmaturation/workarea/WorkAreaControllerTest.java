package uk.co.nstauthority.hydrocarbonmaturation.workarea;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;

@ContextConfiguration(
    classes = WorkAreaController.class
)
class WorkAreaControllerTest extends AbstractControllerTest {

  @Test
  void getWorkArea_assertHttpOk() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()))
        )
        .andExpect(status().isOk())
        .andExpect(view().name("hm/workarea/workArea"))
        .andExpect(model().attribute("startSubmissionUrl", ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose())));

  }
}
