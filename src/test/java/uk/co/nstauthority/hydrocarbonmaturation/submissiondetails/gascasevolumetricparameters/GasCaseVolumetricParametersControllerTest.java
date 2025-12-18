package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

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
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.time.Instant;
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
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = GasCaseVolumetricParametersController.class)
class GasCaseVolumetricParametersControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private GasCaseVolumetricParametersFormService gasCaseVolumetricParametersFormService;

  @MockBean
  private GasCaseVolumetricParametersFormValidator gasCaseVolumetricParametersFormValidator;

  @MockBean
  private GasCaseVolumetricParametersPersistenceService gasCaseVolumetricParametersPersistenceService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private GasCaseVolumetricParametersForm form;
  private UUID submissionDetailId;
  private BindingResult bindingResult;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private UUID reservoirSubmissionDetailId;

  @BeforeEach
  void setUp() {
    form = new GasCaseVolumetricParametersForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();

    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    reservoirSubmissionDetailId = reservoirSubmissionDetail.getId();
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
  }

  @SecurityTest
  void getGasCaseVolumetrics_assertOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(gasCaseVolumetricParametersFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail)).thenReturn(new GasCaseVolumetricParametersForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(GasCaseVolumetricParametersController.class)
            .getGasCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/gasCaseVolumetrics"));
  }

  @SecurityTest
  void getGasCaseVolumetrics_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(GasCaseVolumetricParametersController.class)
                .getGasCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getGasCaseVolumetrics_andTypeIsNotProspectOrDiscovery_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    mockMvc.perform(
            get(ReverseRouter.route(on(GasCaseVolumetricParametersController.class)
                .getGasCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }


  @SecurityTest
  void getGasCaseVolumetrics_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(GasCaseVolumetricParametersController.class)
                .getGasCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveGasCaseVolumetrics_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(GasCaseVolumetricParametersController.class).saveGasCaseVolumetrics(
                submissionDetailId, reservoirSubmissionDetailId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGasCaseVolumetrics_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(gasCaseVolumetricParametersFormValidator.isValid(any(SubmissionForm.class), any(Errors.class), eq(submissionDetail), eq(validationType))).thenReturn(true);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(GasCaseVolumetricParametersController.class)
            .saveGasCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId, form, bindingResult, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null))));

    verify(gasCaseVolumetricParametersPersistenceService, times(1)).saveGasCaseVolumetrics(any(GasCaseVolumetricParametersForm.class), eq(reservoirSubmissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGasCaseVolumetrics_partialValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(gasCaseVolumetricParametersFormValidator.isValid(any(SubmissionForm.class), any(Errors.class), eq(submissionDetail), eq(validationType))).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(GasCaseVolumetricParametersController.class)
                .saveGasCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId, form, bindingResult, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(gasCaseVolumetricParametersPersistenceService, never()).saveGasCaseVolumetrics(any(GasCaseVolumetricParametersForm.class), any(ReservoirSubmissionDetail.class));
  }

  @Test
  void getGasCaseVolumetricsModelAndView_whenProspect_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(gasCaseVolumetricParametersFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail)).thenReturn(
        form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(GasCaseVolumetricParametersController.class)
            .getGasCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("gasFormationVolumeFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT)),
            entry("gasHydrocarbonSaturationTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT)),
            entry("condensateGasRatioLifeOfFieldTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD)),
            entry("gasRecoveryFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR)),
            entry("gasExpansionFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR)),
            entry("gasExpansionFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasFormationVolumeFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("condensateGasRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoveryFactorError", ProbabilityDistributionTypeError.emptyError())
        );
  }

  @Test
  void getGasCaseVolumetricsModelAndView_whenDiscovery_assertModelProperties() throws Exception {
    var discoverySubmissionDetailId = UUID.randomUUID();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY,
        discoverySubmissionDetailId);
    discoverySubmissionDetail.setAcceptedTimestamp(Instant.now());
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    var discoveryReservoirSubmissionDetailId = discoveryReservoirSubmissionDetail.getId();
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, discoverySubmissionDetail);

    when(submissionDetailService.getSubmissionDetail(discoverySubmissionDetailId)).thenReturn(Optional.of(discoverySubmissionDetail));
    when(submissionDetailService.getSubmissionDetailForIdOrError(discoverySubmissionDetailId)).thenReturn(discoverySubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(discoveryReservoirSubmissionDetailId))
        .thenReturn(discoveryReservoirSubmissionDetail);
    when(gasCaseVolumetricParametersFormService.getGasCaseVolumetricsForm(discoveryReservoirSubmissionDetail)).thenReturn(
        form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(GasCaseVolumetricParametersController.class)
                .getGasCaseVolumetrics(discoverySubmissionDetailId, discoveryReservoirSubmissionDetailId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(TaskListController.class).getTaskList(discoverySubmissionDetailId, null)), TaskListController.PAGE_NAME),
            entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(discoverySubmissionDetailId)), ReservoirTaskListController.PAGE_TITLE)
        );

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("gasFormationVolumeFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY)),
            entry("gasHydrocarbonSaturationTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY)),
            entry("condensateGasRatioLifeOfFieldTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD)),
            entry("gasRecoveryFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR)),
            entry("gasExpansionFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR)),
            entry("gasExpansionFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasFormationVolumeFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("condensateGasRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoveryFactorError", ProbabilityDistributionTypeError.emptyError())
        );
  }
}