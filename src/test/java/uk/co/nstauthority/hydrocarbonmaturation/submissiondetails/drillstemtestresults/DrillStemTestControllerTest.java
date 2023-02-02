package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@ContextConfiguration(classes = DrillStemTestController.class)
class DrillStemTestControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private DrillStemTestFormService drillStemTestFormService;

  @MockBean
  private DrillStemTestInitialPersistenceService drillStemTestPersistenceService;

  @MockBean
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  private DrillStemTestInitialForm form;
  private BindingResult bindingResult;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    form = new DrillStemTestInitialForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetailId = submissionDetail.getId();
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
  }

  @SecurityTest
  void getDrillStemTestPage_assertStatusOk() throws Exception {
    when(drillStemTestFormService.getDrillStemTestInitialForm(submissionDetail)).thenReturn(form);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestController.class).getDrillStemTestPage(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/drillstemtest/drillStemTestInitial"));
  }

  @SecurityTest
  void getDrillStemTestPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestController.class).getDrillStemTestPage(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getDrillStemTestPage_andNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestController.class).getDrillStemTestPage(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getDrillStemTestPage_whenFoundResults_assertRedirect() throws Exception {
    when(drillStemTestFormService.getDrillStemTestInitialForm(submissionDetail)).thenReturn(form);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsBySubmissionDetail(submissionDetail))
        .thenReturn(List.of(new DrillStemTestResult()));
    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestController.class).getDrillStemTestPage(submissionDetail.getId(), null)))
                .with(user(USER)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(DrillStemTestSummaryController.class)
            .getDrillStemTestSummaryPage(submissionDetail.getId()))));
  }

  @Test
  void saveDrillStemTestPage_whenValidForm_assertRedirection() throws Exception {
    when(drillStemTestFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(drillStemTestPersistenceService, times(1))
        .saveInitialDrillStemTest(any(DrillStemTestInitialForm.class), eq(submissionDetail));
  }

  @SecurityTest
  void saveDrillStemTestPage_whenValidForm_AndYesDrillStemResult_assertRedirection() throws Exception {
    when(drillStemTestFormService.validate(any(), any(), any(), any())).thenReturn(ReverseRouter.emptyBindingResult());
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    var form = new DrillStemTestInitialForm();
    form.setHasDrillStemTestResult(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf())
                .flashAttr("form", form))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(DrillStemTestResultController.class)
            .getCreateNewDrillStemTestResultPage(submissionDetailId))));

    verify(drillStemTestPersistenceService, times(1))
        .saveInitialDrillStemTest(any(DrillStemTestInitialForm.class), eq(submissionDetail));
  }

  @SecurityTest
  void saveDrillStemTestPage_whenInvalidForm_assertStatusOk() throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(drillStemTestFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(drillStemTestPersistenceService, never()).saveInitialDrillStemTest(any(), eq(submissionDetail));
  }

  @SecurityTest
  void getDrillStemTestPage_whenUserNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestController.class)
                .getDrillStemTestPage(submissionDetailId, null)))
                .with(user(USER))
                .flashAttr("form", form))
        .andExpect(status().isForbidden());

    verifyNoInteractions(drillStemTestPersistenceService);
  }

  @SecurityTest
  void saveDrillStemTestPage_whenUserNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
    verifyNoInteractions(drillStemTestPersistenceService);
  }

  @Test
  void getDrillStemTestModelAndView_assertModelProperties() throws Exception {
    var drillStemTest = mock(DrillStemTest.class);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    when(drillStemTestPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest))
        .thenReturn(List.of());
    var form = new DrillStemTestInitialForm();
    when(drillStemTestFormService.getDrillStemTestInitialForm(submissionDetail))
        .thenReturn(form);

    ModelMap modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(DrillStemTestController.class)
                    .getDrillStemTestPage(submissionDetailId, null)))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("actionUrl", ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetailId, null, null))),
            entry("form", form))
        .containsKeys("breadcrumbsMap");
  }
}