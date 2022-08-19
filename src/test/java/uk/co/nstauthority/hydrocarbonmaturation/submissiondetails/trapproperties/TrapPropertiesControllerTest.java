package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
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
@ContextConfiguration(classes = TrapPropertiesController.class)
class TrapPropertiesControllerTest extends AbstractControllerTest {

  @MockBean
  TrapPropertiesFormService trapPropertiesFormService;

  @MockBean
  SubmissionDetailService submissionDetailService;

  @MockBean
  TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  private static TrapPropertiesForm form;
  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;
  private static BindingResult bindingResult;

  @BeforeAll
  static void setup() {
    form = new TrapPropertiesForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getTrapProperties_assertOK() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail)).thenReturn(new TrapPropertiesForm());

    mockMvc.perform(
        get(ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetailId))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/trapProperties"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveTrapProperties_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(trapPropertiesFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(TrapPropertiesController.class).saveTrapProperties(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(trapPropertiesPersistenceService, times(1)).saveTrapProperties(any(TrapPropertiesForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveTrapProperties_partialValidation_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(trapPropertiesFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(TrapPropertiesController.class).saveTrapProperties(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(trapPropertiesPersistenceService, never()).saveTrapProperties(any(TrapPropertiesForm.class), any());
  }

  @Test
  void getGeneralDetailsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail)).thenReturn(new TrapPropertiesForm());

    var modelMap =     Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetailId))))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(modelMap.get("form").getClass()).isEqualTo(TrapPropertiesForm.class);
    assertThat(modelMap.get("trapTypes")).isEqualTo(TrapType.getTrapTypes());
    assertThat(modelMap.get("longitudeHemisphere")).isEqualTo( Hemisphere.getLongitudeHemisphere());
    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)), ProspectTaskListController.PAGE_NAME)
    );
  }
}