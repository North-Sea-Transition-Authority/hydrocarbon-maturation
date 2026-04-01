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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

@ContextConfiguration(classes = FieldReservoirOilVolumetricsController.class)
class FieldReservoirOilVolumetricsControllerTest extends AbstractSubmissionControllerTest {

  @MockitoBean
  private FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService;

  @MockitoBean
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

    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirOilVolumetricsController.class)
            .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/fieldVolumetrics"));
  }

  @SecurityTest
  void getFieldReservoirVolumetrics_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirOilVolumetricsController.class)
            .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFieldReservoirVolumetrics_andTypeIsNotField_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirOilVolumetricsController.class)
            .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getPermeabilityPage_andUserNotInResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirOilVolumetricsController.class)
            .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void getFieldReservoirVolumetricsModelAndView_assertModelProperties_withReservoirCarriedForward(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);

    var form = new FieldReservoirVolumetricsForm();
    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail)).thenReturn(form);

    var reservoirCarriedForward = Boolean.TRUE;
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(reservoirCarriedForward);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(FieldReservoirOilVolumetricsController.class)
                        .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", FieldReservoirOilVolumetricsController.OIL_VOLUMETRICS_PAGE_TITLE),
            entry("form", form),
            entry("hydrocarbonType", HydrocarbonsDiscovered.OIL_ONLY),
            entry("oilInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilInPlaceTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD)),
            entry("reservoirCarriedForward", reservoirCarriedForward)
        );

    assertEquals(FieldReservoirVolumetricsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId(), null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId())), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @Test
  void getFieldReservoirVolumetricsModelAndView_assertModelProperties_noReservoirCarriedForward() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = new FieldReservoirVolumetricsForm();
    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(FieldReservoirOilVolumetricsController.class)
                        .getFieldReservoirVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .doesNotContainKey("reservoirCarriedForward")
        .contains(
            entry("pageTitle", FieldReservoirOilVolumetricsController.OIL_VOLUMETRICS_PAGE_TITLE),
            entry("form", form),
            entry("hydrocarbonType", HydrocarbonsDiscovered.OIL_ONLY),
            entry("oilInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilInPlaceTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD))
        );

    assertEquals(FieldReservoirVolumetricsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId(), null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId())), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void saveFieldReservoirVolumetrics_invalidForm_assertErrorAdded(
      SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
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
                post(ReverseRouter.route(on(FieldReservoirOilVolumetricsController.class)
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
        .containsEntry("oilInPlaceError",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(
                    bindingResult,
                    "oilInPlace",
                    form.getOilInPlace()
                ));

  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void saveFieldReservoirVolumetrics_validForm_assertSavedandRedirection(
      SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);

    var form = new FieldReservoirVolumetricsForm();
    when(fieldReservoirVolumetricsFormValidator.isValid(
        eq(form),
        any(BindingResult.class),
        eq(reservoirSubmissionDetail),
        eq(ValidationType.FULL))
    ).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldReservoirOilVolumetricsController.class)
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
            .getReservoirTaskList(submissionDetail.getId()))));

    verify(fieldReservoirVolumetricsFormService, times(1))
        .saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);
    verifyNoMoreInteractions(fieldReservoirVolumetricsFormService);
  }
}