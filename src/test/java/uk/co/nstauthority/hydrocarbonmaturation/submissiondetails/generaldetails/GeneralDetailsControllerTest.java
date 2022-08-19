package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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

import java.util.Map;
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
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = GeneralDetailsController.class)
class GeneralDetailsControllerTest extends AbstractControllerTest {

  @MockBean
  GeneralDetailsFormService generalDetailsFormService;

  @MockBean
  SubmissionDetailService submissionDetailService;

  @MockBean
  GeneralDetailsPersistenceService generalDetailsPersistenceService;

  private static GeneralDetailsForm form;
  private static BindingResult bindingResult;
  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void getGeneralDetails_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(new GeneralDetailsForm());
    mockMvc.perform(
        get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/generalDetails"));
  }

  @Test
  void saveGeneralDetails_partialValidation_whenValidForm_assertSaveAndRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(GeneralDetailsForm.class, "form");
    when(generalDetailsFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(ValidationType.PARTIAL))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(generalDetailsFormService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(generalDetailsPersistenceService, times(1)).saveGeneralDetails(any(GeneralDetailsForm.class), eq(submissionDetail));
  }

  @Test
  void saveGeneralDetailsPage_fullValidation_whenInvalidForm_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(GeneralDetailsForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(generalDetailsFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(ValidationType.FULL))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/generalDetails"));

    verify(generalDetailsFormService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(generalDetailsPersistenceService, never()).saveGeneralDetails(any(), any());
  }

  @Test
  void saveGeneralDetailsPage_fullValidation_whenValidForm_assertSaveAndRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(GeneralDetailsForm.class, "form");
    when(generalDetailsFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(ValidationType.FULL))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(generalDetailsFormService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(generalDetailsPersistenceService, times(1)).saveGeneralDetails(any(GeneralDetailsForm.class), eq(submissionDetail));
  }

  @Test
  void getGeneralDetailsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(new GeneralDetailsForm());

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null))))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("onshoreOffshore")).isEqualTo(OnshoreOffshore.getOnshoreOffshore());
    assertThat(modelMap.get("informationSources")).isEqualTo(InformationSource.getInformationSource());
    assertEquals(GeneralDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)), ProspectTaskListController.PAGE_NAME)
    );  }
}