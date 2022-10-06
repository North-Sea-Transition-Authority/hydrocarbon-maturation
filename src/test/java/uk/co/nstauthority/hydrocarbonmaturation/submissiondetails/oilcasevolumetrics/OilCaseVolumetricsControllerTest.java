package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

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
@ContextConfiguration(classes = OilCaseVolumetricsController.class)
class OilCaseVolumetricsControllerTest extends AbstractControllerTest {

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private OilCaseVolumetricsFormService oilCaseVolumetricsFormService;

  @MockBean
  private OilCaseVolumetricsPersistenceService oilCaseVolumetricsPersistenceService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private static OilCaseVolumetricsForm form;
  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;
  private static BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    form = new OilCaseVolumetricsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getOilCaseVolumetrics_assertOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(submissionDetail)).thenReturn(new OilCaseVolumetricsForm());

    mockMvc.perform(
            get(ReverseRouter.route(on(OilCaseVolumetricsController.class).getOilCaseVolumetrics(submissionDetailId, null))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/oilCaseVolumetrics"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOilCaseVolumetrics_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(oilCaseVolumetricsFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(OilCaseVolumetricsController.class).saveOilCaseVolumetrics(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(oilCaseVolumetricsPersistenceService, times(1)).saveOilCaseVolumetrics(any(OilCaseVolumetricsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOilCaseVolumetrics_partialValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(oilCaseVolumetricsFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(OilCaseVolumetricsController.class).saveOilCaseVolumetrics(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(oilCaseVolumetricsPersistenceService, never()).saveOilCaseVolumetrics(any(OilCaseVolumetricsForm.class), any());
  }

  @Test
  void getGasCaseVolumetricsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(submissionDetail)).thenReturn(
        form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(OilCaseVolumetricsController.class)
                .getOilCaseVolumetrics(submissionDetailId, null))))
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
            entry("oilHydrocarbonSaturationTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION)),
            entry("associatedGasOilRatioLifeOfFieldTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD)),
            entry("formationVolumeFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY)),
            entry("oilRecoveryFactorTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR)),
            entry("oilHydrocarbonSaturationError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedGasOilRatioLifeOfFieldError", ProbabilityDistributionTypeError.emptyError()),
            entry("formationVolumeFactorError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilRecoveryFactorError", ProbabilityDistributionTypeError.emptyError())
        );
  }

}