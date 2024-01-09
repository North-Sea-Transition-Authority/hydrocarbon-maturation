package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionSelectionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionService;

@ContextConfiguration(classes = ViewResourceRecordController.class)
class ViewResourceRecordControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SubmissionHistoryQueryService submissionHistoryQueryService;
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
  @MockBean
  private ResponsibleTeamService responsibleTeamService;
  @MockBean
  private CorrectionService correctionService;
  private Resource resource;
  private UUID resourceId;
  private SubmissionDetail submissionDetail;
  private ResourceRecordSummaryView resourceRecordSummaryView;

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resourceId = resource.getId();
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT, resource);
    resourceRecordSummaryView = SubmissionIntegrationTestUtils.emptyResourceRecordSummaryView(UUID.randomUUID(), 1);
    when(viewResourceRecordService.getResourceRecordOrThrow(resourceId)).thenReturn(resourceRecordSummaryView);
    when(submissionDetailService.getSubmissionDetailForIdOrError(resourceRecordSummaryView.submissionDetailId()))
        .thenReturn(submissionDetail);
  }

  @SecurityTest
  void showResourceRecord_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void showResourceRecord_UserCannotViewRecord_assertStatusUnauthorized() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, USER)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showResourceRecord_UserCanViewRecordAndSubmission_assertIsOk() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, USER)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(submissionDetail, resource))
        .thenReturn(true);
    mockModelAndViewCreation(submissionDetail, List.of());
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showSubmissionSummary_assertModelProperties() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, USER)).thenReturn(true);
    submissionDetail.setAcceptedTimestamp(Instant.now());
    var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.PROSPECT_UPDATE, resource);
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionOnResource.getId()))
        .thenReturn(otherSubmissionOnResource);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(otherSubmissionOnResource, resource))
        .thenReturn(true);
    when(correctionService.canStartCorrection(resource, USER)).thenReturn(true);
    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(otherSubmissionOnResource));
    mockModelAndViewCreation(otherSubmissionOnResource, queryResult);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(resourceId, otherSubmissionOnResource.getId(), null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(otherSubmissionOnResource.getSubmission().getResource().getId(), null))),
            entry("resourceType", otherSubmissionOnResource.getSubmission().getResource().getType().getDisplayName()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", otherSubmissionOnResource.getSubmissionType()),
            entry("correctionUrl", ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resource.getId(), null)))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsNotRegulator_assertModelProperties() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, USER)).thenReturn(true);
    submissionDetail.setAcceptedTimestamp(Instant.now());

    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(false);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(false);
    var resource = new Resource();
    resource.setId(resourceId);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(submissionDetail, resource))
        .thenReturn(true);
    when(correctionService.canStartCorrection(resource, USER)).thenReturn(false);
    mockModelAndViewCreation(submissionDetail, List.of());

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("canManageRegulatorManagementInfo", false),
            entry("canViewRegulatorManagementInfo", false)
        ).doesNotContainKeys("regulatorManagedInformationSummaryView", "resourceGroupsSummaryView");

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsRegulator_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, USER)).thenReturn(true);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);
    var regulatorSummary = RegulatorManagedInformationSummaryView.empty(false);
    when(regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(any()))
        .thenReturn(regulatorSummary);
    var resource = new Resource();
    resource.setId(resourceId);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(submissionDetail, resource))
        .thenReturn(true);
    var resourceGroupSummary = new ResourceGroupSummaryView(List.of(), ResourceType.DISCOVERY, "");
    when(resourceGroupSummaryService.getResourceGroupLinkSummaryFromResource(resource))
        .thenReturn(resourceGroupSummary);
    when(correctionService.canStartCorrection(resource, USER)).thenReturn(true);
    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(submissionDetail));
    mockModelAndViewCreation(submissionDetail, queryResult);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("resourceType", submissionDetail.getSubmission().getResource().getType().getDisplayName()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("canManageRegulatorManagementInfo", true),
            entry("regulatorManagedInformationUrl",
                ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId))),
            entry("resourceGroupUrl",
                ReverseRouter.route(on(ResourceGroupController.class)
                    .getResourceGroupPage(resourceId, null))),
            entry("regulatorManagedInformationSummaryView", regulatorSummary),
            entry("resourceGroupsSummaryView", resourceGroupSummary),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("canViewRegulatorManagementInfo", true),
            entry("correctionUrl", ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resource.getId(), null)))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
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
                .showSubmissionSummary(resourceId, otherSubmissionId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void showSubmissionSummary_whenCannotViewSelectedSubmission_assertForbidden() throws Exception{
    var otherSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var otherSubmissionId = UUID.randomUUID();
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionId))
        .thenReturn(otherSubmissionDetail);
    when(viewResourceRecordService.canViewResource(any(), eq(USER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(otherSubmissionDetail, resource))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(resourceId, otherSubmissionId, null)))
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
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());
    when(regulatorTeamService.isRegulatorUser(USER))
        .thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);
    when(submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(any(), any()))
        .thenReturn(new ModelAndView());
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(resourceId, otherSubmissionId, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void viewSubmission_assertRedirect() throws Exception {
    var form = new SubmissionSearchSelectorForm();
    form.setSelectedSubmissionId(UUID.randomUUID().toString());
    mockMvc.perform(
            post(ReverseRouter.route(on(ViewResourceRecordController.class).viewSubmission(resourceId, null)))
                .with(user(USER))
                .with(csrf())
                .flashAttr("form", form))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ViewResourceRecordController.class)
            .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null))));
  }

  void mockModelAndViewCreation(SubmissionDetail submissionDetail, List<SubmissionQuerySearchRecord> querySearchRecords) {
    var modelAndView = new ModelAndView();
    when(submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(eq(submissionDetail), anyString()))
        .thenReturn(modelAndView);
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resourceId)).thenReturn(querySearchRecords);
  }
}