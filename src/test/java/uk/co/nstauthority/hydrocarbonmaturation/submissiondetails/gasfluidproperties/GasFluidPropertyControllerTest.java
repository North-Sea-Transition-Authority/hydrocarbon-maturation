package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

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
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = GasFluidPropertyController.class)
class GasFluidPropertyControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private GasFluidPropertyFormService gasFluidPropertyFormService;

  @MockBean
  private GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  private BindingResult bindingResult;
  private GasFluidPropertyForm gasFluidPropertyForm;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    gasFluidPropertyForm = new GasFluidPropertyForm();
    bindingResult = new BeanPropertyBindingResult(gasFluidPropertyForm, "form");
  }

  @SecurityTest
  void getGasFluidProperties_assertStatusOk() throws Exception {
    when(gasFluidPropertyFormService.getGasFluidPropertyForm(submissionDetail))
        .thenReturn(new GasFluidPropertyForm());
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(GasFluidPropertyController.class).getGasFluidProperties(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/gasFluidProperties"));
  }

  @SecurityTest
  void getGasFluidProperties_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(GasFluidPropertyController.class).getGasFluidProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }


  @SecurityTest
  void getGasFluidProperties_andTypeIsNotDiscovery_assertForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    mockMvc.perform(
            get(ReverseRouter.route(on(GasFluidPropertyController.class).getGasFluidProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getGasFluidProperties_andNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(GasFluidPropertyController.class).getGasFluidProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveGasFluidProperties_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(gasFluidPropertyFormService.validate(any(), any(), any(), eq(submissionDetail)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(GasFluidPropertyController.class)
            .saveGasFluidProperties(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetailId))));

    verify(gasFluidPropertyPersistenceService, times(1))
        .saveGasFluidProperty(any(GasFluidPropertyForm.class), eq(submissionDetail));
    verify(gasFluidPropertyFormService, times(1))
        .validate(any(), any(), eq(validationType), eq(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveGasFluidProperties_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(gasFluidPropertyFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(GasFluidPropertyController.class)
            .saveGasFluidProperties(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(gasFluidPropertyFormService, times(1))
        .validate(any(), any(), eq(validationType), eq(submissionDetail));
    verify(gasFluidPropertyPersistenceService, never())
        .saveGasFluidProperty(any(), any());
  }

  @Test
  void getGasFluidPropertiesModelAndView_assertModelProperties() throws Exception {
    var form = new GasFluidPropertyForm();
    when(gasFluidPropertyFormService.getGasFluidPropertyForm(submissionDetail))
        .thenReturn(form);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(GasFluidPropertyController.class)
                .getGasFluidProperties(submissionDetailId)))
                .with(user(USER))).andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(GasFluidPropertyForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form);
  }
}