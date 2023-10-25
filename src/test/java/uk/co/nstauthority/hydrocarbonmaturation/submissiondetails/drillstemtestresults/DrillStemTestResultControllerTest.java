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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = DrillStemTestResultController.class)
class DrillStemTestResultControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private DrillStemTestResultFormService drillStemTestResultFormService;

  @MockBean
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @MockBean
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  private DrillStemTestResultForm form;
  private BindingResult bindingResult;
  private UUID drillStemTestId;
  private UUID submissionDetailId;
  private UUID reservoirSubmissionDetailId;
  private DrillStemTest drillStemTest;

  @BeforeEach
  void setUp() {
    form = new DrillStemTestResultForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    reservoirSubmissionDetailId = reservoirSubmissionDetail.getId();

    drillStemTestId = UUID.randomUUID();
    drillStemTest = new DrillStemTest();
    drillStemTest.setId(drillStemTestId);
  }

  @SecurityTest
  void getDrillStemTestResultPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);

    var drillStemTestResult = mock(DrillStemTestResult.class);
    when(drillStemTestResultFormService.getDrillStemTestResultForm(drillStemTestResult)).thenReturn(form);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestId))
        .thenReturn(drillStemTestResult);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestResultController.class)
                .getDrillStemTestResultPage(submissionDetailId, reservoirSubmissionDetailId, drillStemTestId)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getDrillStemTestResultPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestResultController.class)
                .getDrillStemTestResultPage(submissionDetailId, reservoirSubmissionDetailId, drillStemTestId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getCreateNewDrillStemTestResultPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
    setUpWhenUserIsResponsibleTeam_passSecurity();
    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestResultController.class)
                .getCreateNewDrillStemTestResultPage(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/drillstemtest/drillStemTestResult"));
  }

  @SecurityTest
  void getDrillStemTestResultPage_whenNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestResultController.class)
                .getDrillStemTestResultPage(submissionDetailId, reservoirSubmissionDetailId, drillStemTestId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getCreateNewDrillStemTestResultPage_whenNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestResultController.class)
                .getCreateNewDrillStemTestResultPage(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void updateDrillStemTestResultDetails_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(drillStemTestResultFormService.validateForUpdate(any(), any(), any(), any()))
        .thenReturn(bindingResult);
    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail))
        .thenReturn(drillStemTest);

    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setId(UUID.randomUUID());
    drillStemTestResult.setDrillStemTest(drillStemTest);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestId))
        .thenReturn(drillStemTestResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestResultController.class)
                .updateDrillStemTestResultDetails(submissionDetailId, reservoirSubmissionDetailId, drillStemTestId,
                    null, null)))
                .with(user(USER))
                .with(csrf())
                .params(ControllerTestUtils.validationPostParams(validationType)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(DrillStemTestSummaryController.class)
            .getDrillStemTestSummaryPage(submissionDetailId, reservoirSubmissionDetailId))));

    verify(drillStemTestResultPersistenceService, times(1))
        .saveDrillStemTestResult(any(DrillStemTestResultForm.class), eq(drillStemTest), eq(drillStemTestId));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void updateDrillStemTestResultDetails_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(drillStemTestResultFormService.validateForUpdate(any(), any(), any(), any()))
        .thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail))
        .thenReturn(drillStemTest);
    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setId(UUID.randomUUID());
    drillStemTestResult.setDrillStemTest(drillStemTest);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestId))
        .thenReturn(drillStemTestResult);
    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestResultController.class)
                .updateDrillStemTestResultDetails(submissionDetailId, reservoirSubmissionDetailId, drillStemTestId, null, null)))
                .with(user(USER))
                .with(csrf())
                .params(ControllerTestUtils.validationPostParams(validationType)))
        .andExpect(status().isOk());

    verify(drillStemTestResultPersistenceService, never()).saveDrillStemTestResult(any(), any(), any());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveNewDrillStemTestResult_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(drillStemTestResultFormService.validate(any(), any(), any()))
        .thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail))
        .thenReturn(drillStemTest);

    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setId(UUID.randomUUID());
    drillStemTestResult.setDrillStemTest(drillStemTest);
    when(drillStemTestResultPersistenceService.createNewDrillStemTestResult(any()))
        .thenReturn(drillStemTestResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestResultController.class)
                .saveNewDrillStemTestResult(submissionDetailId, reservoirSubmissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf())
                .params(ControllerTestUtils.validationPostParams(validationType)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(DrillStemTestSummaryController.class)
            .getDrillStemTestSummaryPage(submissionDetailId, reservoirSubmissionDetailId))));

    verify(drillStemTestResultPersistenceService, times(1))
        .saveDrillStemTestResult(any(DrillStemTestResultForm.class), eq(drillStemTest), eq(drillStemTestResult.getId()));
  }

  @Test
  void getDrillStemTestResultModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var drillStemTestResult = mock(DrillStemTestResult.class);
    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestId))
        .thenReturn(drillStemTestResult);
    var form = new DrillStemTestResultForm();
    when(drillStemTestResultFormService.getDrillStemTestResultForm(drillStemTestResult))
        .thenReturn(form);


    ModelMap modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(DrillStemTestResultController.class)
                    .getDrillStemTestResultPage(submissionDetailId, reservoirSubmissionDetailId, drillStemTestId)))
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
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);

    var drillStemTestResult = mock(DrillStemTestResult.class);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultByIdOrThrow(drillStemTestId))
        .thenReturn(drillStemTestResult);

    ModelMap modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(DrillStemTestResultController.class)
                    .getCreateNewDrillStemTestResultPage(submissionDetailId, reservoirSubmissionDetailId)))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView()).getModelMap();

    assertEquals(DrillStemTestResultForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .containsKeys("breadcrumbsMap", "form");
  }
}