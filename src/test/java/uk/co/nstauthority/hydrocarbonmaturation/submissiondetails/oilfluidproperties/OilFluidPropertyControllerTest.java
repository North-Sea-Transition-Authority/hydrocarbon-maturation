package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

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

@ContextConfiguration(classes = OilFluidPropertyController.class)
class OilFluidPropertyControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private OilFluidPropertyFormService oilFluidPropertyFormService;

  @MockBean
  private OilFluidPropertyFormValidator oilFluidPropertyFormValidator;

  @MockBean
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  private BindingResult bindingResult;

  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    var oilFluidPropertyForm = new OilFluidPropertyForm();
    bindingResult = new BeanPropertyBindingResult(oilFluidPropertyForm, "form");
  }

  @SecurityTest
  void getOilFluidProperties_assertStatusOk() throws Exception {
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail))
        .thenReturn(new OilFluidPropertyForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(OilFluidPropertyController.class).getOilFluidProperties(submissionDetailId, reservoirSubmissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/oilFluidProperties"));
  }

  @SecurityTest
  void getOilFluidProperties_andTypeIsField_assertStatusOk() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail))
        .thenReturn(new OilFluidPropertyForm());
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(OilFluidPropertyController.class).getOilFluidProperties(submissionDetailId, reservoirSubmissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/oilFluidProperties"));
  }

  @SecurityTest
  void getOilFluidProperties_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(OilFluidPropertyController.class).getOilFluidProperties(submissionDetailId, reservoirSubmissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOilFluidProperties_andTypeIsNotDiscovery_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    mockMvc.perform(
            get(ReverseRouter.route(on(OilFluidPropertyController.class).getOilFluidProperties(submissionDetailId, reservoirSubmissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOilFluidProperties_andNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(OilFluidPropertyController.class).getOilFluidProperties(submissionDetailId, reservoirSubmissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveOilFluidProperties_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(oilFluidPropertyFormValidator.isValid(any(OilFluidPropertyForm.class), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType)))
        .thenReturn(true);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(OilFluidPropertyController.class)
                .saveOilFluidProperties(submissionDetailId, reservoirSubmissionDetail.getId(), null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetailId))));

    verify(oilFluidPropertyPersistenceService, times(1))
        .saveOilFluidProperty(any(OilFluidPropertyForm.class), eq(reservoirSubmissionDetail));
    verify(oilFluidPropertyFormValidator, times(1))
        .isValid(any(OilFluidPropertyForm.class), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveOilFluidProperties_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(oilFluidPropertyFormValidator.isValid(any(OilFluidPropertyForm.class), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType)))
        .thenReturn(false);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(OilFluidPropertyController.class)
                .saveOilFluidProperties(submissionDetailId, reservoirSubmissionDetail.getId(), null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(oilFluidPropertyFormValidator, times(1))
        .isValid(any(OilFluidPropertyForm.class), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType));
    verify(oilFluidPropertyPersistenceService, never())
        .saveOilFluidProperty(any(OilFluidPropertyForm.class), eq(reservoirSubmissionDetail));
  }

  @Test
  void getOilFluidPropertiesModelAndView_alreadyExists_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    var form = new OilFluidPropertyForm();
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail))
        .thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                get(ReverseRouter.route(on(OilFluidPropertyController.class)
                    .getOilFluidProperties(submissionDetailId, reservoirSubmissionDetail.getId())))
                    .with(user(USER))).andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(OilFluidPropertyForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId)), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @Test
  void getOilFluidProperties_noneAlreadyExist_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail))
        .thenReturn(new OilFluidPropertyForm());

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                get(ReverseRouter.route(on(OilFluidPropertyController.class)
                    .getOilFluidProperties(submissionDetailId, reservoirSubmissionDetail.getId())))
                    .with(user(USER))).andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(OilFluidPropertyForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId)), ReservoirTaskListController.PAGE_TITLE)
    );
  }
}