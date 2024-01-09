package uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Map;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceView;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminedFieldRestController;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.NewFieldService;
import uk.co.nstauthority.hydrocarbonmaturation.field.StartFieldController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = FieldRegulatorManagedInformationController.class)
class FieldRegulatorManagedInformationControllerTest extends AbstractSubmissionControllerTest {
  @MockBean
  private ResponsibleTeamService responsibleTeamService;
  @MockBean
  private ResourceService resourceService;
  @MockBean
  private NewFieldService newFieldService;
  @MockBean
  private SubmissionResourceService submissionResourceService;
  @MockBean
  private FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService;

  private FieldForm form;
  private BindingResult bindingResult;
  private UUID resourceId;
  private Resource resource;

  @BeforeEach
  void setUp() {
    resource = new Resource();
    form = new FieldForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceId = UUID.randomUUID();
    resource.setId(resourceId);
    submissionDetail.getSubmission().setResource(resource);
  }

  @Test
  void getFieldRegulatorManagedInformationPage_whenNoResource_assertStatus() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))
    ).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void saveFieldRegulatorManagedInformation_whenNoResource_assertStatus() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))
    ).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getFieldRegulatorManagedInformationPage_whenNoSubmittedOrAcceptedSubmissionDetail_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))
    ).thenReturn(true);
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFieldRegulatorManagedInformationPage_whenNotResponsibleTeamUser_assertStatusForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    when(permissionService.hasPermission(USER, Set.of(
        RolePermission.EXPLORATION,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))
    ).thenReturn(false);

    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));

    when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getFieldRegulatorManagedInformationPage_assertProperties() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    var acceptedSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    acceptedSubmissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(acceptedSubmissionDetail));
    when(responsibleTeamService.isInResponsibleTeam(USER, acceptedSubmissionDetail))
        .thenReturn(true);

    form.setResponsibleOrganisationId("organisationId");
    form.setParentDeterminedField("parent");
    form.setAssociatedDiscoveries(List.of("discoveries"));
    when(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .thenReturn(form);

    var expectedResponsibleOrganisation = Map.of("organisation", "unit");
    when(newFieldService.getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId()))
        .thenReturn(expectedResponsibleOrganisation);

    var expectedGeographicalArea = Map.of("geographical", "areas");
    when(newFieldService.getGeographicalAreaDisplayableOptions())
        .thenReturn(expectedGeographicalArea);

    var expectedResponsibleTeamTypes = Map.of("responsible", "team");
    when(newFieldService.getResponsibleTeamTypeDisplayableOptions())
        .thenReturn(expectedResponsibleTeamTypes);

    var expectedParentDeterminedField = Map.of("parent", "determined");
    when(newFieldService.getPreSelectedParentDeterminedField(form.getParentDeterminedField()))
        .thenReturn(expectedParentDeterminedField);

    var expectedAssociatedDiscoveries = List.of(new ResourceView(UUID.randomUUID(), ""));
    when(newFieldService.getPreSelectedAssociatedDiscoveriesMap(form.getAssociatedDiscoveries()))
        .thenReturn(expectedAssociatedDiscoveries);

    var expectedBreadcrumbTitle = "title";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(acceptedSubmissionDetail))
        .thenReturn(expectedBreadcrumbTitle);

    var modelMap = mockMvc.perform(
            get(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/field/newField"))
        .andReturn()
        .getModelAndView()
        .getModelMap();

    assertEquals(FieldForm.class, modelMap.get("form").getClass());

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("backLinkUrl", ReverseRouter.route(on(StartFieldController.class).getStartPage())),
            entry("pageTitle", FieldRegulatorManagedInformationController.PAGE_NAME),
            entry("organisationUnitEndpoint",
                SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null))),
            entry("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore()),
            entry("geographicalAreas", expectedGeographicalArea),
            entry("responsibleTeamTypes", expectedResponsibleTeamTypes),
            entry("determinationStatus", DisplayableEnumOptionUtil.getDisplayableOptions(DeterminationStatus.class)),
            entry("parentDeterminedFieldEndpoint",
                SearchSelectorService.route(on(DeterminedFieldRestController.class).searchDeterminedFields(null))),
            entry("discoveriesApiEndpoint",
                SearchSelectorService.route(on(ResourceRestController.class)
                    .searchDiscoveriesNotInGroupOrPreferredView(null))),
            entry("responsibleOrganisation", expectedResponsibleOrganisation),
            entry("parentDeterminedField", expectedParentDeterminedField),
            entry("alreadyAddedAssociatedDiscoveries", expectedAssociatedDiscoveries)
            ).containsKey("breadcrumbsMap");
  }

  @Test
  void saveRegulatorManagedInformation_validForm_submittedSubmission_assertRedirection() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    var reviewSubmissionUrl = ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(resource.getId()));

    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(fieldRegulatorManagedInformationService.isValidNewFieldForm(any(FieldForm.class), any(BindingResult.class)))
        .thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(user, submissionDetail)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(reviewSubmissionUrl));

    verify(fieldRegulatorManagedInformationService, times(1)).saveField(any(FieldForm.class), eq(resource));
  }

  @Test
  void saveRegulatorManagedInformation_validForm_submissionSubmitted_changedPermissions_assertRedirection() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    when(responsibleTeamService.isInResponsibleTeam(user, submissionDetail))
        .thenReturn(false);
    var workAreaUrl = ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null));

    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(fieldRegulatorManagedInformationService.isValidNewFieldForm(any(FieldForm.class), any(BindingResult.class)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(workAreaUrl));

    verify(fieldRegulatorManagedInformationService, times(1)).saveField(any(FieldForm.class), eq(resource));
  }

  @Test
  void saveRegulatorManagedInformation_validForm_acceptedSubmission_assertRedirection() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    var viewResourceRecordUrl = ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId()));

    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(fieldRegulatorManagedInformationService.isValidNewFieldForm(any(FieldForm.class), any(BindingResult.class)))
        .thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(user, submissionDetail)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(viewResourceRecordUrl));

    verify(fieldRegulatorManagedInformationService, times(1)).saveField(any(FieldForm.class), eq(resource));
  }

  @Test
  void saveRegulatorManagedInformation_validForm_submissionAccepted_changedPermissions_assertRedirection() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    when(responsibleTeamService.isInResponsibleTeam(user, submissionDetail))
        .thenReturn(false);
    var searchResourceRecordsUrl = ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId));

    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(fieldRegulatorManagedInformationService.isValidNewFieldForm(any(FieldForm.class), any(BindingResult.class)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(searchResourceRecordsUrl));

    verify(fieldRegulatorManagedInformationService, times(1)).saveField(any(FieldForm.class), eq(resource));
  }

  @Test
  void saveRegulatorManagedInformation_invalidForm_assertStatusOkay() throws Exception {
    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(fieldRegulatorManagedInformationService.isValidNewFieldForm(any(FieldForm.class), any(BindingResult.class)))
        .thenReturn(false);
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(any()))
        .thenReturn("breadcrumb title");

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .saveRegulatorManagedInformation(resourceId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("remi/field/newField"));

    verify(fieldRegulatorManagedInformationService, never()).saveField(any(FieldForm.class), any(Resource.class));
  }

  @Test
  void getRegulatorManagedInformationPage_submissionAccepted_assertBreadcrumbsAndCancelUrl() throws Exception {
    var acceptedSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    acceptedSubmissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);

    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(acceptedSubmissionDetail));
    when(responsibleTeamService.isInResponsibleTeam(USER, acceptedSubmissionDetail))
        .thenReturn(true);

    form.setResponsibleOrganisationId("organisationId");
    form.setParentDeterminedField("parent");
    form.setAssociatedDiscoveries(List.of("discoveries"));
    when(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .thenReturn(form);

    var expectedResponsibleOrganisation = Map.of("organisation", "unit");
    when(newFieldService.getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId()))
        .thenReturn(expectedResponsibleOrganisation);

    var expectedGeographicalArea = Map.of("geographical", "areas");
    when(newFieldService.getGeographicalAreaDisplayableOptions())
        .thenReturn(expectedGeographicalArea);

    var expectedResponsibleTeamTypes = Map.of("responsible", "team");
    when(newFieldService.getResponsibleTeamTypeDisplayableOptions())
        .thenReturn(expectedResponsibleTeamTypes);

    var expectedParentDeterminedField = Map.of("parent", "determined");
    when(newFieldService.getPreSelectedParentDeterminedField(form.getParentDeterminedField()))
        .thenReturn(expectedParentDeterminedField);

    var expectedAssociatedDiscoveries = List.of(new ResourceView(UUID.randomUUID(), ""));
    when(newFieldService.getPreSelectedAssociatedDiscoveriesMap(form.getAssociatedDiscoveries()))
        .thenReturn(expectedAssociatedDiscoveries);

    var expectedBreadcrumbTitle = "title";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(acceptedSubmissionDetail))
        .thenReturn(expectedBreadcrumbTitle);

    var modelMap = mockMvc.perform(
            get(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()
        .getModelMap();

    var cancelUrl = modelMap.get("cancelUrl");
    assertThat(cancelUrl).isEqualTo(
        ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(resource.getId()))
    );

    @SuppressWarnings("unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null)),
                SearchFieldRecordsController.PAGE_TITLE),
            entry(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId)),
                expectedBreadcrumbTitle)
        );
  }

  @Test
  void getRegulatorManagedInformationPage_submissionSubmitted_assertBreadcrumbsAndCancelUrl() throws Exception {
    var submittedSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    submittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);

    when(resourceService.getResourceById(resourceId))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submittedSubmissionDetail));
    when(responsibleTeamService.isInResponsibleTeam(USER, submittedSubmissionDetail))
        .thenReturn(true);

    form.setResponsibleOrganisationId("organisationId");
    form.setParentDeterminedField("parent");
    form.setAssociatedDiscoveries(List.of("discoveries"));
    when(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .thenReturn(form);

    var expectedResponsibleOrganisation = Map.of("organisation", "unit");
    when(newFieldService.getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId()))
        .thenReturn(expectedResponsibleOrganisation);

    var expectedGeographicalArea = Map.of("geographical", "areas");
    when(newFieldService.getGeographicalAreaDisplayableOptions())
        .thenReturn(expectedGeographicalArea);

    var expectedResponsibleTeamTypes = Map.of("responsible", "team");
    when(newFieldService.getResponsibleTeamTypeDisplayableOptions())
        .thenReturn(expectedResponsibleTeamTypes);

    var expectedParentDeterminedField = Map.of("parent", "determined");
    when(newFieldService.getPreSelectedParentDeterminedField(form.getParentDeterminedField()))
        .thenReturn(expectedParentDeterminedField);

    var expectedAssociatedDiscoveries = List.of(new ResourceView(UUID.randomUUID(), ""));
    when(newFieldService.getPreSelectedAssociatedDiscoveriesMap(form.getAssociatedDiscoveries()))
        .thenReturn(expectedAssociatedDiscoveries);

    var expectedBreadcrumbTitle = "title";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submittedSubmissionDetail))
        .thenReturn(expectedBreadcrumbTitle);

    var modelMap = mockMvc.perform(
            get(ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()
        .getModelMap();

    var cancelUrl = (String) modelMap.get("cancelUrl");
    assertThat(cancelUrl).isEqualTo(
        ReverseRouter.route(on(ReviewSubmissionController.class)
            .showReviewPage(resource.getId()))
    );

    @SuppressWarnings("unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)),
                WorkAreaController.WORK_AREA_TITLE),
            entry(cancelUrl, expectedBreadcrumbTitle)
        );
  }
}