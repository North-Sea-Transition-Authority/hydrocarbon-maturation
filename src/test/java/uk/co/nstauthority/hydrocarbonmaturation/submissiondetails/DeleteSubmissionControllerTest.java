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

import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = DeleteSubmissionController.class)
class DeleteSubmissionControllerTest extends AbstractSubmissionControllerTest {

  public static final String VIEW_NAME = "remi/submissiondetails/deleteConfirmation";

  public static final String RESOURCE_NAME = "resource name";
  public static final String TYPE_NAME = "type name";

  @MockitoBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @MockitoBean
  private SubmissionResourceService submissionResourceService;

  @MockitoBean
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @BeforeEach
  void setUp() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID)).thenReturn(submissionDetail);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail)).thenReturn(submissionDetail.getSubmissionType().getTypeName());
  }

  @SecurityTest
  void showDeleteConfirmationPage_assertStatusOk() throws Exception {
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    when(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail))
        .thenReturn("test");

    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(
            SUBMISSION_DETAIL_ID, null)))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void showDeleteConfirmationPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(
            SUBMISSION_DETAIL_ID, null)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showDeleteConfirmationPage_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(get(ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(
            SUBMISSION_DETAIL_ID, null)))
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
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn(RESOURCE_NAME);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();

    when(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail))
        .thenReturn("test");

    var modelMap = Objects.requireNonNull(mockMvc.perform(get(
            ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(
                SUBMISSION_DETAIL_ID, null)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("resourceName", RESOURCE_NAME),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("deleteUrl",
                ReverseRouter.route(on(DeleteSubmissionController.class).deleteSubmission(SUBMISSION_DETAIL_ID, null))),
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(SUBMISSION_DETAIL_ID, null))),
            entry("deleteButtonText", "Delete")
        )
        .containsKeys("pageName");
  }

  @Test
  void showDeleteConfirmationPage_whenIsDraftUpdate_assertDeleteButtonText() throws Exception {
    submissionDetail.setVersionNumber(2);
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn(RESOURCE_NAME);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();

    when(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail))
        .thenReturn("test");

    var modelMap = Objects.requireNonNull(mockMvc.perform(get(
            ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(
                SUBMISSION_DETAIL_ID, null)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("deleteButtonText", "Delete draft update"));
  }

  @Test
  void showDeleteConfirmationPage_whenIsCorrection_assertDeleteButtonText() throws Exception {
    submissionDetail.setCorrection(true);
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn(RESOURCE_NAME);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockCreatePostDrillDryWellSubmissionSummaryModelAndView();

    when(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail))
        .thenReturn("test");

    var modelMap = Objects.requireNonNull(mockMvc.perform(get(
            ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(
                SUBMISSION_DETAIL_ID, null)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("deleteButtonText", "Delete draft correction"));
  }

  @Test
  void deleteSubmission_assertRedirection() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setVersionNumber(1);
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail))
        .thenReturn(RESOURCE_NAME);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .thenReturn(TYPE_NAME);

    var flashMap = mockMvc.perform(
        post(ReverseRouter.route(on(DeleteSubmissionController.class).deleteSubmission(SUBMISSION_DETAIL_ID, null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null))))
        .andReturn().getFlashMap();

    verify(submissionDetailPersistenceService, times(1)).deleteSubmission(submissionDetail);

    assertThat((NotificationBanner) flashMap.get("flash"))
        .extracting(
            NotificationBanner::getTitle,
            NotificationBanner::getContent)
        .containsExactly(
            "%s deleted".formatted(TYPE_NAME),
            "%s for %s has been deleted".formatted(TYPE_NAME, RESOURCE_NAME));
  }

  @Test
  void deleteSubmission_whenDraftUpdate_assertRedirection() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setVersionNumber(2);
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail))
        .thenReturn(RESOURCE_NAME);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .thenReturn(TYPE_NAME);

    var flashMap = mockMvc.perform(
        post(ReverseRouter.route(on(DeleteSubmissionController.class)
            .deleteSubmission(SUBMISSION_DETAIL_ID, null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null))))
        .andReturn().getFlashMap();

    verify(submissionDetailPersistenceService, times(1)).deleteSubmission(submissionDetail);

    assertThat((NotificationBanner) flashMap.get("flash"))
        .extracting(
            NotificationBanner::getTitle,
            NotificationBanner::getContent)
        .containsExactly(
            "%s draft update deleted".formatted(TYPE_NAME),
            "%s draft update for %s has been deleted".formatted(TYPE_NAME, RESOURCE_NAME));
  }

  @Test
  void deleteSubmission_whenCorrection_assertRedirection() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setCorrection(true);
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail))
        .thenReturn(RESOURCE_NAME);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .thenReturn(TYPE_NAME);

    var flashMap = mockMvc.perform(
            post(ReverseRouter.route(on(DeleteSubmissionController.class)
                .deleteSubmission(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null))))
        .andReturn().getFlashMap();

    verify(submissionDetailPersistenceService, times(1)).deleteSubmission(submissionDetail);

    assertThat((NotificationBanner) flashMap.get("flash"))
        .extracting(
            NotificationBanner::getTitle,
            NotificationBanner::getContent)
        .containsExactly(
            "%s draft correction deleted".formatted(TYPE_NAME),
            "%s draft correction for %s has been deleted".formatted(TYPE_NAME, RESOURCE_NAME));
  }

  private void mockCreatePostDrillDryWellSubmissionSummaryModelAndView() {
    var dryWellDetailsSummaryView =  DryWellDetailsSummaryView.empty();
    var permeabilityDetailSummaryView = PermeabilityDetailSummaryView.empty();
    var generalDetailSummaryView = GeneralDetailSummaryView.empty(SubmissionType.POST_DRILL_DRY_HOLE, "prospect");
    var reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        SubmissionType.POST_DRILL_DRY_HOLE,
        "formation",
        "sub unit",
        "age",
        "group name",
        "lithology",
        "facies",
        "connectivity",
        ProbabilityDistributionTableView.empty(),
        ProbabilityDistributionTableView.empty());
    var sourceAndSealSummaryView = SourceAndSealSummaryView.empty();
    var opportunityDetailsView = OpportunityDetailsSummaryView.empty();
    var outlineForMaximumCaseSummaryView = OutlineForMaximumCaseSummaryView.empty();
    var trapPropertiesSummaryView = TrapPropertiesSummaryView.empty();
    var resourceDetailSummaryView = ResourceDetailSummaryView.empty("resourceType");
    var modelAndView = new ModelAndView(VIEW_NAME)
        .addObject("generalDetailsSummaryView", generalDetailSummaryView)
        .addObject("dryWellDetailsSummaryView", dryWellDetailsSummaryView)
        .addObject("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView)
        .addObject("permeabilitySummaryView", permeabilityDetailSummaryView)
        .addObject("opportunityDetailsSummaryView", opportunityDetailsView)
        .addObject("outlineForMaximumCaseSummaryView", outlineForMaximumCaseSummaryView)
        .addObject("sourceAndSealSummaryView", sourceAndSealSummaryView)
        .addObject("trapPropertiesSummaryView", trapPropertiesSummaryView)
        .addObject("resourceDetailSummaryView", resourceDetailSummaryView)
        .addObject("accordionId", submissionDetail.getId())
        .addObject("isOffshore", true);

    doReturn(modelAndView).when(submissionSummaryModelService).createSummaryForSubmissionTypeToModelAndView(submissionDetail, VIEW_NAME, USER);
  }
}