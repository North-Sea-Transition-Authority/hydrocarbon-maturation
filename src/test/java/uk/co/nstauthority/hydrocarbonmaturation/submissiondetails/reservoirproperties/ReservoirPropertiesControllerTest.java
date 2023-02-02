package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ReservoirPropertiesController.class)
class ReservoirPropertiesControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private ReservoirPropertiesFormService reservoirPropertiesFormService;

  @MockBean
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @MockBean
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @MockBean
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private ReservoirPropertiesForm form;
  private BindingResult bindingResult;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    form = new ReservoirPropertiesForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getReservoirProperties_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(new ReservoirPropertiesForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/reservoirProperties"));
  }

  @SecurityTest
  void getReservoirProperties_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getReservoirProperties_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .getReservoirProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveReservoirPropertiesPage_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirPropertiesController.class).saveReservoirProperties(
                submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = SubmissionType.class, names = {"CREATE_DISCOVERY", "UPDATE_DISCOVERY"})
  void getReservoirProperties_whenDiscovery_assertStatusOk() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(new ReservoirPropertiesForm());
    var discoveryWellDetails = new DiscoveryWellDetail();
    discoveryWellDetails.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetails));

    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/reservoirProperties"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveReservoirProperties_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(reservoirPropertiesFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(ReservoirPropertiesController.class).saveReservoirProperties(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(reservoirPropertiesPersistenceService, times(1))
        .saveReservoirProperty(any(ReservoirPropertiesForm.class), eq(submissionDetail));
    verify(reservoirPropertiesFormService, times(1))
        .validate(any(), any(), eq(validationType), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveReservoirProperties_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(reservoirPropertiesFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(ReservoirPropertiesController.class).saveReservoirProperties(submissionDetailId, form, bindingResult, ValidationType.PARTIAL)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(reservoirPropertiesFormService, times(1))
        .validate(any(), any(), eq(validationType), eq(submissionDetail));
    verify(reservoirPropertiesPersistenceService, never())
        .saveReservoirProperty(any(), any());
  }

  @Test
  void getReservoirPropertiesModelAndView_whenProspectSubmission_assertModelProperties() throws Exception {
    var form = new ReservoirPropertiesForm();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(form);

    var hydrocarbonCaseDetail = mock(HydrocarbonCaseDetail.class);
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCaseDetail));
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail))
        .thenReturn(true);
    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("averageNetPayThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToHydrocarbonContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY))
        .containsEntry("depthToHydrocarbonContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT))
        .containsEntry("mixedCase", true)
        .doesNotContainKeys("depthToGasOilContactTemplate",
            "depthToGasWaterContactTemplate", "depthToOilWaterContactTemplate");
  }

  @Test
  void getReservoirPropertiesModelAndView_whenDiscoverySubmission_assertModelProperties() throws Exception {
    var form = new ReservoirPropertiesForm();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(form);
    var discoveryWellDetails = new DiscoveryWellDetail();
    when(discoveryWellDetailsPersistenceService.isDiscoveryMixed(any())).thenReturn(true);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetails));

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("averageNetPayThicknessOptionalError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToOilWaterContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToGasWaterContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToGasOilContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessOptionalTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY))
        .containsEntry("depthToOilWaterContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT))
        .containsEntry("depthToGasWaterContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT))
        .containsEntry("depthToGasOilContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT))
        .containsEntry("mixedCase", true);
  }

  @Test
  void getReservoirPropertiesModelAndView_whenDryHoleSubmission_assertModelProperties() throws Exception {
    var form = new ReservoirPropertiesForm();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("averageNetPayThicknessOptionalError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessOptionalTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY))
        .doesNotContainKeys("mixedCase", "depthToGasOilContactTemplate",
            "depthToGasWaterContactTemplate", "depthToOilWaterContactTemplate");
  }

  @Test
  void getReservoirPropertiesModelAndView_whenDiscoverySubmission_whenNoDiscoveryWell_assertHydrocarbonCasesFalse() throws Exception {
    var form = new ReservoirPropertiesForm();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(form);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .containsEntry("mixedCase", false)
        .containsEntry("gasCase", false)
        .containsEntry("oilCase", false)
        .doesNotContainKeys("depthToGasOilContactTemplate",
        "depthToGasWaterContactTemplate", "depthToOilWaterContactTemplate");
  }

  @Test
  void saveReservoirProperties_whenDiscoverySubmission_whenErrors_assertErrorsAdded() throws Exception {
    var form = new ReservoirPropertiesForm();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(form);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(reservoirPropertiesFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    var modelMap = mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .saveReservoirProperties(submissionDetailId, null, null, ValidationType.FULL)))
                .params(ControllerTestUtils.validationPostParams(ValidationType.FULL))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView()
        .getModelMap();

    assertThat(modelMap)
        .containsEntry("depthToOilWaterContactError",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(bindingResult,
                    "depthToOilWaterContact", form.getDepthToOilWaterContact()))
        .containsEntry("depthToGasWaterContactError",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(bindingResult,
                    "depthToGasWaterContact", form.getDepthToGasWaterContact()))
        .containsEntry("depthToGasOilContactError",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(bindingResult,
                    "depthToGasOilContact", form.getDepthToGasOilContact()))
        .containsEntry("mixedCase", false)
        .containsEntry("gasCase", false)
        .containsEntry("oilCase", false);
  }
}