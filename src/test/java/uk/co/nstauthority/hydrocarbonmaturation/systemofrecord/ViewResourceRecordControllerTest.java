package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.ViewFieldStatusHistoryController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectListController;
import uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation.FieldRegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation.FieldRegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.FieldMapView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.FieldMapViewService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionSelectionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoverySummaryController;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

@ContextConfiguration(classes = ViewResourceRecordController.class)
class ViewResourceRecordControllerTest extends AbstractControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

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
  private FieldRegulatorManagedInformationSummaryService fieldRegulatorManagedInformationSummaryService;
  @MockBean
  private CorrectionService correctionService;
  @MockBean
  private FieldPersistenceService fieldPersistenceService;
  @MockBean
  private FieldMapViewService fieldMapViewService;

  private Resource resource;
  private UUID resourceId;
  private SubmissionDetail submissionDetail;
  private ResourceRecordSummaryView resourceRecordSummaryView;

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resource.setArchived(false);
    resourceId = resource.getId();
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT, resource);
    resourceRecordSummaryView = new ResourceRecordSummaryView(submissionDetail.getId(), "resource", "coi", "RT9",
        1, "org name", List.of(), List.of(),
        null, "parent", "field status");

    when(viewResourceRecordService.getResourceRecordOrThrow(resourceId)).thenReturn(resourceRecordSummaryView);
    when(submissionDetailService.getSubmissionDetail(resourceRecordSummaryView.submissionDetailId()))
        .thenReturn(Optional.of(submissionDetail));
  }

  @SecurityTest
  void showResourceRecord_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void showResourceRecord_UserCannotViewRecord_assertStatusUnauthorized() throws Exception {
    when(resourceService.getResourceById(resourceId))
            .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showResourceRecord_UserCanViewRecordAndSubmission_assertIsOk() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(submissionDetail, resource))
        .thenReturn(true);
    mockModelAndViewCreation(submissionDetail, List.of());
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showSubmissionSummary_prospectSubmission_assertModelProperties() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    submissionDetail.setAcceptedTimestamp(Instant.now());
    var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.PROSPECT_UPDATE,
        resource);
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
                .showSubmissionSummary(resourceId, otherSubmissionOnResource.getId(), null, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(otherSubmissionOnResource.getSubmission().getResource().getId(), null))),
            entry("isFieldResource", false),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", otherSubmissionOnResource.getSubmissionType()),
            entry("correctionUrl",
                ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resource.getId(), null)))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showSubmissionSummary_discoverySubmission_notArchived_assertModelProperties() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    submissionDetail.setAcceptedTimestamp(Instant.now());
    var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY,
        resource);
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
                .showSubmissionSummary(resourceId, otherSubmissionOnResource.getId(), null, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(otherSubmissionOnResource.getSubmission().getResource().getId(), null))),
            entry("isFieldResource", false),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", otherSubmissionOnResource.getSubmissionType()),
            entry("correctionUrl",
                ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resource.getId(), null)))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showSubmissionSummary_fieldSubmission_assertModelProperties() throws Exception {
    resource.setType(ResourceType.FIELD);

    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER))
        .thenReturn(true);

    submissionDetail.setAcceptedTimestamp(Instant.now());
    var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR,
        resource);
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionOnResource.getId()))
        .thenReturn(otherSubmissionOnResource);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(otherSubmissionOnResource, resource))
        .thenReturn(true);
    when(correctionService.canStartCorrection(resource, USER))
        .thenReturn(true);

    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(otherSubmissionOnResource));
    mockModelAndViewCreation(otherSubmissionOnResource, queryResult);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(resourceId, otherSubmissionOnResource.getId(), null, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(otherSubmissionOnResource.getSubmission().getResource().getId(), null))),
            entry("isFieldResource", true),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", otherSubmissionOnResource.getSubmissionType())
        ).doesNotContainKey("correctionUrl");

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null, null)),
            SearchFieldRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsNotRegulator_assertModelProperties() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    submissionDetail.setAcceptedTimestamp(Instant.now());

    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(false);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(false);
    resource.setId(resourceId);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(submissionDetail, resource))
        .thenReturn(true);
    when(correctionService.canStartCorrection(resource, USER)).thenReturn(false);
    mockModelAndViewCreation(submissionDetail, List.of());

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isFieldResource",
                ResourceType.FIELD.equals(submissionDetail.getSubmission().getResource().getType())),
            entry("canManageRegulatorManagementInfo", false),
            entry("canViewRegulatorManagementInfo", false),
            entry("archived", false)
        ).doesNotContainKeys("regulatorManagedInformationSummaryView", "resourceGroupsSummaryView");

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @SecurityTest
  void showResourceRecordModelAndView_whenIsNotRegulator_assertStatusUnauthorized() throws Exception {
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);

    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
            .thenReturn(false);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
            .thenReturn(false);

    resource.setId(resourceId);
    resource.setArchived(true);
    when(resourceService.getResourceById(resourceId))
            .thenReturn(Optional.of(resource));

    mockMvc.perform(
                    get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                            .with(user(USER)))
            .andExpect(status().isForbidden());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsRegulator_fieldSubmission_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    resource.setType(ResourceType.FIELD);

    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);
    resource.setId(resourceId);
    resource.setType(ResourceType.FIELD);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(submissionDetail, resource))
        .thenReturn(true);
    var regulatorSummary = new SummaryCard("", SummaryCardType.EMPTY_SUMMARY, List.of());
    when(fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource))
        .thenReturn(regulatorSummary);
    var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR,
        resource);
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionOnResource.getId()))
        .thenReturn(otherSubmissionOnResource);
    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(otherSubmissionOnResource));
    mockModelAndViewCreation(submissionDetail, queryResult);

    var field = FieldTestUtils.createField("Alex", OnshoreOffshore.ONSHORE);
    field.setFieldIdentifier(8000);
    field.setResource(resource);
    when(fieldPersistenceService.getFieldOrThrow(resource))
        .thenReturn(field);
    var fieldMapView = new FieldMapView("", "");
    when(fieldMapViewService.getFieldGeoJsonUrl(field))
        .thenReturn(Optional.of(fieldMapView));


    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isFieldResource", true),
            entry("canManageRegulatorManagementInfo", true),
            entry("regulatorManagedInformationUrl",
                ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId, null))),
            entry("fieldRegulatorManagedInformationSummaryCard", regulatorSummary),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("canViewRegulatorManagementInfo", true),
            entry("fieldStatusUrl", ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                .getViewFieldStatusHistory(field.getFieldIdentifier(), null))),
            entry("manageResourceInformationText", "Manage field information"),
            entry("projectTrackerUrl",
                ReverseRouter.route(on(ProjectListController.class)
                    .getProjectList(field.getResource().getId(), null))),
            entry("fieldMapView", fieldMapView),
            entry("archiveUrl", ReverseRouter.route(on(ResourceArchiveController.class).archiveResourceConfirmation(resourceId, null))),
            entry("archived", false)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null, null)),
            SearchFieldRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsRegulator_archivedFieldSubmission_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    resource.setArchived(true);
    resource.setType(ResourceType.FIELD);

    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);
    resource.setId(resourceId);
    resource.setType(ResourceType.FIELD);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(submissionDetail, resource))
        .thenReturn(true);
    var regulatorSummary = new SummaryCard("", SummaryCardType.EMPTY_SUMMARY, List.of());
    when(fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource))
        .thenReturn(regulatorSummary);
    var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR,
        resource);
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionOnResource.getId()))
        .thenReturn(otherSubmissionOnResource);
    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(otherSubmissionOnResource));
    mockModelAndViewCreation(submissionDetail, queryResult);

    var field = FieldTestUtils.createField("Alex", OnshoreOffshore.ONSHORE);
    field.setFieldIdentifier(8000);
    field.setResource(resource);
    when(fieldPersistenceService.getFieldOrThrow(resource))
        .thenReturn(field);
    var fieldMapView = new FieldMapView("", "");
    when(fieldMapViewService.getFieldGeoJsonUrl(field))
        .thenReturn(Optional.of(fieldMapView));


    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isFieldResource", true),
            entry("canManageRegulatorManagementInfo", true),
            entry("regulatorManagedInformationUrl",
                ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId, null))),
            entry("fieldRegulatorManagedInformationSummaryCard", regulatorSummary),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("canViewRegulatorManagementInfo", true),
            entry("fieldStatusUrl", ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                .getViewFieldStatusHistory(field.getFieldIdentifier(), null))),
            entry("manageResourceInformationText", "Manage field information"),
            entry("projectTrackerUrl",
                ReverseRouter.route(on(ProjectListController.class)
                    .getProjectList(field.getResource().getId(), null))),
            entry("fieldMapView", fieldMapView),
            entry("unarchiveUrl", ReverseRouter.route(on(ResourceArchiveController.class).unarchiveResourceConfirmation(resourceId, null))),
            entry("archived", true)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null, null)),
            SearchFieldRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsRegulator_fieldResourceWithNoSubmissions_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    when(submissionDetailService.getSubmissionDetail(resourceRecordSummaryView.submissionDetailId()))
        .thenReturn(Optional.empty());
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER))
        .thenReturn(true);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);

    resource.setId(resourceId);
    resource.setType(ResourceType.FIELD);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));

    var field = FieldTestUtils.createField("Alex", OnshoreOffshore.ONSHORE);
    field.setFieldIdentifier(8000);
    field.setResource(resource);
    when(fieldPersistenceService.getFieldOrThrow(resource))
        .thenReturn(field);
    var fieldMapView = new FieldMapView("", "");
    when(fieldMapViewService.getFieldGeoJsonUrl(field))
        .thenReturn(Optional.of(fieldMapView));

    var regulatorSummary = new SummaryCard("", SummaryCardType.EMPTY_SUMMARY, SummaryDataView.newWithKeyValue("", ""));
    when(fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource))
        .thenReturn(regulatorSummary);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("resourceRecord", resourceRecordSummaryView),
            entry("isFieldResource", true),
            entry("canManageRegulatorManagementInfo", true),
            entry("regulatorManagedInformationUrl",
                ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId, null))),
            entry("fieldRegulatorManagedInformationSummaryCard", regulatorSummary),
            entry("canViewRegulatorManagementInfo", true),
            entry("manageResourceInformationText", "Manage field information"),
            entry("fieldMapView", fieldMapView)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null, null)),
            SearchFieldRecordsController.PAGE_TITLE)
    );
  }

  @Test
  void showResourceRecordModelAndView_assertModelProperties_noFieldMapView() throws Exception {
    when(submissionDetailService.getSubmissionDetail(resourceRecordSummaryView.submissionDetailId()))
        .thenReturn(Optional.empty());
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER))
        .thenReturn(true);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);

    resource.setId(resourceId);
    resource.setType(ResourceType.FIELD);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));

    var field = FieldTestUtils.createField("Alex", OnshoreOffshore.ONSHORE);
    field.setFieldIdentifier(8000);
    field.setResource(resource);
    when(fieldPersistenceService.getFieldOrThrow(resource))
        .thenReturn(field);
    when(fieldMapViewService.getFieldGeoJsonUrl(field))
        .thenReturn(Optional.empty());

    var regulatorSummary = new SummaryCard("", SummaryCardType.EMPTY_SUMMARY, SummaryDataView.newWithKeyValue("", ""));
    when(fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource))
        .thenReturn(regulatorSummary);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap).doesNotContainKey("fieldMapView");
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsRegulator_prospectSubmission_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    resource.setArchived(false);

    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);
    var regulatorSummary = RegulatorManagedInformationSummaryView.empty(false);
    when(regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(any()))
        .thenReturn(regulatorSummary);
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
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isFieldResource", false),
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
            entry("correctionUrl",
                ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resource.getId(), null))),
            entry("manageResourceInformationText", "Manage resource information"),
            entry("archiveUrl", ReverseRouter.route(on(ResourceArchiveController.class).archiveResourceConfirmation(resourceId, null))),
            entry("archived", false)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsRegulator_archivedProspectSubmission_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    resource.setArchived(true);

    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);
    var regulatorSummary = RegulatorManagedInformationSummaryView.empty(false);
    when(regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(any()))
        .thenReturn(regulatorSummary);
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
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isFieldResource", false),
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
            entry("correctionUrl",
                ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resource.getId(), null))),
            entry("manageResourceInformationText", "Manage resource information"),
            entry("unarchiveUrl", ReverseRouter.route(on(ResourceArchiveController.class).unarchiveResourceConfirmation(resourceId, null))),
            entry("archived", true)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsRegulator_discoverySubmission_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    resource.setArchived(false);

    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);
    var regulatorSummary = RegulatorManagedInformationSummaryView.empty(false);
    when(regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(any()))
        .thenReturn(regulatorSummary);
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
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isFieldResource", false),
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
            entry("correctionUrl",
                ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resource.getId(), null))),
            entry("manageResourceInformationText", "Manage resource information"),
            entry("archiveUrl", ReverseRouter.route(on(ResourceArchiveController.class).archiveResourceConfirmation(resourceId, null))),
            entry("archived", false)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsRegulator_archivedDiscoverySubmission_assertModelPropertiesAndVerifyAddRegulatorAttributes() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    resource.setArchived(true);
    resource.setType(ResourceType.DISCOVERY);

    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    when(viewResourceRecordService.isRegulatorOrAreaTeamUser(USER))
        .thenReturn(true);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);
    var regulatorSummary = RegulatorManagedInformationSummaryView.empty(false);
    when(regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(any()))
        .thenReturn(regulatorSummary);
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
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isFieldResource", false),
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
            entry("correctionUrl",
                ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resource.getId(), null))),
            entry("manageResourceInformationText", "Manage resource information"),
            entry("unarchiveUrl", ReverseRouter.route(on(ResourceArchiveController.class).unarchiveResourceConfirmation(resourceId, null))),
            entry("archived", true)
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @Test
  void showSubmissionSummary_whenCannotViewSubmission_assertForbidden() throws Exception {
    var otherSubmissionDetail = new SubmissionDetail();
    var otherSubmissionId = UUID.randomUUID();
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionId))
        .thenReturn(otherSubmissionDetail);
    when(resourceService.getResourceById(resourceId))
            .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewResource(any(), any(), eq(USER)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(resourceId, otherSubmissionId, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_whenIsReservesAndResourcesAdvisor_discovery_assertModelProperties() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, resource, USER)).thenReturn(true);
    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR,
        Role.RESERVES_AND_RESOURCES_ADVISOR)).thenReturn(true);

    resource.setId(resourceId);
    resource.setType(ResourceType.DISCOVERY);
    resource.setArchived(false);
    when(resourceService.getResourceById(resourceId)).thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(submissionDetail, resource)).thenReturn(true);

    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(submissionDetail));
    mockModelAndViewCreation(submissionDetail, queryResult);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    var resourceTypeDisplayName = resource.getType().getDisplayName();

    assertThat(modelMap)
        .contains(
            entry("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult)),
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("resourceTypeDisplayName", resourceTypeDisplayName),
            entry("resourceTypeDisplayNameLower", resourceTypeDisplayName.toLowerCase()),
            entry("resourceRecord", resourceRecordSummaryView),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isFieldResource", false),
            entry("canManageRegulatorManagementInfo", false),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("canViewRegulatorManagementInfo", false),
            entry("undevelopedDiscoveryUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
                .getUndevelopedDiscoverySummary(resourceId, null)))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)),
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE)
    );
  }

  @Test
  void showSubmissionSummary_whenCannotViewSelectedSubmission_assertForbidden() throws Exception {
    var otherSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var otherSubmissionId = UUID.randomUUID();
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionId))
        .thenReturn(otherSubmissionDetail);
    when(viewResourceRecordService.canViewResource(any(), any(), eq(USER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(viewResourceRecordService.canViewSubmissionDetail(otherSubmissionDetail, resource))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(resourceId, otherSubmissionId, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void showSubmissionSummary_AsRegulator_whenNoResource_assertStatus() throws Exception {
    var otherSubmissionDetail = new SubmissionDetail();
    var otherSubmissionId = UUID.randomUUID();
    when(submissionDetailService.getSubmissionDetailForIdOrError(otherSubmissionId))
        .thenReturn(otherSubmissionDetail);
    when(viewResourceRecordService.canViewResource(any(), any(), eq(USER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, resourceRecordSummaryView.responsibleTeam()))
        .thenReturn(true);
    when(submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(any(), any(), any()))
        .thenReturn(new ModelAndView());
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class)
                .showSubmissionSummary(resourceId, otherSubmissionId, null, null)))
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
            .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null, null))));
  }

  void mockModelAndViewCreation(SubmissionDetail submissionDetail,
                                List<SubmissionQuerySearchRecord> querySearchRecords) {
    var modelAndView = new ModelAndView();
    when(submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(eq(submissionDetail), anyString(),
        any()))
        .thenReturn(modelAndView);
    when(submissionHistoryQueryService.getAcceptedSubmissionsOnResourceWithLinkedResources(resourceId)).thenReturn(
        querySearchRecords);
  }
}