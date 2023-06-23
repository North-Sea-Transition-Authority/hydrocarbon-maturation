package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.RequestUpdateController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.StartUpdateController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdate;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionQuerySearchRecord;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionSearchSelectorForm;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ReviewSubmissionController.class)
class ReviewSubmissionControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();
  private static final String SUBMISSION_UPDATE_REASON = "reason for update";

  @MockBean
  private ReviewSubmissionService reviewSubmissionService;
  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;
  @MockBean
  private SubmissionHistoryQueryService submissionHistoryQueryService;
  @MockBean
  private RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService;
  @MockBean
  private ResourceService resourceService;
  @MockBean
  private SubmissionUpdateService submissionUpdateService;
  @MockBean
  private ResourceGroupSummaryService resourceGroupSummaryService;
  @MockBean
  private ResponsibleTeamService responsibleTeamService;

  private Resource resource;
  private UUID resourceId;
  private SubmissionDetail submissionDetail;
  private SubmissionRecordSummaryView submissionRecordSummaryView;

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resourceId = resource.getId();
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT, resource);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionRecordSummaryView = SubmissionIntegrationTestUtils.emptySubmissionRecordSummaryView(
        UUID.randomUUID(), 1, SubmissionType.CREATE_PROSPECT);
    when(reviewSubmissionService.getSubmissionRecordOrThrow(resourceId)).thenReturn(submissionRecordSummaryView);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionRecordSummaryView.submissionDetailId()))
        .thenReturn(submissionDetail);
  }

  @SecurityTest
  void showReviewPage_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resourceId))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void showReviewPage_UserCannotReview_assertStatusUnauthorized() throws Exception {
    when(reviewSubmissionService.canViewSubmission(submissionRecordSummaryView, USER, submissionDetail)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resourceId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showReviewPage_UserCanReview_assertStatusAuthorized() throws Exception {
    when(reviewSubmissionService.canViewSubmission(submissionRecordSummaryView, USER, submissionDetail)).thenReturn(true);
    mockModelAndViewCreation(submissionDetail, List.of());
    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resourceId)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showSubmissionSummary_whenSubmissionUpdateUnresolved_assertModelProperties() throws Exception {
    var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.PROSPECT_UPDATE, resource);
    otherSubmissionOnResource.setStatus(SubmissionDetailStatus.SUBMITTED);
    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(otherSubmissionOnResource));
    mockModelAndViewCreation(otherSubmissionOnResource, queryResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionOnResource.getId()))
        .thenReturn(otherSubmissionOnResource);

    when(reviewSubmissionService.canReviewSubmission(submissionRecordSummaryView, USER, otherSubmissionOnResource))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(responsibleTeamService.isInResponsibleTeam(USER, submissionRecordSummaryView.responsibleTeamType())).thenReturn(false);
    var latestSubmission = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.of(latestSubmission));
    var submissionUpdate = new SubmissionUpdate();
    submissionUpdate.setResolved(false);
    submissionUpdate.setReason(SUBMISSION_UPDATE_REASON);
    when(reviewSubmissionService.getLatestUnresolvedSubmissionUpdateBySubmission(otherSubmissionOnResource.getSubmission()))
        .thenReturn(Optional.of(submissionUpdate));
    when(reviewSubmissionService.canViewSubmission(submissionRecordSummaryView, USER, latestSubmission))
        .thenReturn(true);
    when(reviewSubmissionService.hasUpdateBeenRequestedOnSubmission(otherSubmissionOnResource.getSubmission()))
        .thenReturn(true);
    when(reviewSubmissionService.canUpdateSubmission(submissionRecordSummaryView, USER, otherSubmissionOnResource))
        .thenReturn(true);
    when(reviewSubmissionService.canViewSelectedSubmissionDetail(otherSubmissionOnResource, resource))
        .thenReturn(true);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class)
                .showSubmissionSummary(resourceId, otherSubmissionOnResource.getId(), null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionRecord", submissionRecordSummaryView),
            entry("canReview", false),
            entry("isInResponsibleTeam", false),
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .viewSubmission(otherSubmissionOnResource.getSubmission().getResource().getId(), null))),
            entry("resourceType", otherSubmissionOnResource.getSubmission().getResource().getType().getDisplayName()),
            entry("submissionType", otherSubmissionOnResource.getSubmissionType()),
            entry("updateSubmissionUrl", ReverseRouter.route(on(StartUpdateController.class)
                .showStartUpdatePage(otherSubmissionOnResource.getId()))),
            entry("updateStarted", false),
            entry("updateRequested", true),
            entry("submissionUpdateReason", SUBMISSION_UPDATE_REASON),
            entry("isIndustrySubmitter", true)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()),
            WorkAreaController.WORK_AREA_TITLE)
    );
  }

  @SecurityTest
  void showSubmissionSummary_whenCannotViewSelectedSubmissionDetail_assertForbidden() throws Exception {
    var otherSubmissionOnResource = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    otherSubmissionOnResource.setStatus(SubmissionDetailStatus.SUBMITTED);
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionOnResource.getId()))
        .thenReturn(otherSubmissionOnResource);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(reviewSubmissionService.canViewSelectedSubmissionDetail(otherSubmissionOnResource, resource))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class)
                .showSubmissionSummary(resourceId, otherSubmissionOnResource.getId(), null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showSubmissionModelAndView_assertModelProperties() throws Exception {
    when(reviewSubmissionService.canViewSubmission(submissionRecordSummaryView, USER, submissionDetail)).thenReturn(true);
    when(reviewSubmissionService.canReviewSubmission(submissionRecordSummaryView, USER, submissionDetail))
        .thenReturn(false);
    when(responsibleTeamService.isInResponsibleTeam(USER, submissionRecordSummaryView.responsibleTeamType())).thenReturn(false);
    when(reviewSubmissionService.canUpdateSubmission(submissionRecordSummaryView, USER, submissionDetail))
        .thenReturn(true);
    var updateSubmission = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(reviewSubmissionService.getUpdateSubmissionDetailBySubmission(
        submissionDetail.getSubmission()))
        .thenReturn(Optional.of(updateSubmission));
    mockModelAndViewCreation(submissionDetail, List.of());
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resourceId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionRecord", submissionRecordSummaryView),
            entry("canReview", false),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isInResponsibleTeam", false),
            entry("updateStarted", true),
            entry("updateSubmissionUrl", ReverseRouter.route(on(TaskListController.class)
                .getTaskList(updateSubmission.getId())))
        ).doesNotContainKey("regulatorManagedInformationSummaryView");
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()),
            WorkAreaController.WORK_AREA_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showSubmissionModelAndView_whenIsRegulator_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    when(reviewSubmissionService.canViewSubmission(submissionRecordSummaryView, USER, submissionDetail)).thenReturn(true);
    when(reviewSubmissionService.canReviewSubmission(submissionRecordSummaryView, USER, submissionDetail))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    var regulatorSummary = RegulatorManagedInformationSummaryView.empty(false);
    when(regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(any()))
        .thenReturn(regulatorSummary);
    var resourceGroupSummary = new ResourceGroupSummaryView(List.of(), ResourceType.PROSPECT, "");
    when(resourceGroupSummaryService.getResourceGroupLinkSummaryFromResource(resource))
        .thenReturn(resourceGroupSummary);
    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(submissionDetail));
    mockModelAndViewCreation(submissionDetail, queryResult);
    when(responsibleTeamService.isInResponsibleTeam(USER, submissionRecordSummaryView.responsibleTeamType())).thenReturn(true);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resourceId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionRecord", submissionRecordSummaryView),
            entry("canReview", true),
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("resourceType", submissionDetail.getSubmission().getResource().getType().getDisplayName()),
            entry("approveUrl", ReverseRouter.route(on(ApproveSubmissionController.class)
                .showApproveConfirmationPage(submissionDetail.getId()))),
            entry("regulatorManagedInformationUrl",
                ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(
                        resourceId, submissionDetail.getId()))),
            entry("requestUpdateUrl", ReverseRouter.route(on(RequestUpdateController.class)
                .showRequestUpdateConfirmationPage(submissionDetail.getId()))),
            entry("canRequestUpdate", false),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("regulatorManagedInformationSummaryView", regulatorSummary),
            entry("resourceGroupsSummaryView", resourceGroupSummary),
            entry("withdrawUrl", ReverseRouter.route(on(WithdrawSubmissionController.class)
                .showWithdrawConfirmationPage(submissionDetail.getId()))),
            entry("isInResponsibleTeam", true),
            entry("updateRequested", false),
            entry("isIndustrySubmitter", false)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()),
            WorkAreaController.WORK_AREA_TITLE)
    );
  }

  @Test
  void showSubmissionModelAndView_whenSubmissionUpdateRequested_assertUpdateModelProperties() throws Exception {
    when(reviewSubmissionService.canViewSubmission(submissionRecordSummaryView, USER, submissionDetail)).thenReturn(true);
    mockModelAndViewCreation(submissionDetail, List.of());
    var submissionUpdate = new SubmissionUpdate();
    submissionUpdate.setReason(SUBMISSION_UPDATE_REASON);
    when(reviewSubmissionService.getLatestUnresolvedSubmissionUpdateBySubmission(submissionDetail.getSubmission()))
        .thenReturn(Optional.of(submissionUpdate));
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resourceId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("updateRequested", true),
            entry("submissionUpdateReason", SUBMISSION_UPDATE_REASON),
            entry("isIndustrySubmitter", false)
        );
  }

  @Test
  void showSubmissionModelAndView_whenIsRegulator_whenNoResource_assertStatus() throws Exception {
    when(reviewSubmissionService.canViewSubmission(submissionRecordSummaryView, USER, submissionDetail)).thenReturn(true);
    when(reviewSubmissionService.canReviewSubmission(submissionRecordSummaryView, USER, submissionDetail))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());
    var regulatorSummary = RegulatorManagedInformationSummaryView.empty(false);
    when(regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(any()))
        .thenReturn(regulatorSummary);
    mockModelAndViewCreation(submissionDetail, List.of());
    when(responsibleTeamService.isInResponsibleTeam(USER, submissionRecordSummaryView.responsibleTeamType())).thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resourceId)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void showSubmissionSummary_whenInvalidResourceId_assertIsNotFound() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());
    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class)
                .showSubmissionSummary(resourceId, UUID.randomUUID(), null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void showSubmissionSummary_whenNoLatestApprovedOrSubmittedSubmission_assertIsNotFound() throws Exception {
    var submissionDetailId = UUID.randomUUID();
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.empty());
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(reviewSubmissionService.canViewSelectedSubmissionDetail(submissionDetail, resource))
        .thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class)
                .showSubmissionSummary(resourceId, submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void showSubmissionSummary_whenCannotViewSubmission_assertForbidden() throws Exception{
    var otherSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT, resource);
    var otherSubmissionId = otherSubmissionDetail.getId();
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionId))
        .thenReturn(otherSubmissionDetail);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    var latestSubmission = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.of(latestSubmission));
    when(reviewSubmissionService.canViewSubmission(submissionRecordSummaryView, USER, latestSubmission))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class)
                .showSubmissionSummary(resourceId, otherSubmissionId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void viewSubmission_assertRedirect() throws Exception {
    var form = new SubmissionSearchSelectorForm();
    form.setSelectedSubmissionId(UUID.randomUUID().toString());
    mockMvc.perform(
            post(ReverseRouter.route(on(ReviewSubmissionController.class).viewSubmission(resourceId, null)))
                .with(user(USER))
                .with(csrf())
                .flashAttr("form", form))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReviewSubmissionController.class)
            .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null))));
  }

  void mockModelAndViewCreation(SubmissionDetail submissionDetail, List<SubmissionQuerySearchRecord> querySearchRecords) {
    var modelAndView = new ModelAndView();
    when(submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(eq(submissionDetail), anyString()))
        .thenReturn(modelAndView);
    when(submissionHistoryQueryService.getApprovedAndSubmittedSubmissionsOnResource(resourceId)).thenReturn(querySearchRecords);
  }
}