package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchResourceRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = RegulatorManagedInformationController.class)
class RegulatorManagedInformationControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private RegulatorManagedInformationFormService regulatorManagedInformationFormService;

  @MockBean
  private SubmissionResourceService submissionResourceService;

  @MockBean
  private ResourceService resourceService;

  @MockBean
  private ResourceSupportingDocumentService resourceSupportingDocumentService;

  private RegulatorManagedInformationForm form;
  private BindingResult bindingResult;
  private UUID resourceId;
  private Resource resource;
  private FileUploadTemplate fileUploadTemplate;

  @BeforeEach
  void setUp() {
    resource = new Resource();
    form = new RegulatorManagedInformationForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceId = UUID.randomUUID();
    resource.setId(resourceId);
    submissionDetail.getSubmission().setResource(resource);
    fileUploadTemplate = new FileUploadTemplate("", "", "", "", "");
  }

  @Test
  void getRegulatorManagedInformationPage_whenNoResource_assertStatus() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void saveRegulatorManagedInformation_whenNoResource_assertStatus() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getRegulatorManagedInformationPage_whenNoSubmitterOrApprovedSubmissionDetail_assertStatus() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.empty());

    mockMvc.perform(
                get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId, null)))
                    .with(user(USER)))
            .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveRegulatorManagedInformation_whenNoSubmitterOrApprovedSubmissionDetail_assertStatus() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  @SuppressWarnings("unchecked")
  void getRegulatorManagedInformationPage_prospect_assertModelProperties() throws Exception {
    var prospectResource = resource;
    prospectResource.setType(ResourceType.PROSPECT);
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(prospectResource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(prospectResource))
        .thenReturn(Optional.of(submissionDetail));
    var resourceName = "Sample name";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(prospectResource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationFormService.getRegulatorManagedInformationForm(prospectResource))
        .thenReturn(form);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(false);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/review/regulatorManagedInformation"))
            .andReturn()
            .getModelAndView())
        .getModelMap();
    assertEquals(RegulatorManagedInformationForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("geographicalAreas", DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class)),
            entry("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .showResourceRecord(resourceId))),
            entry("isResourceBeingRelinquished", false),
            entry("canUpdateResponsibleTeam", false)
        )
        .containsKeys("breadcrumbsMap")
        .doesNotContainKeys("relinquishmentReportTemplate", "relinquishmentReportUploads", "responsibleTeamTypes");

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen(null)),
                SearchResourceRecordsController.PAGE_TITLE),
            entry(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId)),
                resourceName)
    );
  }

  @SecurityTest
  void getRegulatorManagedInformationPage_discovery_assertModelProperties() throws Exception {
    var discoveryResource = resource;
    discoveryResource.setType(ResourceType.DISCOVERY);
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(discoveryResource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(discoveryResource))
        .thenReturn(Optional.of(submissionDetail));
    var resourceName = "Sample name";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(discoveryResource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationFormService.getRegulatorManagedInformationForm(discoveryResource))
        .thenReturn(form);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId, null)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/review/regulatorManagedInformation"))
            .andReturn()
            .getModelAndView())
        .getModelMap();
    assertEquals(RegulatorManagedInformationForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("canUpdateResponsibleTeam", true),
            entry("responsibleTeamTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ResponsibleTeamType.class))
        );
  }

  @SecurityTest
  void getRegulatorManagedInformationPage_whenNotResponsibleTeamUser_assertStatusForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveRegulatorManagedInformation_assertRedirect() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(true);
    when(resourceSupportingDocumentService.buildFileUploadTemplate(resourceId, ResourceSupportingDocumentType.RELINQUISHMENT_REPORT))
        .thenReturn(fileUploadTemplate);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(true), eq(true)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource))
        .thenReturn(new RegulatorManagedInformation());

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(resourceId))));
  }

  @SecurityTest
  void saveRegulatorManagedInformation_isNotExploration_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  @SuppressWarnings("unchecked")
  void getRegulatorManagedInformationPage_whenSubmissionDetailIdProvided_assertModelProperties() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(resourceSupportingDocumentService.buildFileUploadTemplate(resourceId, ResourceSupportingDocumentType.RELINQUISHMENT_REPORT))
        .thenReturn(fileUploadTemplate);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    var resourceName = "The Resource (25/5)";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn(resourceName);
    var mockFileUploadView = new UploadedFileView("", "", "", "", Instant.EPOCH);
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(resource))
        .thenReturn(true);
    var fileUploadList = List.of(mockFileUploadView);
    when(regulatorManagedInformationFormService.getRelinquishmentReportUploads(form))
        .thenReturn(fileUploadList);
    when(regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resource))
        .thenReturn(form);
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(false);


    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView())
        .getModelMap();
    assertEquals(RegulatorManagedInformationForm.class, modelMap.get("form").getClass());
    assertThat(modelMap).contains(
            entry("geographicalAreas", DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class)),
            entry("cancelUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(resourceId))),
        entry("relinquishmentReportTemplate", fileUploadTemplate),
        entry("relinquishmentReportUploads", fileUploadList)
        )
        .containsKeys("breadcrumbsMap");

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resourceId)),
            resourceName)
    );
  }

  @Test
  void saveRegulatorManagedInformation_whenSubmissionDetailIdProvided_assertRedirect() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(false);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(false);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(false), eq(false)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource))
        .thenReturn(new RegulatorManagedInformation());

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReviewSubmissionController.class)
            .showReviewPage(resourceId))));
  }

  @Test
  void saveRegulatorManagedInformation_whenSubmissionDetailIdProvided_updatedResponsibleTeam_assertRedirectToWorkArea()
      throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(false);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(false), eq(true)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource))
        .thenReturn(new RegulatorManagedInformation());

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("responsibleTeamType", ResponsibleTeamType.ONSHORE.name())
                .param("geographicalArea", GeographicalArea.ONSHORE.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class).getWorkArea())));
  }

  @Test
  void saveRegulatorManagedInformation_whenSubmissionDetailIdProvidedAndResponsibleTeamUpdated_assertRedirect() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(false);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(false), eq(true)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    var resourceName = "The Resource (25/5)";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource))
        .thenReturn(new RegulatorManagedInformation());

    var form = new RegulatorManagedInformationForm();
    form.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, SUBMISSION_DETAIL_ID, form, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReviewSubmissionController.class)
            .showReviewPage(resourceId))));
  }



  @Test
  void saveRegulatorManagedInformation_whenWrongSubmissionDetailForBreadcrumbsIdProvided_assertStatus() throws Exception{
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getSubmissionDetail(SUBMISSION_DETAIL_ID))
        .thenReturn(Optional.empty());
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(false);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(false), eq(false)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.of(submissionDetail));

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  void saveRegulatorManagedInformation_whenNoSubmissionDetailIdAndNoLatestApprovedSubmission_assertStatus() throws Exception{
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(false);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(false), eq(false)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .thenReturn(Optional.of(submissionDetail));

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }
}