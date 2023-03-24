package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.time.Instant;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.ResourceGroupSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.ResourceGroupSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;

@ContextConfiguration(classes = ViewResourceRecordController.class)
class ViewResourceRecordControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService;
  @MockBean
  private ViewResourceRecordService viewResourceRecordService;
  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;
  @MockBean
  private RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService;
  @MockBean
  private ResourceService resourceService;
  @MockBean
  private ResourceGroupSummaryService resourceGroupSummaryService;

  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final ResourceRecordSummaryView RESOURCE_RECORD_VIEW = SubmissionIntegrationTestUtils.emptyResourceRecordSummaryView(
      UUID.randomUUID(), 1);
  private final SubmissionDetail SUBMISSION_DETAIL = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @BeforeEach
  void setUp() {
    when(viewResourceRecordService.getResourceRecordOrThrow(RESOURCE_ID)).thenReturn(RESOURCE_RECORD_VIEW);
    when(submissionDetailService.getSubmissionDetailForIdOrError(RESOURCE_RECORD_VIEW.submissionDetailId()))
        .thenReturn(SUBMISSION_DETAIL);

    var modelAndView = new ModelAndView();
    doReturn(modelAndView)
        .when(submissionSummaryModelService).createSummaryForSubmissionTypeToModelAndView(eq(SUBMISSION_DETAIL), anyString());
    modelAndView
        .addObject("submissionSelectedUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
            .viewSubmission(RESOURCE_ID, null)))
        .addObject("submissionsOnResource", Map.of());

    doReturn(modelAndView)
        .when(submissionSummaryModelService).createSummaryForSubmissionTypeToModelAndView(eq(SUBMISSION_DETAIL), anyString());
  }

  @SecurityTest
  void showResourceRecord_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void showResourceRecord_UserCannotViewRecord_assertStatusUnauthorized() throws Exception {
    when(viewResourceRecordService.canViewResource(RESOURCE_RECORD_VIEW, USER)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showResourceRecord_UserCanViewRecord_assertStatusUnauthorized() throws Exception {
    when(viewResourceRecordService.canViewResource(RESOURCE_RECORD_VIEW, USER)).thenReturn(true);
    mockModelAndViewCreation(SUBMISSION_DETAIL);
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showSubmissionSummary_assertModelProperties() throws Exception {
    when(viewResourceRecordService.canViewResource(RESOURCE_RECORD_VIEW, USER)).thenReturn(true);
    SUBMISSION_DETAIL.setApprovedTimestamp(Instant.now());
    var otherSubmissionOnResource = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionOnResource.getId()))
        .thenReturn(otherSubmissionOnResource);
    mockModelAndViewCreation(otherSubmissionOnResource);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(RESOURCE_ID, otherSubmissionOnResource.getId(), null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", Map.of()),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(otherSubmissionOnResource.getResourceId().getId(), null))),
            entry("resourceType", otherSubmissionOnResource.getResourceId().getType().getDisplayName()),
            entry("resourceRecord", RESOURCE_RECORD_VIEW),
            entry("submissionType", otherSubmissionOnResource.getSubmissionType())
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen()),
            SearchResourceRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsNotRegulator_assertModelProperties() throws Exception {
    when(viewResourceRecordService.canViewResource(RESOURCE_RECORD_VIEW, USER)).thenReturn(true);
    SUBMISSION_DETAIL.setApprovedTimestamp(Instant.now());
    mockModelAndViewCreation(SUBMISSION_DETAIL);

    when(viewResourceRecordService.isInResponsibleRegulatorTeam(USER))
        .thenReturn(false);
    var resource = new Resource();
    resource.setId(RESOURCE_ID);
    when(resourceService.getResourceById(RESOURCE_ID))
        .thenReturn(Optional.of(resource));
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("resourceRecord", RESOURCE_RECORD_VIEW),
            entry("submissionType", SUBMISSION_DETAIL.getSubmissionType()),
            entry("isRegulatorInResponsibleTeam", false),
            entry("regulatorManagedInformationUrl",
                ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(RESOURCE_ID, null))),
            entry("isRegulator", false)
        ).doesNotContainKeys("regulatorManagedInformationSummaryView", "resourceGroupsSummaryView");

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen()),
            SearchResourceRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsRegulator_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    when(viewResourceRecordService.canViewResource(RESOURCE_RECORD_VIEW, USER)).thenReturn(true);
    when(viewResourceRecordService.isInResponsibleRegulatorTeam(USER))
        .thenReturn(false);
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(true);
    var regulatorSummary = RegulatorManagedInformationSummaryView.empty(false);
    when(regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(any()))
        .thenReturn(regulatorSummary);
    mockModelAndViewCreation(SUBMISSION_DETAIL);
    var resource = new Resource();
    resource.setId(RESOURCE_ID);
    when(resourceService.getResourceById(RESOURCE_ID))
        .thenReturn(Optional.of(resource));
    var resourceGroupSummary = new ResourceGroupSummaryView(List.of(), ResourceType.DISCOVERY, "");
    when(resourceGroupSummaryService.getResourceGroupLinkSummaryFromResource(resource))
        .thenReturn(resourceGroupSummary);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", Map.of()),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(SUBMISSION_DETAIL.getResourceId().getId(), null))),
            entry("resourceType", SUBMISSION_DETAIL.getResourceId().getType().getDisplayName()),
            entry("resourceRecord", RESOURCE_RECORD_VIEW),
            entry("submissionType", SUBMISSION_DETAIL.getSubmissionType()),
            entry("isRegulatorInResponsibleTeam", false),
            entry("regulatorManagedInformationUrl",
                ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(RESOURCE_ID, null))),
            entry("regulatorManagedInformationSummaryView", regulatorSummary),
            entry("resourceGroupsSummaryView", resourceGroupSummary),
            entry("submissionType", SUBMISSION_DETAIL.getSubmissionType()),
            entry("isRegulator", true)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen()),
            SearchResourceRecordsController.PAGE_TITLE)
    );
  }

  @Test
  void showSubmissionSummary_whenCannotViewSubmission_assertForbidden() throws Exception{
    var otherSubmissionDetail = new SubmissionDetail();
    var otherSubmissionId = UUID.randomUUID();
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionId))
        .thenReturn(otherSubmissionDetail);
    when(viewResourceRecordService.canViewResource(any(), eq(USER)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(RESOURCE_ID, otherSubmissionId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void showSubmissionSummary_AsRegulator_whenNoResource_assertStatus() throws Exception{
    var otherSubmissionDetail = new SubmissionDetail();
    var otherSubmissionId = UUID.randomUUID();
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionId))
        .thenReturn(otherSubmissionDetail);
    when(viewResourceRecordService.canViewResource(any(), eq(USER)))
        .thenReturn(true);
    when(resourceService.getResourceById(RESOURCE_ID))
        .thenReturn(Optional.empty());
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(true);
    when(submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(any(), any()))
        .thenReturn(new ModelAndView());
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(RESOURCE_ID, otherSubmissionId, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void viewSubmission_assertRedirect() throws Exception {
    var form = new SubmissionSearchSelectorForm();
    form.setSelectedSubmissionId(UUID.randomUUID().toString());
    mockMvc.perform(
            post(ReverseRouter.route(on(ViewResourceRecordController.class).viewSubmission(RESOURCE_ID, null)))
                .with(user(USER))
                .with(csrf())
                .flashAttr("form", form))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ViewResourceRecordController.class)
            .showSubmissionSummary(RESOURCE_ID, UUID.fromString(form.getSelectedSubmissionId()), null))));
  }

  void mockModelAndViewCreation(SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView();
    doReturn(modelAndView)
        .when(submissionSummaryModelService).createSummaryForSubmissionTypeToModelAndView(eq(submissionDetail), anyString());
    modelAndView
        .addObject("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
            .viewSubmission(submissionDetail.getResourceId().getId(), null)))
        .addObject("submissionNumber", "1")
        .addObject("submissionsOnResource", Map.of());

    doReturn(modelAndView)
        .when(submissionHistorySearchSelectorModelService)
        .addViewSorSubmissionSearchSelectorItemsToModelAndView(modelAndView, RESOURCE_ID);
  }
}