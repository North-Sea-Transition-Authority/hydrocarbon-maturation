package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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

import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = DrillStemTestResultController.class)
class DrillStemTestResultControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private DrillStemTestResultFormService drillStemTestResultFormService;

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @MockBean
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @Autowired
  private ControllerHelperService controllerHelperService;

  private DrillStemTestResultForm form;
  private BindingResult bindingResult;
  private SubmissionDetail submissionDetail;
  private UUID drillStemTestId;

  private DrillStemTest drillStemTest;

  @BeforeEach
  void setUp() {
    form = new DrillStemTestResultForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    drillStemTestId = UUID.randomUUID();
    drillStemTest = new DrillStemTest();
    drillStemTest.setId(drillStemTestId);
  }

  @Test
  void getDrillStemTestResultPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    var drillStemTestResult = mock(DrillStemTestResult.class);

    when(drillStemTestResultFormService.getDrillStemTestResultForm(drillStemTestResult)).thenReturn(form);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestId))
        .thenReturn(drillStemTestResult);

    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestResultController.class)
                .getDrillStemTestResultPage(submissionDetail.getId(), drillStemTestId)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  void getCreateNewDrillStemTestResultPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestResultController.class)
                .getCreateNewDrillStemTestResultPage(submissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/drillstemtest/drillStemTestResult"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void updateDrillStemTestResultDetails_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(drillStemTestResultFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestBySubmissionDetailOrThrow(submissionDetail))
        .thenReturn(drillStemTest);

    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setId(UUID.randomUUID());
    drillStemTestResult.setDrillStemTest(drillStemTest);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestId))
        .thenReturn(drillStemTestResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestResultController.class)
                .updateDrillStemTestResultDetails(submissionDetail.getId(), drillStemTestId, null, null, null)))
                .with(user(USER))
                .with(csrf())
                .params(ControllerTestUtils.validationPostParams(validationType)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId()))));

    verify(drillStemTestResultPersistenceService, times(1))
        .saveDrillStemTestResult(any(DrillStemTestResultForm.class), eq(drillStemTest), eq(drillStemTestId));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void updateDrillStemTestResultDetails_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(drillStemTestResultFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestBySubmissionDetailOrThrow(submissionDetail))
        .thenReturn(drillStemTest);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestResultController.class)
                .updateDrillStemTestResultDetails(submissionDetail.getId(), drillStemTestId, null, null, null)))
                .with(user(USER))
                .with(csrf())
                .params(ControllerTestUtils.validationPostParams(validationType)))
        .andExpect(status().isOk());

    verify(drillStemTestResultPersistenceService, never()).saveDrillStemTestResult(any(), any(), any());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveNewDrillStemTestResult_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(drillStemTestResultFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestBySubmissionDetailOrThrow(submissionDetail))
        .thenReturn(drillStemTest);

    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setId(UUID.randomUUID());
    drillStemTestResult.setDrillStemTest(drillStemTest);
    when(drillStemTestResultPersistenceService.createNewDrillStemTestResult(any()))
        .thenReturn(drillStemTestResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestResultController.class)
                .saveNewDrillStemTestResult(submissionDetail.getId(), null, null, null)))
                .with(user(USER))
                .with(csrf())
                .params(ControllerTestUtils.validationPostParams(validationType)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId()))));

    verify(drillStemTestResultPersistenceService, times(1))
        .saveDrillStemTestResult(any(DrillStemTestResultForm.class), eq(drillStemTest), eq(drillStemTestResult.getId()));
  }

  @Test
  void getDrillStemTestResultModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    var drillStemTestResult = mock(DrillStemTestResult.class);

    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestId))
        .thenReturn(drillStemTestResult);
    var form = new DrillStemTestResultForm();
    when(drillStemTestResultFormService.getDrillStemTestResultForm(drillStemTestResult))
        .thenReturn(form);


    ModelMap modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(DrillStemTestResultController.class)
                    .getDrillStemTestResultPage(submissionDetail.getId(), drillStemTestId)))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView()).getModelMap();

    assertEquals(DrillStemTestResultForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("form", form))
        .containsKeys("breadcrumbsMap");
  }

  @Test
  void getCreateNewDrillStemTestResultPage_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    var drillStemTestResult = mock(DrillStemTestResult.class);

    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestId))
        .thenReturn(drillStemTestResult);

    ModelMap modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(DrillStemTestResultController.class)
                    .getCreateNewDrillStemTestResultPage(submissionDetail.getId())))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView()).getModelMap();

    assertEquals(DrillStemTestResultForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .containsKeys("breadcrumbsMap", "form");
  }
}