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
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = GasFluidPropertyController.class)
class GasFluidPropertyControllerTest extends AbstractSubmissionControllerTest {

  @MockitoBean
  private GasFluidPropertyFormService gasFluidPropertyFormService;

  @MockitoBean
  private GasFluidPropertyFormValidator gasFluidPropertyFormValidator;

  @MockitoBean
  private GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  private BindingResult bindingResult;
  private UUID submissionDetailId;
  private UUID reservoirSubmissionDetailId;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    reservoirSubmissionDetailId = reservoirSubmissionDetail.getId();

    var gasFluidPropertyForm = new GasFluidPropertyForm();
    bindingResult = new BeanPropertyBindingResult(gasFluidPropertyForm, "form");
  }

  @SecurityTest
  void getGasFluidProperties_assertStatusOk() throws Exception {
    when(gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail))
        .thenReturn(new GasFluidPropertyForm());
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(GasFluidPropertyController.class).getGasFluidProperties(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/gasFluidProperties"));
  }

  @SecurityTest
  void getGasFluidProperties_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(GasFluidPropertyController.class).getGasFluidProperties(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }


  @SecurityTest
  void getGasFluidProperties_andTypeIsNotDiscovery_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    mockMvc.perform(
            get(ReverseRouter.route(on(GasFluidPropertyController.class).getGasFluidProperties(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getGasFluidProperties_andNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(GasFluidPropertyController.class).getGasFluidProperties(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveGasFluidProperties_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(gasFluidPropertyFormValidator.isValid(any(GasFluidPropertyForm.class), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType)))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(GasFluidPropertyController.class)
            .saveGasFluidProperties(submissionDetailId, reservoirSubmissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetailId))));

    verify(gasFluidPropertyFormValidator, times(1))
        .isValid(any(GasFluidPropertyForm.class), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType));
    verify(gasFluidPropertyPersistenceService, times(1))
        .saveGasFluidProperty(any(GasFluidPropertyForm.class), eq(reservoirSubmissionDetail));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveGasFluidProperties_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(gasFluidPropertyFormValidator.isValid(any(GasFluidPropertyForm.class), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType)))
        .thenReturn(false);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(GasFluidPropertyController.class)
            .saveGasFluidProperties(submissionDetailId, reservoirSubmissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(gasFluidPropertyFormValidator, times(1))
        .isValid(any(GasFluidPropertyForm.class), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType));
    verify(gasFluidPropertyPersistenceService, never())
        .saveGasFluidProperty(any(GasFluidPropertyForm.class), eq(reservoirSubmissionDetail));
  }

  @Test
  void getGasFluidPropertiesModelAndView_noExistingProperties_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    var form = new GasFluidPropertyForm();
    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    when(gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(GasFluidPropertyController.class)
                .getGasFluidProperties(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER))).andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(GasFluidPropertyForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId)), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @Test
  void getGasFluidPropertiesModelAndView_existingProperties_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    var form = new GasFluidPropertyForm();
    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(new GasFluidProperty()));
    when(gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                get(ReverseRouter.route(on(GasFluidPropertyController.class)
                    .getGasFluidProperties(submissionDetailId, reservoirSubmissionDetailId)))
                    .with(user(USER))).andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(GasFluidPropertyForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId)), ReservoirTaskListController.PAGE_TITLE)
    );

    assertThat(modelMap)
        .containsEntry("form", form);
  }
}