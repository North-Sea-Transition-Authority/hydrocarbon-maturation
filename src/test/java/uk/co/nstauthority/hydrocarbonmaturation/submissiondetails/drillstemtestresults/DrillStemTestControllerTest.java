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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

@ContextConfiguration(classes = DrillStemTestController.class)
class DrillStemTestControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private DrillStemTestFormService drillStemTestFormService;

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private DrillStemTestInitialPersistenceService drillStemTestPersistenceService;

  @Autowired
  private ControllerHelperService controllerHelperService;

  private static DrillStemTestInitialForm form;
  private static BindingResult bindingResult;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    form = new DrillStemTestInitialForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void getDrillStemTestPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(drillStemTestFormService.getDrillStemTestInitialForm(submissionDetail)).thenReturn(form);

    mockMvc.perform(
            get(ReverseRouter.route(on(DrillStemTestController.class).getDrillStemTestPage(submissionDetail.getId(), null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/drillstemtest/drillStemTestInitial"));
  }

  @Test
  void saveDrillStemTestPage_whenValidForm_assertRedirection() throws Exception {
    when(drillStemTestFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetail.getId(), null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId()))));

    verify(drillStemTestPersistenceService, times(1))
        .saveInitialDrillStemTest(any(DrillStemTestInitialForm.class), eq(submissionDetail));
  }

  @Test
  void saveDrillStemTestPage_whenValidForm_AndYesDrillStemResult_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(drillStemTestFormService.validate(any(), any(), any())).thenReturn(ReverseRouter.emptyBindingResult());

    var form = new DrillStemTestInitialForm();
    form.setHasDrillStemTestResult(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetail.getId(), null, null)))
                .with(user(USER))
                .with(csrf())
                .flashAttr("form", form))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(DrillStemTestResultController.class)
            .getCreateNewDrillStemTestResultPage(submissionDetail.getId()))));

    verify(drillStemTestPersistenceService, times(1))
        .saveInitialDrillStemTest(any(DrillStemTestInitialForm.class), eq(submissionDetail));
  }

  @Test
  void saveDrillStemTestPage_whenInvalidForm_assertStatusOk() throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(drillStemTestFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetail.getId(), null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(drillStemTestPersistenceService, never()).saveInitialDrillStemTest(any(), eq(submissionDetail));
  }

  @Test
  void getDrillStemTestModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    var form = new DrillStemTestInitialForm();
    when(drillStemTestFormService.getDrillStemTestInitialForm(submissionDetail))
        .thenReturn(form);

    ModelMap modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(DrillStemTestController.class)
                    .getDrillStemTestPage(submissionDetail.getId(), null)))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView()).getModelMap();

    assertEquals(DrillStemTestInitialForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("actionUrl", ReverseRouter.route(on(DrillStemTestController.class)
                .saveDrillStemTestDetails(submissionDetail.getId(), null, null))))
        .containsKeys("breadcrumbsMap");
  }
}