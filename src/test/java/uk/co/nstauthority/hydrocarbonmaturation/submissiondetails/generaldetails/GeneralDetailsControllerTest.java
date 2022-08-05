package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.StartProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@WithMockUser
@ContextConfiguration(classes = GeneralDetailsController.class)
class GeneralDetailsControllerTest extends AbstractControllerTest {

  @MockBean
  GeneralDetailsService generalDetailsService;

  @MockBean
  SubmissionDetailService submissionDetailService;

  private static GeneralDetailsForm form;
  private static BindingResult bindingResult;
  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    form = new GeneralDetailsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void getGeneralDetails_assertStatusOk() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null))))
        .andExpect(status().isOk())
        .andExpect(view().name("hm/submissiondetails/generalDetails"));
  }

  @Test
  void saveGeneralDetails_whenValidForm_assertRedirection() throws Exception {
    when(generalDetailsService.validate(any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, form, bindingResult)))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(generalDetailsService, times(1)).saveGeneralDetails(any(GeneralDetailsForm.class), eq(submissionDetail));
  }

  @Test
  void saveGeneralDetailsPage_whenInValidForm_assertStatusOk() throws Exception {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(generalDetailsService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, form, bindingResult)))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(generalDetailsService, never()).saveGeneralDetails(any(), any());
  }

  @Test
  void getGeneralDetailsModelAndView_assertModelProperties() throws Exception {
    var modelMap =     Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null))))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap.get("onshoreOffshore")).isEqualTo(OnshoreOffshore.getOnshoreOffshore());
    assertThat(modelMap.get("informationSources")).isEqualTo(InformationSource.getInformationSource());
    assertEquals(GeneralDetailsForm.class, modelMap.get("form").getClass());
    assertThat(modelMap.get("actionUrl")).isEqualTo(ReverseRouter.route(on(GeneralDetailsController.class)
        .saveGeneralDetails(submissionDetailId, null, null)));
    assertThat(modelMap.get("backLinkUrl")).isEqualTo(ReverseRouter.route(on(StartProspectController.class).getStartPage()));
  }
}