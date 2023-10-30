package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.ReservoirAdditionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirAdditionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirAdditionsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ReservoirTaskListController.class)
class ReservoirTaskListControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private ReservoirTaskListService reservoirTaskListService;

  @MockBean
  private ReservoirAdditionsFormService reservoirAdditionsFormService;

  @MockBean
  private ReservoirAdditionsFormValidator reservoirAdditionsFormValidator;

  @SecurityTest
  void getReservoirTaskList_forDiscovery() throws Exception {
    var reservoirAdditionForm = new ReservoirAdditionForm(ReservoirAdditionStatus.NO);

    when(reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail))
        .thenReturn(reservoirAdditionForm);

    setUpWhenUserIsResponsibleTeam_passSecurity();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var taskListItems = List.of(
        new TaskListItem("item1", "actionUrl1"),
        new TaskListItem("item2", "actionUrl2")
    );

    var taskLists = List.of(
        new ReservoirTaskListItem("reservoir 1", "deleteUrl1", taskListItems),
        new ReservoirTaskListItem("reservoir 2", "deleteUrl1", taskListItems)
    );

    when(reservoirTaskListService.getAllReservoirTaskLists(submissionDetail)).thenReturn(taskLists);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ReservoirTaskListController.class)
                        .getReservoirTaskList(submissionDetail.getId(), null)))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/submissiondetails/tasklist/reservoirTaskList"))
                .andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("reservoirTaskListItems", taskLists),
            entry("pageTitle", ReservoirTaskListController.PAGE_TITLE),
            entry("form", reservoirAdditionForm),
            entry("actionUrl", ReverseRouter.route(on(ReservoirTaskListController.class)
                .saveReservoirAdditions(submissionDetail.getId(), reservoirAdditionForm, null))),
            entry("reservoirAdditionStatusOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirAdditionStatus.class))
        );

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME)
    );
  }

  @SecurityTest
  void getTaskList_whenNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirTaskListController.class)
                .getReservoirTaskList(submissionDetail.getId(), null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getTaskList_whenProspect_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleTeam_passSecurity();

    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirTaskListController.class)
                .getReservoirTaskList(submissionDetail.getId(), null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void saveReservoirAdditions_noMore_invalidForm() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    setUpWhenUserIsResponsibleTeam_passSecurity();

    var form = new ReservoirAdditionForm(ReservoirAdditionStatus.NO);

    var testError = new ObjectError("testObject", "testMessage");
    doAnswer(validateCall -> {
      BindingResult bindingResult = validateCall.getArgument(1);
      bindingResult.addError(testError);
      return false;
    }).when(reservoirAdditionsFormValidator).isValid(
        any(ReservoirAdditionForm.class),
        any(BindingResult.class),
        eq(submissionDetail),
        eq(ValidationType.FULL));

    var modelAndView = mockMvc.perform (
        post(ReverseRouter.route(on(ReservoirTaskListController.class)
            .saveReservoirAdditions(submissionDetail.getId(), null, null)))
            .with(user(USER))
            .with(csrf())
            .flashAttr("form", form)
        )
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/tasklist/reservoirTaskList"))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView.getModelMap())
        .containsEntry("singleErrorMessage", testError.getDefaultMessage());
  }

  @Test
  void saveReservoirAdditions_noMore_validForm() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    setUpWhenUserIsResponsibleTeam_passSecurity();

    var form = new ReservoirAdditionForm(ReservoirAdditionStatus.NO);

    when(reservoirAdditionsFormValidator.isValid(
        any(ReservoirAdditionForm.class),
        any(BindingResult.class),
        eq(submissionDetail),
        eq(ValidationType.FULL)))
        .thenReturn(true);

    mockMvc.perform (
            post(ReverseRouter.route(on(ReservoirTaskListController.class)
                .saveReservoirAdditions(submissionDetail.getId(), null, null)))
                .with(user(USER))
                .with(csrf())
                .flashAttr("form", form))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId()))));

    verify(reservoirAdditionsFormService, times(1))
        .updateReservoirAdditionStatus(submissionDetail, ReservoirAdditionStatus.NO);
  }

  @Test
  void saveReservoirAdditions_yesLater_validForm() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    setUpWhenUserIsResponsibleTeam_passSecurity();

    var form = new ReservoirAdditionForm(ReservoirAdditionStatus.YES_LATER);

    when(reservoirAdditionsFormValidator.isValid(
        any(ReservoirAdditionForm.class),
        any(BindingResult.class),
        eq(submissionDetail),
        eq(ValidationType.PARTIAL)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirTaskListController.class)
                .saveReservoirAdditions(submissionDetail.getId(), null, null)))
                .with(user(USER))
                .with(csrf())
                .flashAttr("form", form))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId()))));

    verify(reservoirAdditionsFormService, times(1))
        .updateReservoirAdditionStatus(submissionDetail, ReservoirAdditionStatus.YES_LATER);
  }

  @Test
  void saveReservoirAdditions_yesNow_validForm() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    setUpWhenUserIsResponsibleTeam_passSecurity();

    var newReservoirSubmissionDetail = new ReservoirSubmissionDetail();
    newReservoirSubmissionDetail.setId(UUID.randomUUID());
    when(reservoirSubmissionDetailPersistenceService.createReservoirAndReservoirSubmissionDetail(submissionDetail))
        .thenReturn(newReservoirSubmissionDetail);

    var form = new ReservoirAdditionForm(ReservoirAdditionStatus.YES_NOW);

    mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirTaskListController.class)
                .saveReservoirAdditions(submissionDetail.getId(), null, null)))
                .with(user(USER))
                .with(csrf())
                .flashAttr("form", form))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(GeneralReservoirInformationController.class)
            .getGeneralReservoirInformationPage(submissionDetail.getId(), newReservoirSubmissionDetail.getId()))));

    verify(reservoirAdditionsFormService, times(1))
        .updateReservoirAdditionStatus(submissionDetail, ReservoirAdditionStatus.YES_NOW);
  }

  @Test
  void saveReservoirAdditions_noSelection_validForm() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    setUpWhenUserIsResponsibleTeam_passSecurity();

    var newReservoirSubmissionDetail = new ReservoirSubmissionDetail();
    newReservoirSubmissionDetail.setId(UUID.randomUUID());
    when(reservoirSubmissionDetailPersistenceService.createReservoirAndReservoirSubmissionDetail(submissionDetail))
        .thenReturn(newReservoirSubmissionDetail);

    var form = new ReservoirAdditionForm(null);

    var testError = new ObjectError("testObject", "testMessage");
    doAnswer(validateCall -> {
      BindingResult bindingResult = validateCall.getArgument(1);
      bindingResult.addError(testError);
      return false;
    }).when(reservoirAdditionsFormValidator).validateFull(
        any(ReservoirAdditionForm.class),
        any(BindingResult.class),
        eq(submissionDetail));

    var modelAndView = mockMvc.perform (
            post(ReverseRouter.route(on(ReservoirTaskListController.class)
                .saveReservoirAdditions(submissionDetail.getId(), null, null)))
                .with(user(USER))
                .with(csrf())
                .flashAttr("form", form)
        )
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/tasklist/reservoirTaskList"))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView.getModelMap())
        .containsEntry("singleErrorMessage", testError.getDefaultMessage());
  }
}
