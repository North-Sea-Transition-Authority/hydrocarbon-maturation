package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

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

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = GasCaseVolumetricsController.class)
class GasCaseVolumetricsControllerTest extends AbstractControllerTest {

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private GasCaseVolumetricsFormService gasCaseVolumetricsFormService;

  @MockBean
  private GasCaseVolumetricsPersistenceService gasCaseVolumetricsPersistenceService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private static GasCaseVolumetricsForm form;
  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;
  private static BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    form = new GasCaseVolumetricsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getGasCaseVolumetrics_assertOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(submissionDetail)).thenReturn(new GasCaseVolumetricsForm());

    mockMvc.perform(
            get(ReverseRouter.route(on(GasCaseVolumetricsController.class).getGasCaseVolumetrics(submissionDetailId, null))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/gasCaseVolumetrics"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGasCaseVolumetrics_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(gasCaseVolumetricsFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(GasCaseVolumetricsController.class).saveGasCaseVolumetrics(submissionDetailId, form, bindingResult, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(gasCaseVolumetricsPersistenceService, times(1)).saveGasCaseVolumetrics(any(GasCaseVolumetricsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGasCaseVolumetrics_partialValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(gasCaseVolumetricsFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(GasCaseVolumetricsController.class).saveGasCaseVolumetrics(submissionDetailId, form, bindingResult, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(gasCaseVolumetricsPersistenceService, never()).saveGasCaseVolumetrics(any(GasCaseVolumetricsForm.class), any());
  }

  @Test
  void getGasCaseVolumetricsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(submissionDetail)).thenReturn(
        form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(GasCaseVolumetricsController.class).getGasCaseVolumetrics(submissionDetailId, null))))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId)), ProspectTaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("gasHydrocarbonSaturationTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION)),
            entry("condensateGasRatioLifeOfFieldTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD)),
            entry("gasExpansionFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR)),
            entry("gasRecoveryFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR)),
            entry("gasHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("condensateGasRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasExpansionFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("gasRecoveryFactorError", ProbabilityDistributionTypeError.emptyError())
        );
  }
}