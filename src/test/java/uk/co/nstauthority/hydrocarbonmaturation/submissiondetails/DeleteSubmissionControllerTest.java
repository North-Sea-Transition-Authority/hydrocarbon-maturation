package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.doReturn;
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

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = DeleteSubmissionController.class)
class DeleteSubmissionControllerTest extends AbstractSubmissionControllerTest {

  public static final String VIEW_NAME = "remi/submissiondetails/deleteConfirmation";

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @MockBean
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @BeforeEach
  void setUp() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID)).thenReturn(submissionDetail);
    when(submissionDetailService.getSubmissionTypeName(submissionDetail)).thenReturn(submissionDetail.getSubmissionType().getTypeName());
  }

  @SecurityTest
  void showDeleteConfirmationPage_assertStatusOk() throws Exception {
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(SUBMISSION_DETAIL_ID)))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void showDeleteConfirmationPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(SUBMISSION_DETAIL_ID)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showDeleteConfirmationPage_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(SUBMISSION_DETAIL_ID)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void deleteSubmission_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionController.class)
            .deleteSubmission(SUBMISSION_DETAIL_ID, null)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void showDeleteConfirmationPage_assertModelProperties() throws Exception {
    var resourceName = "ResourceName";
    when(submissionDetailService.getResourceNameOrDefault(submissionDetail)).thenReturn(resourceName);
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();

    var modelMap = mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(SUBMISSION_DETAIL_ID)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView().getModelMap();

    assertThat(modelMap)
        .contains(
            entry("resourceName", resourceName),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("deleteUrl",
                ReverseRouter.route(on(DeleteSubmissionController.class).deleteSubmission(SUBMISSION_DETAIL_ID, null))),
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(SUBMISSION_DETAIL_ID)))
        )
        .containsKeys("pageName");
  }

  @Test
  void saveOpportunityDetailsPage_assertRedirection() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(DeleteSubmissionController.class).deleteSubmission(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class).getWorkArea())));

    verify(submissionDetailPersistenceService, times(1)).deleteSubmission(submissionDetail);
  }

  private void mockCreatePostDrillDryWellSubmissionSummaryModelAndView() {
    var dryWellDetailsSummaryView =  DryWellDetailsSummaryView.empty();
    var permeabilityDetailSummaryView = PermeabilityDetailSummaryView.empty();
    var generalDetailSummaryView = GeneralDetailSummaryView.empty(SubmissionType.POST_DRILL_DRY_HOLE, "prospect");
    var reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        SubmissionType.POST_DRILL_DRY_HOLE,
        "formation",
        "age",
        "lithology",
        "facies",
        "connectivity",
        new ArrayList<>(),
        new ArrayList<>());
    var modelAndView = new ModelAndView(VIEW_NAME)
        .addObject("generalDetailsSummaryView", generalDetailSummaryView)
        .addObject("dryWellDetailsSummaryView", dryWellDetailsSummaryView)
        .addObject("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView)
        .addObject("permeabilitySummaryView", permeabilityDetailSummaryView);
    doReturn(modelAndView).when(submissionSummaryModelService).createSummaryForSubmissionTypeToModelAndView(submissionDetail, VIEW_NAME);
  }
}