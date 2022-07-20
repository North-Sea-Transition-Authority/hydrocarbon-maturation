package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.DefaultModelAttributeTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;

@WebMvcTest
@ContextConfiguration(
    classes = WorkAreaController.class
)
class WorkAreaControllerTest extends AbstractControllerTest {

  @Test
  void getWorkArea_assertHttpOk() throws Exception {
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()))
    )
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    var expectedStartSubmissionUrl = ReverseRouter.route(on(StartSubmissionController.class).startSubmission(null,null));
    assertEquals("hm/workarea/workArea", modelAndView.getViewName());

    var modelMap = modelAndView.getModel();

    assertThat(modelMap.keySet()).isEqualTo(DefaultModelAttributeTestUtils.withDefaultKeys("startSubmissionUrl"));
    assertEquals(expectedStartSubmissionUrl, modelAndView.getModel().get("startSubmissionUrl"));
  }
}