package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceTypeUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = DiscoveryMixedCaseVolumetricsController.class)
class DiscoveryMixedCaseVolumetricsControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private DiscoveryMixedCaseVolumetricsFormService discoveryMixedCaseVolumetricsFormService;

  @MockBean
  private DiscoveryMixedCaseVolumetricsFormValidator discoveryMixedCaseVolumetricsFormValidator;

  @MockBean
  private DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private MixedCaseOutputsForm form;
  private SubmissionDetail submissionDetail;
  private UUID submissionDetailId;
  private BindingResult bindingResult;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private UUID reservoirSubmissionDetailId;

  @BeforeEach
  void setUp() {
    form = new MixedCaseOutputsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    submissionDetailId = submissionDetail.getId();

    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    reservoirSubmissionDetailId = reservoirSubmissionDetail.getId();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionDetailService.getSubmissionDetail(submissionDetailId)).thenReturn(Optional.of(submissionDetail));
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
  }

  @SecurityTest
  void getMixedCaseOutputs_assertOk() throws Exception {
    when(discoveryMixedCaseVolumetricsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)).thenReturn(new MixedCaseOutputsForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);

    mockMvc.perform(
        get(ReverseRouter.route(on(DiscoveryMixedCaseVolumetricsController.class)
            .getMixedCaseOutputs(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/mixedCaseOutputs"));
  }

  @SecurityTest
  void getMixedCaseOutputs_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryMixedCaseVolumetricsController.class)
                .getMixedCaseOutputs(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getMixedCaseOutputs_andNotDiscovery_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryMixedCaseVolumetricsController.class)
                .getMixedCaseOutputs(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }


  @SecurityTest
  void getMixedCaseOutputs_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryMixedCaseVolumetricsController.class)
                .getMixedCaseOutputs(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveMixedCaseOutputs_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryMixedCaseVolumetricsController.class).saveMixedCaseOutputs(
                submissionDetailId, reservoirSubmissionDetailId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveMixedCaseOutputs_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(discoveryMixedCaseVolumetricsFormValidator.isValid(
        any(MixedCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(validationType))).thenReturn(true);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(DiscoveryMixedCaseVolumetricsController.class)
            .saveMixedCaseOutputs(submissionDetailId, reservoirSubmissionDetailId, null, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId, null))));

    verify(discoveryMixedCaseVolumetricsPersistenceService, times(1)).saveMixedCaseOutputs(any(MixedCaseOutputsForm.class), eq(reservoirSubmissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveMixedCaseOutputs_partialValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(discoveryMixedCaseVolumetricsFormValidator.isValid(
        any(MixedCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(validationType))).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(DiscoveryMixedCaseVolumetricsController.class)
            .saveMixedCaseOutputs(submissionDetailId, reservoirSubmissionDetailId, null, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(discoveryMixedCaseVolumetricsPersistenceService, never()).saveMixedCaseOutputs(any(MixedCaseOutputsForm.class), any());
  }

  @Test
  void getMixedCaseOutputsModelAndView_assertModelProperties() throws Exception {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoverySubmissionDetailId = discoverySubmissionDetail.getId();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    var discoveryReservoirSubmissionDetailId = discoveryReservoirSubmissionDetail.getId();

    when(submissionDetailService.getSubmissionDetail(discoverySubmissionDetailId)).thenReturn(Optional.of(discoverySubmissionDetail));
    when(submissionDetailService.getSubmissionDetailForIdOrError(discoverySubmissionDetailId)).thenReturn(discoverySubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(discoveryReservoirSubmissionDetailId)).thenReturn(discoveryReservoirSubmissionDetail);
    when(discoveryMixedCaseVolumetricsFormService.getMixedCaseOutputsForm(discoveryReservoirSubmissionDetail)).thenReturn(form);


    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryMixedCaseVolumetricsController.class)
                .getMixedCaseOutputs(discoverySubmissionDetailId, discoveryReservoirSubmissionDetailId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(TaskListController.class).getTaskList(discoverySubmissionDetailId)), TaskListController.PAGE_NAME),
            entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(discoverySubmissionDetailId, null)), ReservoirTaskListController.PAGE_TITLE)
        );

    var contingentUncertaintyCategoryPromptOverrides =
        ProbabilityOfExceedanceTypeUtil.getContingentUncertaintyCategoryPromptOverrides();

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("pageTitle", DiscoveryMixedCaseVolumetricsController.PAGE_NAME),
            entry("oilInPlaceTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY)),
            entry("oilRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("associatedGasRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("gasInPlaceTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY)),
            entry("gasRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("associatedCondensateRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("oilEquivalentRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("oilInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        );
  }
}