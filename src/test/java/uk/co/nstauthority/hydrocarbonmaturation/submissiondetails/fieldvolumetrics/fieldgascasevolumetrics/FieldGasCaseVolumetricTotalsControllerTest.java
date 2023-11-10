package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = FieldGasCaseVolumetricTotalsController.class)
class FieldGasCaseVolumetricTotalsControllerTest extends AbstractSubmissionControllerTest {
  @MockBean
  private FieldGasCaseVolumetricTotalsService fieldGasCaseVolumetricTotalsService;

  @MockBean
  private FieldGasCaseVolumetricTotalsFormValidator fieldGasCaseVolumetricTotalsFormValidator;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private GasCaseOutputsForm form;
  private BindingResult bindingResult;
  private SubmissionDetail submissionDetail;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    form = new GasCaseOutputsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    when(submissionDetailService.getSubmissionDetail(submissionDetailId)).thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
  }

  @SecurityTest
  void getGasVolumetricTotals_whenUserIsResponsibleOrganisation_assertOk() throws Exception {
    when(fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(new GasCaseOutputsForm());
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class).getFieldGasCaseVolumetricTotals(
                submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/gasCaseOutputs"));
  }

  @SecurityTest
  void getGasVolumetricTotals_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class).getFieldGasCaseVolumetricTotals(
                submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getGasVolumetricTotals_andTypeIsNotDiscovery_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class).getFieldGasCaseVolumetricTotals(
                submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getGasVolumetricTotals_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class)
                .getFieldGasCaseVolumetricTotals(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveGasVolumetricTotals_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class).saveFieldGasCaseVolumetricTotals(
                submissionDetailId,  null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGasVolumetricTotals_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(fieldGasCaseVolumetricTotalsFormValidator.isValid(any(GasCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(validationType))).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class)
                .saveFieldGasCaseVolumetricTotals(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(fieldGasCaseVolumetricTotalsService, never()).saveFieldGasCaseVolumetricTotals(any(GasCaseOutputsForm.class), any(SubmissionDetail.class));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGasVolumetricTotals_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(fieldGasCaseVolumetricTotalsFormValidator.isValid(any(GasCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(validationType))).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class)
                .saveFieldGasCaseVolumetricTotals(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(fieldGasCaseVolumetricTotalsService, never()).saveFieldGasCaseVolumetricTotals(any(GasCaseOutputsForm.class), any(SubmissionDetail.class));

  }

  @Test
  void getOilVolumetricTotalsModelAndView_isCsrField_assertModelProperties() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    when(fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class)
                .getFieldGasCaseVolumetricTotals(submissionDetailId)))
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
            entry("resourceType", submissionDetail.getSubmission().getResource().getType().toString()),
            entry("submissionType", submissionDetail.getSubmissionType().getEnumName()),
            entry("pageTitle", FieldGasCaseVolumetricTotalsController.PAGE_NAME),
            entry("gasInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoverableReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedCondensateRecoverableReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedCondensateRecoverableReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilEquivalentRecoverableReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("naturalGasLiquidsResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("naturalGasLiquidsReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("salesGasResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("salesGasReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasInPlaceTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD)),
            entry("gasRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("associatedCondensateRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("oilEquivalentRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("naturalGasLiquidsResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("salesGasResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides))
        );
  }

  @Test
  void getOilVolumetricTotalsModelAndView_isNotCsrField_assertModelProperties() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    when(fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(FieldGasCaseVolumetricTotalsController.class)
                .getFieldGasCaseVolumetricTotals(submissionDetailId)))
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
            entry("resourceType", submissionDetail.getSubmission().getResource().getType().toString()),
            entry("submissionType", submissionDetail.getSubmissionType().getEnumName()),
            entry("pageTitle", FieldGasCaseVolumetricTotalsController.PAGE_NAME),
            entry("gasInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoverableReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedCondensateRecoverableReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedCondensateRecoverableReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilEquivalentRecoverableReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("naturalGasLiquidsResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("naturalGasLiquidsReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("salesGasResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("salesGasReservesError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasInPlaceTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD)),

            entry("gasRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("associatedCondensateRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("oilEquivalentRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("naturalGasLiquidsResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("salesGasResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),

            entry("gasRecoverableReservesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("associatedCondensateRecoverableReservesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("oilEquivalentRecoverableReservesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("naturalGasLiquidsReservesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
                contingentUncertaintyCategoryPromptOverrides)),
            entry("salesGasReservesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD,
                contingentUncertaintyCategoryPromptOverrides))
        );
  }
}