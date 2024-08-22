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

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = FieldReservoirCombinedVolumetricsController.class)
class FieldReservoirCombinedVolumetricsControllerTest extends AbstractSubmissionControllerTest {
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
  void getFieldCombinedVolumetrics_assertOk() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(new FieldReservoirVolumetricsForm());

    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
            .getFieldCombinedVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/fieldVolumetrics"));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      names = {"DRAFT"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void getFieldCombinedReservoirVolumetrics_andStatusIsNotDraft_assertForbidden(
      SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
            .getFieldCombinedVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void getFieldCombinedReservoirVolumetrics_andTypeIsNotField_assertForbidden(
      SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
            .getFieldCombinedVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getFieldCombinedReservoirVolumetrics_andTypeIsField_assertOk(
      SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    submissionDetail.setSubmissionType(submissionType);

    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(new FieldReservoirVolumetricsForm());

    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    mockMvc.perform(get(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
            .getFieldCombinedVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void saveFieldCombinedReservoirVolumetrics_validForm_assertRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    when(fieldReservoirVolumetricsFormValidator.isValid(
        any(SubmissionForm.class),
        any(BindingResult.class),
        eq(reservoirSubmissionDetail),
        any(ValidationType.class))
    ).thenReturn(true);

    mockMvc.perform(post(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
            .saveFieldCombinedVolumetrics(
                submissionDetail.getId(),
                reservoirSubmissionDetail.getId(),
                null,
                null,
                null)))
            .with(user(USER))
            .with(csrf())
            .params(ControllerTestUtils.validationPostParams(validationType)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId(), null))));
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void saveFieldCombinedReservoirVolumetrics_invalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    when(fieldReservoirVolumetricsFormValidator.isValid(
        any(SubmissionForm.class),
        any(BindingResult.class),
        eq(reservoirSubmissionDetail),
        eq(ValidationType.FULL))
    ).thenReturn(false);

    mockMvc.perform(post(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
            .saveFieldCombinedVolumetrics(
                submissionDetail.getId(),
                reservoirSubmissionDetail.getId(),
                null,
                null,
                null)))
            .with(user(USER))
            .with(csrf())
            .params(ControllerTestUtils.validationPostParams(validationType)))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("remi/submissiondetails/fieldVolumetrics"));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void getFieldReservoirVolumetricsModelAndView_assertModelProperties_reservoirCarriedForward(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);

    var form = new FieldReservoirVolumetricsForm();
    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail)).thenReturn(form);

    var reservoirWasCarriedForward = Boolean.TRUE;
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoirSubmissionDetail.getReservoir()))
        .thenReturn(reservoirWasCarriedForward);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
                        .getFieldCombinedVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", FieldReservoirCombinedVolumetricsController.OIL_AND_GAS_VOLUMETRICS_PAGE_TITLE),
            entry("form", form),
            entry("hydrocarbonType", HydrocarbonsDiscovered.MIXED_OIL_AND_GAS),
            entry("oilInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilInPlaceTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD)),
            entry("gasInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasInPlaceTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD)),
            entry("reservoirCarriedForward", reservoirWasCarriedForward)
        );

    assertEquals(FieldReservoirVolumetricsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId(), null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId(), null)), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @Test
  void getFieldReservoirVolumetricsModelAndView_assertModelProperties_reservoirNotCarriedForward() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = new FieldReservoirVolumetricsForm();
    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
                        .getFieldCombinedVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .doesNotContainKey("reservoirCarriedForward")
        .contains(
            entry("pageTitle", FieldReservoirCombinedVolumetricsController.OIL_AND_GAS_VOLUMETRICS_PAGE_TITLE),
            entry("form", form),
            entry("hydrocarbonType", HydrocarbonsDiscovered.MIXED_OIL_AND_GAS),
            entry("oilInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilInPlaceTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD)),
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
            .getTaskList(submissionDetail.getId(), null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId(), null)), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void saveFieldCombinedReservoirVolumetrics_invalidForm_assertErrorAdded(
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
                post(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
                    .saveFieldCombinedVolumetrics(
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
        .contains(
            entry("oilInPlaceError", probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(bindingResult, "oilInPlace", form.getOilInPlace())),
            entry("gasInPlaceError", probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(bindingResult, "gasInPlace", form.getGasInPlace()))
            );

  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void saveFieldCombinedReservoirVolumetrics_validForm_assertSavedandRedirection(
      SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
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
            post(ReverseRouter.route(on(FieldReservoirCombinedVolumetricsController.class)
                .saveFieldCombinedVolumetrics(
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

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .filter(submissionType -> !submissionType.equals(SubmissionType.CREATE_FIELD))
        .map(Arguments::of);
  }
}