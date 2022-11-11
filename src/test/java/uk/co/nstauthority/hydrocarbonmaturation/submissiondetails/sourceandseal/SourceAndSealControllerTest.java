package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = SourceAndSealController.class)
class SourceAndSealControllerTest extends AbstractControllerTest {

  @MockBean
  SourceAndSealFormService sourceAndSealFormService;

  @MockBean
  SubmissionDetailService submissionDetailService;

  @MockBean
  SourceAndSealPersistenceService sourceAndSealPersistenceService;

  private SourceAndSealForm form;
  private BindingResult bindingResult;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @BeforeEach
  void setup() {
    form = new SourceAndSealForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void getSourceAndSealDetails_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(sourceAndSealFormService.getSourceAndSealForm(submissionDetail)).thenReturn(new SourceAndSealForm());

    mockMvc.perform(
        get(ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetailId))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/sourceAndSeal"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveSourceAndSealDetails_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(sourceAndSealFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(SourceAndSealController.class).saveSourceAndSealDetails(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(sourceAndSealPersistenceService, times(1)).saveSourceAndSealDetails(any(SourceAndSealForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveSourceAndSealDetails_fullValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(sourceAndSealFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(SourceAndSealController.class).saveSourceAndSealDetails(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(sourceAndSealPersistenceService, never()).saveSourceAndSealDetails(any(), any());
  }

  @Test
  void getSourceAndSealModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(sourceAndSealFormService.getSourceAndSealForm(submissionDetail)).thenReturn(new SourceAndSealForm());

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                get(ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetailId))))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(SourceAndSealForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)), ProspectTaskListController.PAGE_NAME)
    );
  }
}