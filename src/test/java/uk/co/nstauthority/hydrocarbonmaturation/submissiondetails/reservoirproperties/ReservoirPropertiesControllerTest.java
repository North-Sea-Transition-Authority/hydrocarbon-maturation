package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = ReservoirPropertiesController.class)
class ReservoirPropertiesControllerTest extends AbstractControllerTest {

  @MockBean
  ReservoirPropertiesFormService reservoirPropertiesFormService;

  @MockBean
  ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @MockBean
  SubmissionDetailService submissionDetailService;

  private ReservoirPropertiesForm form;
  private BindingResult bindingResult;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @BeforeEach
  void setup() {
    form = new ReservoirPropertiesForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void getReservoirProperties_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(new ReservoirPropertiesForm());

    mockMvc.perform(
        get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId))))
        .andExpect(status().isOk())
        .andExpect(view().name("hm/submissiondetails/reservoirProperties"));
  }

  @Test
  void saveSourceAndSealDetails_whenValidForm_assertRedirection() throws Exception {
    when(reservoirPropertiesFormService.validate(any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(ReservoirPropertiesController.class).saveReservoirProperties(submissionDetailId, form, bindingResult)))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(reservoirPropertiesPersistenceService, times(1))
        .saveReservoirProperty(any(ReservoirPropertiesForm.class), eq(submissionDetail));
  }

  @Test
  void saveSourceAndSealDetails_whenInvalidForm_assertStatusOk() throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(reservoirPropertiesFormService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ReservoirPropertiesController.class).saveReservoirProperties(submissionDetailId, form, bindingResult)))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(reservoirPropertiesPersistenceService, never())
        .saveReservoirProperty(any(), any());
  }

  @Test
  void getReservoirPropertiesModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(new ReservoirPropertiesForm());

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId))))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)), ProspectTaskListController.PAGE_NAME)
    );
  }
}