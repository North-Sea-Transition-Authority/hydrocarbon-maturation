package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = HydrocarbonCaseController.class)
class HydrocarbonCaseControllerTest extends AbstractControllerTest {

  @MockBean
  SubmissionDetailService submissionDetailService;

  @MockBean
  HydrocarbonCaseFormService hydrocarbonCaseFormService;

  @MockBean
  HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  private static HydrocarbonCaseForm form;
  private static BindingResult bindingResult;
  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    form = new HydrocarbonCaseForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getHydrocarbonCase_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(hydrocarbonCaseFormService.getHydrocarbonCaseForm(submissionDetail)).thenReturn(new HydrocarbonCaseForm());

    mockMvc.perform(
        get(ReverseRouter.route(on(HydrocarbonCaseController.class).getHydrocarbonCase(submissionDetailId))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/hydrocarbonCase"));
  }

  @Test
  void saveHydrocarbonCase_whenValidForm_assertRedirection() throws Exception {
    when(hydrocarbonCaseFormService.validate(any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(HydrocarbonCaseController.class).saveHydrocarbonCase(submissionDetailId, form, bindingResult)))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(hydrocarbonCasePersistenceService, times(1)).saveHydrocarbonCase(any(HydrocarbonCaseForm.class), eq(submissionDetail));
  }

  @Test
  void saveHydrocarbonCase_whenInValidForm_assertStatusOk() throws Exception {
    var form = new HydrocarbonCaseForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(hydrocarbonCaseFormService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(HydrocarbonCaseController.class).saveHydrocarbonCase(submissionDetailId, form, bindingResult)))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(hydrocarbonCasePersistenceService, never()).saveHydrocarbonCase(any(), any());
  }

  @Test
  void getGeneralDetailsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(hydrocarbonCaseFormService.getHydrocarbonCaseForm(submissionDetail)).thenReturn(new HydrocarbonCaseForm());

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(HydrocarbonCaseController.class).getHydrocarbonCase(submissionDetailId))))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertEquals(HydrocarbonCaseForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)), ProspectTaskListController.PAGE_NAME)
    );
  }
}