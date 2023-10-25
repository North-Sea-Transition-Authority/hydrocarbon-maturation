package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetricparameters;

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
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = FieldOilVolumetricParametersController.class)
class FieldOilVolumetricParametersControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private FieldOilVolumetricParametersService fieldOilVolumetricParametersService;

  @MockBean
  private OilCaseVolumetricParametersFormValidator oilCaseVolumetricParametersFormValidator;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private OilCaseVolumetricParametersForm form;
  private SubmissionDetail submissionDetail;
  private UUID submissionDetailId;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private UUID reservoirSubmissionDetailId;

  @BeforeEach
  void setUp() {
    form = new OilCaseVolumetricParametersForm();
    submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_CSR);
    submissionDetailId = submissionDetail.getId();

    when(submissionDetailService.getSubmissionDetail(UUID.fromString(submissionDetailId.toString())))
        .thenReturn(Optional.of(submissionDetail));
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    reservoirSubmissionDetailId = reservoirSubmissionDetail.getId();
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);

    var field = new Field();
    field.setResponsibleOrganisationId(1);
    when(fieldPersistenceService.getFieldByResourceOrThrow(submissionDetail.getSubmission().getResource()))
        .thenReturn(field);
  }

  @SecurityTest
  void getOilVolumetrics_assertOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(fieldOilVolumetricParametersService.getOilVolumetricsForm(reservoirSubmissionDetail)).thenReturn(new OilCaseVolumetricParametersForm());
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);

    mockMvc.perform(
        get(ReverseRouter.route(on(FieldOilVolumetricParametersController.class)
            .getOilVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/oilCaseVolumetrics"));
  }

  @SecurityTest
  void getOilVolumetrics_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldOilVolumetricParametersController.class)
                .getOilVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOilCaseVolumetrics_andTypeIsNotField_assertForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldOilVolumetricParametersController.class)
                .getOilVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOilVolumetrics_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldOilVolumetricParametersController.class)
                .getOilVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveOilVolumetrics_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(FieldOilVolumetricParametersController.class).saveOilVolumetrics(
                submissionDetailId, reservoirSubmissionDetailId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOilVolumetrics_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(oilCaseVolumetricParametersFormValidator.isValid(any(), any(), eq(submissionDetail), any())).thenReturn(true);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(FieldOilVolumetricParametersController.class)
            .saveOilVolumetrics(submissionDetailId, reservoirSubmissionDetailId, null, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId, null))));

    verify(fieldOilVolumetricParametersService, times(1)).saveOilVolumetrics(any(OilCaseVolumetricParametersForm.class), eq(reservoirSubmissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOilVolumetrics_partialValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(oilCaseVolumetricParametersFormValidator.isValid(any(), any(), eq(submissionDetail), any())).thenReturn(false);

    mockMvc.perform(
        post(ReverseRouter.route(on(FieldOilVolumetricParametersController.class)
            .saveOilVolumetrics(submissionDetailId, reservoirSubmissionDetailId, null, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(fieldOilVolumetricParametersService, never()).saveOilVolumetrics(any(OilCaseVolumetricParametersForm.class), any());
  }

  @Test
  void getOilVolumetricsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetail(reservoirSubmissionDetailId)).thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
    when(fieldOilVolumetricParametersService.getOilVolumetricsForm(reservoirSubmissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(FieldOilVolumetricParametersController.class)
                .getOilVolumetrics(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME),
            entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId, null)), ReservoirTaskListController.PAGE_TITLE)
        );

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("oilHydrocarbonSaturationTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD)),
            entry("associatedGasOilRatioLifeOfFieldTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT)),
            entry("formationVolumeFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD)),
            entry("oilRecoveryFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD)),
            entry("oilHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedGasOilRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("formationVolumeFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilRecoveryFactorError", ProbabilityDistributionTypeError.emptyError())
        );
  }
}