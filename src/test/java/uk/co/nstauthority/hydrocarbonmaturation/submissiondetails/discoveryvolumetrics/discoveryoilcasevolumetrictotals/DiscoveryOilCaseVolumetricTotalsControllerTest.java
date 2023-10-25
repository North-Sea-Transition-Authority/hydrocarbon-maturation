package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = DiscoveryOilCaseVolumetricTotalsController.class)
class DiscoveryOilCaseVolumetricTotalsControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private DiscoveryOilCaseVolumetricTotalsService discoveryOilCaseVolumetricTotalsService;

  @MockBean
  private DiscoveryOilCaseVolumetricTotalsFormValidator discoveryOilCaseVolumetricTotalsFormValidator;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private OilCaseOutputsForm form;
  private BindingResult bindingResult;
  private SubmissionDetail submissionDetail;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    form = new OilCaseOutputsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    when(submissionDetailService.getSubmissionDetail(submissionDetailId)).thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
  }

  @SecurityTest
  void getOilCaseVolumetricTotals_whenUserIsResponsibleOrganisation_assertOk() throws Exception {
    when(discoveryOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(new OilCaseOutputsForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class).getDiscoveryOilCaseVolumetricTotals(
                submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/oilCaseOutputs"));
  }

  @SecurityTest
  void getOilCaseVolumetricTotals_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class).getDiscoveryOilCaseVolumetricTotals(
                submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOilCaseVolumetricTotals_andTypeIsNotDiscovery_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class).getDiscoveryOilCaseVolumetricTotals(
                submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }


  @SecurityTest
  void getOilCaseVolumetricTotals_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class)
                .getDiscoveryOilCaseVolumetricTotals(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveOilCaseVolumetricTotals_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class).saveDiscoveryOilCaseVolumetricTotals(
                submissionDetailId,  null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOilCaseVolumetricTotals_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(discoveryOilCaseVolumetricTotalsFormValidator.isValid(any(OilCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(validationType))).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class)
                .saveDiscoveryOilCaseVolumetricTotals(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(discoveryOilCaseVolumetricTotalsService, times(1)).saveDiscoveryOilCaseVolumetricTotals(any(OilCaseOutputsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOilCaseVolumetricTotals_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(discoveryOilCaseVolumetricTotalsFormValidator.isValid(any(OilCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(validationType))).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class)
                .saveDiscoveryOilCaseVolumetricTotals(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(discoveryOilCaseVolumetricTotalsService, never()).saveDiscoveryOilCaseVolumetricTotals(any(OilCaseOutputsForm.class), any(SubmissionDetail.class));
  }

  @Test
  void getOilCaseVolumetricTotalsModelAndView_assertModelProperties() throws Exception {
    when(discoveryOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryOilCaseVolumetricTotalsController.class)
                .getDiscoveryOilCaseVolumetricTotals(submissionDetailId)))
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

    var contingentUncertaintyCategoryPromptOverrides =
        ProbabilityOfExceedanceTypeUtil.getContingentUncertaintyCategoryPromptOverrides();

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("pageTitle", DiscoveryOilCaseVolumetricTotalsController.PAGE_NAME),
            entry("oilInPlaceTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY)),
            entry("associatedGasRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("oilEquivalentRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("oilRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        );
  }
}
