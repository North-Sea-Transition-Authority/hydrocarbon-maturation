package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import java.util.Objects;
import java.util.Optional;
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
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = DrillStemTestSummaryController.class)
class DrillStemTestSummaryControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private DrillStemTestFormService drillStemTestFormService;

  @MockBean
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @MockBean
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  private DrillStemTestSummaryForm form;
  private BindingResult bindingResult;
  private UUID submissionDetailId;
  private UUID drillStemTestId;

  private DrillStemTest drillStemTest;

  @BeforeEach
  void setUp() {
    form = new DrillStemTestSummaryForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetailId = submissionDetail.getId();
    drillStemTestId = UUID.randomUUID();
    drillStemTest = new DrillStemTest();
    drillStemTest.setId(drillStemTestId);
  }

  @SecurityTest
  void getDrillStemTestSummaryPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(drillStemTestFormService.getDrillStemTestSummaryForm(submissionDetail)).thenReturn(form);

    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestSummaryController.class)
                .getDrillStemTestSummaryPage(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getDrillStemTestSummaryPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestSummaryController.class)
                .getDrillStemTestSummaryPage(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveDrillStemTestSummaryPage_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(drillStemTestFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(drillStemTest));
    when(drillStemTestInitialPersistenceService.saveDrillStemTest(any(), eq(submissionDetail)))
        .thenReturn(drillStemTest);

    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setId(UUID.randomUUID());

    when(drillStemTestResultPersistenceService.createNewDrillStemTestResult(drillStemTest))
        .thenReturn(drillStemTestResult);

    var summaryForm = new DrillStemTestSummaryForm();
    summaryForm.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.YES_NOW);
    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestSummaryController.class)
                .saveDrillStemTestSummary(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf())
                .params(ControllerTestUtils.validationPostParams(validationType))
                .flashAttr("form", summaryForm))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(DrillStemTestResultController.class)
            .getCreateNewDrillStemTestResultPage(submissionDetailId))));

    verify(drillStemTestInitialPersistenceService, times(1))
        .saveDrillStemTest(any(DrillStemTestSummaryForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveDrillStemTestSummaryPage_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(drillStemTestFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestSummaryController.class)
                .saveDrillStemTestSummary(submissionDetailId,null, null)))
                .with(user(USER))
                .with(csrf())
                .params(ControllerTestUtils.validationPostParams(validationType)))
        .andExpect(status().isOk());

    verify(drillStemTestResultPersistenceService, never()).createNewDrillStemTestResult(any());
    verify(drillStemTestInitialPersistenceService, never()).saveInitialDrillStemTest(any(), any());
  }

  @Test
  void getDrillStemTestSummaryModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    var form = new DrillStemTestSummaryForm();
    when(drillStemTestFormService.getDrillStemTestSummaryForm(submissionDetail))
        .thenReturn(form);

    ModelMap modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(DrillStemTestSummaryController.class)
                    .getDrillStemTestSummaryPage(submissionDetailId)))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView()).getModelMap();

    assertEquals(DrillStemTestSummaryForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("actionUrl",
                ReverseRouter.route(on(DrillStemTestController.class)
                    .saveDrillStemTestDetails(submissionDetailId, null, null))),
            entry("drillStemTestSummaryOptions",
                DisplayableEnumOptionUtil.getDisplayableOptions(DrillStemTestSummaryOptions.class)))
        .containsKeys("breadcrumbsMap");
  }
}