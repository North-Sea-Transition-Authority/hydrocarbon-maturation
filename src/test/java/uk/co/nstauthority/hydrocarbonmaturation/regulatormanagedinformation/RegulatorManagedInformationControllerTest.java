package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.fivium.fileuploadlibrary.fds.FileUploadComponentAttributes;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchProspectAndDiscoveryRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
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

  @MockBean
  private ResponsibleTeamService responsibleTeamService;

  @Autowired
  private CustomerConfigurationProperties customerConfigurationProperties;

  private RegulatorManagedInformationForm form;
  private BindingResult bindingResult;
  private UUID resourceId;
  private Resource resource;
  private final List<UploadedFileForm> existingFiles = Collections.emptyList();
  private final FileUploadComponentAttributes fileUploadComponentAttributes = new FileUploadComponentAttributes(
      "path",
      "upload_url",
      "download_url",
      "delete_url",
      "max_allowed_size",
      "allowed_extensions",
      "dropzone_link_text",
      "dropzone_text",
      "dropzone_link_screen_reader_text",
      "multi_file_error_message",
      false,
      existingFiles,
      true,
      false
  );

  @BeforeEach
  void setUp() {
    resource = new Resource();
    form = new RegulatorManagedInformationForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceId = UUID.randomUUID();
    resource.setId(resourceId);
    submissionDetail.getSubmission().setResource(resource);
  }

  @Test
  void getRegulatorManagedInformationPage_whenNoResource_assertStatus() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
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
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
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
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    mockMvc.perform(
                get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId, null)))
                    .with(user(USER)))
            .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveRegulatorManagedInformation_whenNoSubmitterOrApprovedSubmissionDetail_assertStatus() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getRegulatorManagedInformationPage_prospect_assertModelProperties() throws Exception {
    var prospectResource = resource;
    prospectResource.setType(ResourceType.PROSPECT);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(prospectResource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(prospectResource))
        .thenReturn(Optional.of(submissionDetail));
    var resourceName = "Sample name";
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(prospectResource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationFormService.getRegulatorManagedInformationForm(prospectResource))
        .thenReturn(form);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(false);
    when(internalTeamService.isAreaTeamUser(user)).thenReturn(false);

    var preselectedWell = Map.of("well1", "well1");
    when(regulatorManagedInformationFormService.getPreSelectedWell(form.getWell())).thenReturn(preselectedWell);
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
            entry("pageName", customerConfigurationProperties.mnemonic() + " managed information"),
            entry("geographicalAreas", DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class)),
            entry("cancelUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(resource.getId(), null))),
            entry("isResourceBeingRelinquished", false),
            entry("canUpdateResponsibleTeam", false),
            entry("wellEndpoint", SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null))),
            entry("well", preselectedWell)
        )
        .containsKeys("breadcrumbsMap")
        .doesNotContainKeys("relinquishmentReportTemplate", "relinquishmentReportUploads", "responsibleTeamTypes");
  }

  @SecurityTest
  void getRegulatorManagedInformationPage_discovery_assertModelProperties() throws Exception {
    var discoveryResource = resource;
    discoveryResource.setType(ResourceType.DISCOVERY);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(discoveryResource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(discoveryResource))
        .thenReturn(Optional.of(submissionDetail));
    var resourceName = "Sample name";
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(discoveryResource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationFormService.getRegulatorManagedInformationForm(discoveryResource))
        .thenReturn(form);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(internalTeamService.isAreaTeamUser(user)).thenReturn(false);
    var preselectedWell = Map.of("well1", "well1");
    when(regulatorManagedInformationFormService.getPreSelectedWell(form.getWell())).thenReturn(preselectedWell);
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
            entry("pageName", customerConfigurationProperties.mnemonic() + " managed information"),
            entry("canUpdateResponsibleTeam", true),
            entry("responsibleTeamTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ResponsibleTeamType.class)),
            entry("wellEndpoint", SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null))),
            entry("well", preselectedWell)
        ).containsKey("breadcrumbsMap");
  }

  @SecurityTest
  void getRegulatorManagedInformationPage_discovery_areaTeamMember_assertModelProperties() throws Exception {
    var discoveryResource = resource;
    discoveryResource.setType(ResourceType.DISCOVERY);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(discoveryResource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(discoveryResource))
        .thenReturn(Optional.of(submissionDetail));
    var resourceName = "Sample name";
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(discoveryResource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationFormService.getRegulatorManagedInformationForm(discoveryResource))
        .thenReturn(form);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(internalTeamService.isAreaTeamUser(user)).thenReturn(true);
    when(regulatorManagedInformationFormService.getPreSelectedFieldOption(discoveryResource)).thenReturn(Map.of());
    var preselectedWell = Map.of("well1", "well1");
    when(regulatorManagedInformationFormService.getPreSelectedWell(form.getWell())).thenReturn(preselectedWell);
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
            entry("responsibleTeamTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ResponsibleTeamType.class)),
            entry("shelvedCopFieldApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
                .searchShelvedAndCopFields(null, null))),
            entry("preSelectedFieldOption", Map.of()),
            entry("wellEndpoint", SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null))),
            entry("well", preselectedWell)
        ).containsKey("breadcrumbsMap");
  }

  @ParameterizedTest
  @EnumSource(
      value = ResourceType.class,
      names = {"DISCOVERY", "PROSPECT"}
  )
  void getRegulatorManagedInformationPage_submissionAccepted_assertBreadcrumbsAndCancelUrl(ResourceType resourceType) throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);

    var resourceForAccepted = resource;
    resourceForAccepted.setType(resourceType);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resourceForAccepted));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resourceForAccepted))
        .thenReturn(Optional.of(submissionDetail));
    var resourceName = "Sample name";
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resourceForAccepted))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resourceForAccepted))
        .thenReturn(form);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId, null)))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    var cancelUrl = modelMap.get("cancelUrl");
    assertThat(cancelUrl).isEqualTo(
        ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(resource.getId(), null))
    );

    @SuppressWarnings("unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null, null)),
                SearchProspectAndDiscoveryRecordsController.PAGE_TITLE),
            entry(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)),
                resourceName)
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ResourceType.class,
      names = {"DISCOVERY", "PROSPECT"}
  )
  void getRegulatorManagedInformationPage_submissionSubmitted_assertBreadcrumbsAndCancelUrl(ResourceType resourceType) throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);

    var resourceForAccepted = resource;
    resourceForAccepted.setType(resourceType);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resourceForAccepted));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resourceForAccepted))
        .thenReturn(Optional.of(submissionDetail));
    var resourceName = "Sample name";
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resourceForAccepted))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resourceForAccepted))
        .thenReturn(form);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(resourceId, null)))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    var cancelUrl = (String) modelMap.get("cancelUrl");
    assertThat(cancelUrl).isEqualTo(
        ReverseRouter.route(on(ReviewSubmissionController.class)
            .showReviewPage(resource.getId(), null))
    );

    @SuppressWarnings("unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)),
                WorkAreaController.WORK_AREA_TITLE),
            entry(cancelUrl, resourceName)
        );
  }

  @SecurityTest
  void getRegulatorManagedInformationPage_whenNotResponsibleTeamUser_assertStatusForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);


    mockMvc.perform(
            get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void saveRegulatorManagedInformation_submittedSubmission_assertRedirect() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    var reviewSubmissionUrl = ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resource.getId(), null));

    when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
        .thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(true);
    when(resourceSupportingDocumentService.fileUploadComponentAttributes(anyList(), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(true), eq(true), eq(false)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
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
        .andExpect(redirectedUrl(reviewSubmissionUrl));
  }

  @Test
  void saveRegulatorManagedInformation_submittedSubmission_changedPermissions_assertRedirect() throws Exception {
    when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
        .thenReturn(false);

    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    var workAreaUrl = ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null));

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(true);
    when(resourceSupportingDocumentService.fileUploadComponentAttributes(eq(existingFiles), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(true), eq(true), eq(false)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
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
        .andExpect(redirectedUrl(workAreaUrl));
  }

  @Test
  void saveRegulatorManagedInformation_acceptedSubmission_assertRedirect() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    var viewResourceRecordUrl = ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null));

    when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
        .thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(true);
    when(resourceSupportingDocumentService.fileUploadComponentAttributes(eq(existingFiles), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(true), eq(true), eq(false)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
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
        .andExpect(redirectedUrl(viewResourceRecordUrl));
  }

  @Test
  void saveRegulatorManagedInformation_acceptedSubmission_changedPermissions_assertRedirect() throws Exception {
    when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail)).thenReturn(false);

    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    var viewResourceRecordUrl = ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null));

    when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
        .thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(true);
    when(resourceSupportingDocumentService.fileUploadComponentAttributes(eq(existingFiles), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(true);
    when(regulatorManagedInformationFormService.validate(any(), any(), eq(true), eq(true), eq(false)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
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
        .andExpect(redirectedUrl(viewResourceRecordUrl));
  }

  @SecurityTest
  void saveRegulatorManagedInformation_isNotCorrectRole_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);


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
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(resourceSupportingDocumentService.fileUploadComponentAttributes(any(), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    var resourceName = "The Resource (25/5)";
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(resourceName);
    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(resource))
        .thenReturn(true);
    when(regulatorManagedInformationFormService.getRegulatorManagedInformationForm(resource))
        .thenReturn(form);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource)).thenReturn(false);
    var preselectedWell = Map.of("well1", "well1");
    when(regulatorManagedInformationFormService.getPreSelectedWell(form.getWell())).thenReturn(preselectedWell);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView())
        .getModelMap();
    assertEquals(RegulatorManagedInformationForm.class, modelMap.get("form").getClass());
    assertThat(modelMap).contains(
            entry("pageName", customerConfigurationProperties.mnemonic() + " managed information"),
            entry("geographicalAreas", DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class)),
            entry("cancelUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(resourceId, null))),
            entry("relinquishmentReportTemplate", fileUploadComponentAttributes),
            entry("wellEndpoint", SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null))),
            entry("well", preselectedWell)
        )
        .containsKeys("breadcrumbsMap");

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resourceId, null)),
            resourceName)
    );
  }
}