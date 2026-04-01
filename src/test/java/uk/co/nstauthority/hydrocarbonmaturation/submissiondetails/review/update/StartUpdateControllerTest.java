package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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

import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryAssociationService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@ContextConfiguration(classes = StartUpdateController.class)
class StartUpdateControllerTest extends AbstractSubmissionControllerTest {

  @MockitoBean
  private SubmissionUpdatePersistenceService submissionUpdatePersistenceService;

  @MockitoBean
  private SubmissionUpdateService submissionUpdateService;

  @MockitoBean
  private SubmissionResourceService submissionResourceService;

  @MockitoBean
  private DiscoveryAssociationService discoveryAssociationService;

  private SubmissionUpdate submissionUpdate;

  @BeforeEach
  void setUp() {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionUpdate = new SubmissionUpdate();
    submissionUpdate.setReason("default");

  }

  @SecurityTest
  void showStartUpdatePage_whenNotStatusNotSubmitted() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartUpdateController.class).showStartUpdatePage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showStartUpdatePage_whenStatusSubmittedAndUserResponsibleOrg() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    when(submissionUpdateService.canStartUpdateSubmissionDetailOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(true);
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn("resource name");
    when(submissionUpdatePersistenceService
        .getLatestUnresolvedSubmissionUpdateBySubmission(submissionDetail.getSubmission()))
        .thenReturn(Optional.of(submissionUpdate));
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartUpdateController.class).showStartUpdatePage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void showStartUpdatePage_whenStatusSubmittedAndUserNotResponsibleOrg() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(StartUpdateController.class).showStartUpdatePage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void showStartUpdatePage_whenCannotStartUpdate_assertForbidden() throws Exception{
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    when(submissionUpdateService.canStartUpdateSubmissionDetailOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartUpdateController.class).showStartUpdatePage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void showStartUpdatePage_whenCannotFindUpdate_assertIsNotFound() throws Exception{
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    when(submissionUpdateService.canStartUpdateSubmissionDetailOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(true);
    when(submissionUpdatePersistenceService.getLatestUnresolvedSubmissionUpdateBySubmission(
        submissionDetail.getSubmission()))
        .thenReturn(Optional.empty());
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartUpdateController.class).showStartUpdatePage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void showStartUpdatePage_assertModelProperties() throws Exception{
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionUpdateService.canStartUpdateSubmissionDetailOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(true);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    var expectedReason = "reason here";
    submissionUpdate.setReason(expectedReason);
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn("resource name");
    when(submissionUpdatePersistenceService.getLatestUnresolvedSubmissionUpdateBySubmission(
        submissionDetail.getSubmission()))
        .thenReturn(Optional.of(submissionUpdate));
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);
    var modelMap  = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(StartUpdateController.class).showStartUpdatePage(SUBMISSION_DETAIL_ID)))
                    .with(user(USER)))
            .andExpect(view().name("remi/review/startUpdateSubmission"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("startUpdateUrl", ReverseRouter.route(on(StartUpdateController.class)
                .startUpdate(SUBMISSION_DETAIL_ID, null))),
            entry("pageTitle", "Update submission"),
            entry("updateRequestReason", expectedReason)
        );
  }

  @Test
  void startUpdate_assertRedirect() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionUpdateService.canStartUpdateSubmissionDetailOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(true);
    var updateSubmissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionUpdatePersistenceService.startUpdate(submissionDetail, USER))
        .thenReturn(updateSubmissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);
    mockMvc.perform(
            post(ReverseRouter.route(on(StartUpdateController.class).startUpdate(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(updateSubmissionDetail.getId(), null))));

    verify(submissionUpdatePersistenceService, times(1))
        .startUpdate(submissionDetail, USER);
  }

  @Test
  void startUpdate_whenCannotStartUpdate_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionUpdateService.canStartUpdateSubmissionDetailOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartUpdateController.class).startUpdate(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verifyNoInteractions(submissionUpdatePersistenceService);
  }

  @Test
  void startUpdate_whenBlockedByStatus300PlusField_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionUpdateService.canStartUpdateSubmissionDetailOnSubmission(submissionDetail.getSubmission()))
        .thenReturn(true);
    when(discoveryAssociationService.associatedWithStatus300PlusField(submissionDetail.getSubmission()))
        .thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartUpdateController.class).startUpdate(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verifyNoInteractions(submissionUpdatePersistenceService);
  }
}