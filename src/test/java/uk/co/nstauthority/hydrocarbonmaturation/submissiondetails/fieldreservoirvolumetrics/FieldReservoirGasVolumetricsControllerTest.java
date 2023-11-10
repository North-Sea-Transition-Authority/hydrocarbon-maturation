package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = FieldReservoirGasVolumetricsController.class)
class FieldReservoirGasVolumetricsControllerTest extends AbstractSubmissionControllerTest {
  @MockBean
  private FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService;

  @MockBean
  private FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  @BeforeEach
  void setUp() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
  }

  @SecurityTest
  void getFieldReservoirVolumetrics_assertOkay() throws Exception {

    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(new FieldReservoirVolumetricsForm());

    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirGasVolumetricsController.class)
            .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/fieldVolumetrics"));
  }

  @SecurityTest
  void getFieldReservoirVolumetrics_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirGasVolumetricsController.class)
            .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFieldReservoirVolumetrics_andTypeIsNotField_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirGasVolumetricsController.class)
            .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getPermeabilityPage_andUserNotInResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirGasVolumetricsController.class)
            .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void getFieldReservoirVolumetricsModelAndView_assertModelProperties(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);

    var form = new FieldReservoirVolumetricsForm();
    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(FieldReservoirGasVolumetricsController.class)
                        .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", FieldReservoirGasVolumetricsController.GAS_VOLUMETRICS_PAGE_TITLE),
            entry("form", form),
            entry("hydrocarbonType", HydrocarbonsDiscovered.GAS_ONLY),
            entry("gasInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasInPlaceTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD))
        );

    assertEquals(FieldReservoirVolumetricsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId(), null)), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void saveFieldReservoirVolumetrics_invalidForm_assertErrorAdded(
      SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);

    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);

    var form = new FieldReservoirVolumetricsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(fieldReservoirVolumetricsFormValidator.isValid(
        form,
        bindingResult,
        reservoirSubmissionDetail,
        ValidationType.FULL)
    ).thenReturn(false);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                post(ReverseRouter.route(on(FieldReservoirGasVolumetricsController.class)
                    .saveFieldReservoirVolumetrics(
                        submissionDetail.getId(),
                        reservoirSubmissionDetail.getId(),
                        null,
                        null,
                        null)))
                    .params(ControllerTestUtils.validationPostParams(ValidationType.FULL))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .containsEntry("gasInPlaceError",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(
                    bindingResult,
                    "gasInPlace",
                    form.getGasInPlace()
                ));

  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void saveFieldReservoirVolumetrics_validForm_assertSavedandRedirection(
      SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);

    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);

    var form = new FieldReservoirVolumetricsForm();
    when(fieldReservoirVolumetricsFormValidator.isValid(
        eq(form),
        any(BindingResult.class),
        eq(reservoirSubmissionDetail),
        eq(ValidationType.FULL))
    ).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldReservoirGasVolumetricsController.class)
                .saveFieldReservoirVolumetrics(
                    submissionDetail.getId(),
                    reservoirSubmissionDetail.getId(),
                    null,
                    null,
                    null)))
                .flashAttr("form", form)
                .params(ControllerTestUtils.validationPostParams(ValidationType.FULL))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId(), null))));

    verify(fieldReservoirVolumetricsFormService, times(1))
        .saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);
    verifyNoMoreInteractions(fieldReservoirVolumetricsFormService);
  }
}