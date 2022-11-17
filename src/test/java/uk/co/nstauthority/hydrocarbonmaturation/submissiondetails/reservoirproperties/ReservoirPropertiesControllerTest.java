package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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
import org.junit.jupiter.api.BeforeAll;
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

@ContextConfiguration(classes = ReservoirPropertiesController.class)
class ReservoirPropertiesControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private ReservoirPropertiesFormService reservoirPropertiesFormService;

  @MockBean
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private ReservoirPropertiesForm form;
  private BindingResult bindingResult;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @BeforeEach
  void setup() {
    form = new ReservoirPropertiesForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void getReservoirProperties_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(new ReservoirPropertiesForm());

    mockMvc.perform(
        get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/reservoirProperties"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveReservoirProperties_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(reservoirPropertiesFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(ReservoirPropertiesController.class).saveReservoirProperties(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(reservoirPropertiesPersistenceService, times(1))
        .saveReservoirProperty(any(ReservoirPropertiesForm.class), eq(submissionDetail));
    verify(reservoirPropertiesFormService, times(1))
        .validate(any(), any(), eq(validationType));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveReservoirProperties_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(reservoirPropertiesFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(ReservoirPropertiesController.class).saveReservoirProperties(submissionDetailId, form, bindingResult, ValidationType.PARTIAL)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(reservoirPropertiesFormService, times(1))
        .validate(any(), any(), eq(validationType));
    verify(reservoirPropertiesPersistenceService, never())
        .saveReservoirProperty(any(), any());
  }

  @Test
  void getReservoirPropertiesModelAndView_assertModelProperties() throws Exception {
    var form = new ReservoirPropertiesForm();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetailId)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("averageNetPayThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToHydrocarbonContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT_DISCOVERY))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY))
        .containsEntry("depthToHydrocarbonContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT));
  }
}