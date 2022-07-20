package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.StartProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = StartSubmissionController.class)
class StartSubmissionControllerTest extends AbstractControllerTest {

  @MockBean
  SubmissionTypeSelectService submissionTypeSelectService;

  @Test
  void getSubmissionPurpose_assertStatusOk() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose())))
        .andExpect(status().isOk());
  }

  @Test
  void startSubmission_whenValidForm_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    form.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(csrf())
    ).andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrl(ReverseRouter.route(on(StartProspectController.class).getStartPage())));

  }

  @Test
  void startSubmission_whenInValidForm_assertStatusOk() throws Exception {
    var form = new SubmissionTypeSelectForm();
    form.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(csrf())
    ).andExpect(status().isOk());
  }


  @Test
  void getSubmissionPurpose_assertModelProperties() throws Exception {
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose())))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertEquals(SubmissionType.getSubmissionTypes(), model.get("submissionType"));
    assertEquals(SubmissionTypeSelectForm.class, model.get("form").getClass());
    assertEquals(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(null, null)), model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), model.get("backLinkUrl"));
  }
}