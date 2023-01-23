package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = OilCaseOutputsController.class)
class OilCaseOutputsControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private OilCaseOutputsFormService oilCaseOutputsFormService;

  @MockBean
  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private OilCaseOutputsForm form;
  private UUID submissionDetailId;
  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    form = new OilCaseOutputsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getOilCaseOutputs_assertOk() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(
        get(ReverseRouter.route(on(OilCaseOutputsController.class).getOilCaseOutputs(submissionDetailId, null)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOilCaseOutputs_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(oilCaseOutputsFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(OilCaseOutputsController.class).saveOilCaseOutputs(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(oilCaseOutputsPersistenceService, times(1)).saveOilCaseOutputs(any(OilCaseOutputsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOilCaseOutputs_partialValidation_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(oilCaseOutputsFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(OilCaseOutputsController.class).saveOilCaseOutputs(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(oilCaseOutputsPersistenceService, never()).saveOilCaseOutputs(any(OilCaseOutputsForm.class), any());
  }

  @Test
  void getOilCaseOutputsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(oilCaseOutputsFormService.getOilCaseOutputsForm(submissionDetail)).thenReturn(
        form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(OilCaseOutputsController.class)
            .getOilCaseOutputs(submissionDetailId, null)))
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
            entry("oilInPlaceTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE)),
            entry("associatedGasRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES)),
            entry("oilEquivalentRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES)),
            entry("oilRecoverableResourcesTemplate", probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES)),
            entry("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("associatedGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilInPlaceError", ProbabilityDistributionTypeError.emptyError()),
            entry("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        );
  }

}