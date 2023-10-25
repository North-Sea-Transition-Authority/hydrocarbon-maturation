package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ReservoirListController.class)
class ReservoirListControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private ReservoirListFormService reservoirListFormService;

  @MockBean
  private ReservoirListValidator reservoirListValidator;

  private SubmissionDetail submissionDetail;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionDetailService.getSubmissionDetail(submissionDetailId)).thenReturn(Optional.of(submissionDetail));
    ReservoirListForm form = new ReservoirListForm();
    when(reservoirListFormService.getReservoirForm(submissionDetail)).thenReturn(form);
  }

  @SecurityTest
  void getReservoirListForm_whenDiscovery_assertStatusOk() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)).thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("/remi/submissiondetails/reservoir/reservoirList"));
  }

  @SecurityTest
  void getReservoirListForm_whenField_assertStatusOk() throws Exception {
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    var submissionDetailId = submissionDetail.getId();
    var field = new Field();
    field.setResponsibleOrganisationId(1);
    when(fieldPersistenceService.getFieldByResourceOrThrow(submissionDetail.getSubmission().getResource()))
        .thenReturn(field);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionDetailService.getSubmissionDetail(submissionDetailId)).thenReturn(Optional.of(submissionDetail));
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)).thenReturn(false);
    ReservoirListForm form = new ReservoirListForm();
    when(reservoirListFormService.getReservoirForm(submissionDetail)).thenReturn(form);
    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("/remi/submissiondetails/reservoir/reservoirList"));
  }

  @SecurityTest
  void getReservoirListForm_whenReservoirsExist_assertRedirectToReservoirTaskList() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)).thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetail.getId(), null))));
  }

  @SecurityTest
  void getReservoirListForm_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirListController.class).getReservoirs(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getReservoirListForm_andNotDiscoveryOrField_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getReservoirListForm_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirListController.class)
                .getReservoirs(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveReservoirListForm_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirListController.class).saveReservoirs(
                submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveReservoirListForm_whenInvalidForm_assertOk() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(reservoirListValidator.isValid(any(ReservoirListForm.class),
        any(BindingResult.class),
        eq(submissionDetail),
        eq(ValidationType.FULL))
    ).thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirListController.class)
                .saveReservoirs(submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
  }

  @SecurityTest
  void saveReservoirListForm_whenValidForm_assertOk() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(reservoirListValidator.isValid(
        any(ReservoirListForm.class),
        any(BindingResult.class),
        eq(submissionDetail),
        eq(ValidationType.FULL)
    )).thenReturn(true);
    mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirListController.class)
                .saveReservoirs(submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetail.getId(), null))));

    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetails(any(ReservoirListForm.class), eq(submissionDetail));
  }

  @Test
  void getReservoirListForm_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)).thenReturn(false);
    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirListController.class).getReservoirs(submissionDetail.getId(), null)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", String.format("Add each reservoir that exists on this %s",
                submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase()
            )),
            entry("reservoirCount", 0),
            entry("backLinkUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId())))
        );
    assertEquals(ReservoirListForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME)
    );
  }
}
