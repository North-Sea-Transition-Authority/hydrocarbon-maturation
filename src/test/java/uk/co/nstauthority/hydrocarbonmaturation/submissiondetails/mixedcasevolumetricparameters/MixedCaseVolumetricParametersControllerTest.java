package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters;

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
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = MixedCaseVolumetricParametersController.class)
class MixedCaseVolumetricParametersControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private MixedCaseVolumetricParametersFormService mixedCaseVolumetricParametersFormService;

  @MockBean
  private MixedCaseVolumetricParametersPersistenceService mixedCaseVolumetricParametersPersistenceService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private MixedCaseVolumetricParametersForm form;
  private UUID submissionDetailId;
  private BindingResult bindingResult;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private UUID reservoirSubmissionDetailId;

  @BeforeEach
  void setUp() {
    form = new MixedCaseVolumetricParametersForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();

    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    reservoirSubmissionDetailId = reservoirSubmissionDetail.getId();
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
  }

  @SecurityTest
  void getMixedCaseVolumetrics_assertOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(mixedCaseVolumetricParametersFormService.getMixedCaseVolumetricsForm(reservoirSubmissionDetail)).thenReturn(new MixedCaseVolumetricParametersForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(MixedCaseVolumetricParametersController.class)
            .getMixedCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/mixedCaseVolumetrics"));
  }

  @SecurityTest
  void getMixedCaseVolumetrics_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(
            get(ReverseRouter.route(on(MixedCaseVolumetricParametersController.class)
                .getMixedCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getMixedCaseVolumetrics_andTypeNotProspectOrDiscovery_assertForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    mockMvc.perform(
            get(ReverseRouter.route(on(MixedCaseVolumetricParametersController.class)
                .getMixedCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getMixedCaseVolumetrics_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(MixedCaseVolumetricParametersController.class)
                .getMixedCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveMixedCaseVolumetrics_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(MixedCaseVolumetricParametersController.class).saveMixedCaseVolumetrics(
                submissionDetailId, reservoirSubmissionDetailId, null, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveMixedCaseVolumetrics_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(mixedCaseVolumetricParametersFormService.validate(any(), any(), any(), eq(submissionDetail)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(MixedCaseVolumetricParametersController.class)
            .saveMixedCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(mixedCaseVolumetricParametersPersistenceService, times(1)).saveMixedCaseVolumetrics(any(
        MixedCaseVolumetricParametersForm.class), eq(reservoirSubmissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveMixedCaseVolumetrics_partialValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(mixedCaseVolumetricParametersFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(MixedCaseVolumetricParametersController.class)
            .saveMixedCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(mixedCaseVolumetricParametersPersistenceService, never()).saveMixedCaseVolumetrics(any(
        MixedCaseVolumetricParametersForm.class), any());
  }

  @Test
  void getMixedCaseVolumetricsModelAndView_whenProspect_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(mixedCaseVolumetricParametersFormService.getMixedCaseVolumetricsForm(reservoirSubmissionDetail)).thenReturn(
        form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(MixedCaseVolumetricParametersController.class)
            .getMixedCaseVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
        );

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("oilFormationVolumeFactorTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_PROSPECT)),
            entry("oilHydrocarbonSaturationTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT)),
            entry("gasFormationVolumeFactorTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_PROSPECT)),
            entry("gasHydrocarbonSaturationTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT)),
            entry("associatedGasOilRatioLifeOfFieldTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD)),
            entry("condensateGasRatioLifeOfFieldTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD)),
            entry("oilRecoveryFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_PROSPECT)),
            entry("gasRecoveryFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR)),
            entry("oilFormationVolumeFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasFormationVolumeFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedGasOilRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("condensateGasRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilRecoveryFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoveryFactorError", ProbabilityDistributionTypeError.emptyError())
        );
  }

  @Test
  void getMixedCaseVolumetricsModelAndView_whenDiscovery_assertModelProperties() throws Exception {
    var discoverySubmissionDetailId = UUID.randomUUID();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY,
        discoverySubmissionDetailId);
    discoverySubmissionDetail.setApprovedTimestamp(Instant.now());
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    var discoveryReservoirSubmissionDetailId = discoveryReservoirSubmissionDetail.getId();

    when(submissionDetailService.getSubmissionDetail(discoverySubmissionDetailId)).thenReturn(Optional.of(discoverySubmissionDetail));
    when(submissionDetailService.getSubmissionDetailForIdOrError(discoverySubmissionDetailId)).thenReturn(discoverySubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(discoveryReservoirSubmissionDetailId)).thenReturn(discoveryReservoirSubmissionDetail);
    when(mixedCaseVolumetricParametersFormService.getMixedCaseVolumetricsForm(discoveryReservoirSubmissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(MixedCaseVolumetricParametersController.class)
                .getMixedCaseVolumetrics(discoverySubmissionDetailId, discoveryReservoirSubmissionDetailId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(TaskListController.class).getTaskList(discoverySubmissionDetailId)), TaskListController.PAGE_NAME)
        );

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("oilFormationVolumeFactorTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_DISCOVERY)),
            entry("oilHydrocarbonSaturationTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_DISCOVERY)),
            entry("gasFormationVolumeFactorTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_DISCOVERY)),
            entry("gasHydrocarbonSaturationTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_DISCOVERY)),
            entry("associatedGasOilRatioLifeOfFieldTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD)),
            entry("condensateGasRatioLifeOfFieldTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD)),
            entry("oilRecoveryFactorTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_DISCOVERY)),
            entry("gasRecoveryFactorTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR)),
            entry("oilFormationVolumeFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasFormationVolumeFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedGasOilRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("condensateGasRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilRecoveryFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoveryFactorError", ProbabilityDistributionTypeError.emptyError())
        );
  }
}