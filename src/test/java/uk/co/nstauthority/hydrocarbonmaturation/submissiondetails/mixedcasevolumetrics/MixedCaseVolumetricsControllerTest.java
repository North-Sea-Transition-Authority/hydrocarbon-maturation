package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = MixedCaseVolumetricsController.class)
class MixedCaseVolumetricsControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private MixedCaseVolumetricsFormService mixedCaseVolumetricsFormService;

  @MockBean
  private MixedCaseVolumetricsPersistenceService mixedCaseVolumetricsPersistenceService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private static MixedCaseVolumetricsForm form;
  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;
  private static BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    form = new MixedCaseVolumetricsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getMixedCaseVolumetrics_assertOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(mixedCaseVolumetricsFormService.getMixedCaseVolumetricsForm(submissionDetail)).thenReturn(new MixedCaseVolumetricsForm());

    mockMvc.perform(
        get(ReverseRouter.route(on(MixedCaseVolumetricsController.class).getMixedCaseVolumetrics(submissionDetailId, null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/mixedCaseVolumetrics"));
  }


  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveMixedCaseVolumetrics_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(mixedCaseVolumetricsFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(MixedCaseVolumetricsController.class).saveMixedCaseVolumetrics(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(mixedCaseVolumetricsPersistenceService, times(1)).saveMixedCaseVolumetrics(any(MixedCaseVolumetricsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveMixedCaseVolumetrics_partialValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(mixedCaseVolumetricsFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(MixedCaseVolumetricsController.class).saveMixedCaseVolumetrics(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(mixedCaseVolumetricsPersistenceService, never()).saveMixedCaseVolumetrics(any(MixedCaseVolumetricsForm.class), any());
  }

  @Test
  void getMixedCaseVolumetricsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(mixedCaseVolumetricsFormService.getMixedCaseVolumetricsForm(submissionDetail)).thenReturn(
        form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(MixedCaseVolumetricsController.class).getMixedCaseVolumetrics(submissionDetailId, null)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
        );

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("formationVolumeFactorTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY)),
            entry("oilHydrocarbonSaturationTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION)),
            entry("gasHydrocarbonSaturationTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION)),
            entry("associatedGasOilRatioLifeOfFieldTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD)),
            entry("gasExpansionFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR)),
            entry("condensateGasRatioLifeOfFieldTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD)),
            entry("oilRecoveryFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR)),
            entry("gasRecoveryFactorTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR)),
            entry("formationVolumeFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedGasOilRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasExpansionFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("condensateGasRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilRecoveryFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoveryFactorError", ProbabilityDistributionTypeError.emptyError())
        );
  }
}