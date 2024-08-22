package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesFormService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = ResourceGroupController.class)
class ResourceGroupControllerTest extends AbstractSubmissionControllerTest {
  private static final User USER = UserTestUtil.Builder().build();
  private static final UUID RESOURCE_ID = UUID.randomUUID();
  private static final String RESOURCE_NAME = "Name of resource (22/5)";

  @MockBean
  private RelatedResourcesFormService relatedResourcesFormService;

  @MockBean
  private ResourceService resourceService;

  @MockBean
  private SubmissionResourceService submissionResourceService;

  @MockBean
  private ResourceGroupSummaryService resourceGroupSummaryService;

  @MockBean
  private ManageResourceGroupsService manageResourceGroupsService;

  private RelatedResourcesForm form;
  private BindingResult bindingResult;
  private Resource resource;

  @BeforeEach
  void setUp() {
    form = new RelatedResourcesForm();
    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resource.setId(RESOURCE_ID);
    bindingResult = new BeanPropertyBindingResult(form, "form");
    when(resourceService.getResourceById(RESOURCE_ID))
        .thenReturn(Optional.of(resource));
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail));
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(RESOURCE_NAME);
  }

  @Test
  void getResourceGroupPage_whenNoResourceFound_assertStatusNotFound() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    var invalidResourceId = UUID.randomUUID();
    when(resourceService.getResourceById(invalidResourceId))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceGroupController.class)
                .getResourceGroupPage(invalidResourceId, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void saveResourceGroup_whenNoResourceFound_assertStatusNotFound() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    var invalidResourceId = UUID.randomUUID();
    when(resourceService.getResourceById(invalidResourceId))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceGroupController.class)
                .saveResourceGroup(invalidResourceId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getResourceGroupPage_whenNoLatestApprovedSubmissionDetailFound_assertStatusNotFound() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceGroupController.class)
                .getResourceGroupPage(RESOURCE_ID, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getResourceGroupPage_assertStatusOk() throws Exception {
    when(manageResourceGroupsService.getRelatedResourcesFormFromResource(resource))
        .thenReturn(new RelatedResourcesForm());
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceGroupController.class)
                .getResourceGroupPage(RESOURCE_ID, null)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getResourceGroupPage_andUserIsNotResponsibleTeamUser_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceGroupController.class)
                .getResourceGroupPage(RESOURCE_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveResourceGroupPage_andUserIsNotResponsibleTeamUser_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceGroupController.class)
                .saveResourceGroup(RESOURCE_ID, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveResourceGroupPage_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(manageResourceGroupsService.validateForResource(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(
                on(ResourceGroupController.class).saveResourceGroup(RESOURCE_ID, form, bindingResult)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(RESOURCE_ID, null))));

    verify(manageResourceGroupsService, times(1)).updateResourceGroupLinkFromResource(
        eq(resource),
        any(RelatedResourcesForm.class));
  }

  @Test
  void saveResourceGroupPage_whenInvalidForm_assertStatusOk() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(manageResourceGroupsService.validateForResource(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(
                on(ResourceGroupController.class).saveResourceGroup(RESOURCE_ID, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/review/manageResourceGroups"));

    verify(manageResourceGroupsService, never()).updateResourceGroupLinkFromResource(
        eq(resource),
        any(RelatedResourcesForm.class));  }

  @Test
  void getResourceGroupModelAndView_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(manageResourceGroupsService.getRelatedResourcesFormFromResource(resource))
        .thenReturn(form);
    var resourcesInGroup = getResourceGroupMockSummary();
    when(resourceGroupSummaryService.getResourceGroupMembersSummaryWithoutCurrentResource(resource))
        .thenReturn(resourcesInGroup);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ResourceGroupController.class).getResourceGroupPage(RESOURCE_ID, null)))
                .with(user(USER)))
            .andExpect(view().name("remi/review/manageResourceGroups"))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(RelatedResourcesForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("relatedSubmissionDetail", Map.of()),
            entry("resourceType", "prospect"),
            entry("resourceTypePlural", "prospects"),
            entry("resourcesInGroup", resourcesInGroup),
            entry("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .showResourceRecord(RESOURCE_ID, null))),
            entry("pageTitle", "Related prospects"),
            entry("resourceGroupsEndpoint",
                SearchSelectorService.route(on(ResourceGroupRestController.class)
                    .searchProspects(null, RESOURCE_ID)))
        )
        .containsKeys("breadcrumbsMap");
  }

  @Test
  void getResourceGroupModelAndView_whenProspect_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(manageResourceGroupsService.getRelatedResourcesFormFromResource(resource))
        .thenReturn(form);
    var resourcesInGroup = getResourceGroupMockSummary();
    when(resourceGroupSummaryService.getResourceGroupMembersSummaryWithoutCurrentResource(resource))
        .thenReturn(resourcesInGroup);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ResourceGroupController.class).getResourceGroupPage(RESOURCE_ID, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(RelatedResourcesForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("relatedSubmissionDetail", Map.of()),
            entry("resourceType", "prospect"),
            entry("resourceTypePlural", "prospects"),
            entry("resourcesInGroup", resourcesInGroup),
            entry("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .showResourceRecord(RESOURCE_ID, null))),
            entry("pageTitle", "Related prospects"),
            entry("resourceGroupsEndpoint",
                SearchSelectorService.route(on(ResourceGroupRestController.class)
                    .searchProspects(null, RESOURCE_ID)))
        )
        .containsKeys("breadcrumbsMap");
  }

  @Test
  void getResourceGroupModelAndView_whenDiscovery_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    resource.setType(ResourceType.DISCOVERY);
    when(manageResourceGroupsService.getRelatedResourcesFormFromResource(resource))
        .thenReturn(form);
    var resourcesInGroup = getResourceGroupMockSummary();
    when(resourceGroupSummaryService.getResourceGroupMembersSummaryWithoutCurrentResource(resource))
        .thenReturn(resourcesInGroup);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ResourceGroupController.class).getResourceGroupPage(RESOURCE_ID, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(RelatedResourcesForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("resourceType", "discovery"),
            entry("resourceTypePlural", "discoveries"),
            entry("resourcesInGroup", resourcesInGroup),
            entry("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .showResourceRecord(RESOURCE_ID, null))),
            entry("pageTitle", "Related discoveries"),
            entry("resourceGroupsEndpoint",
                SearchSelectorService.route(on(ResourceGroupRestController.class)
                    .searchDiscoveries(null, RESOURCE_ID)))
        )
        .containsKeys("breadcrumbsMap");
  }

  private List<ResourceItemSummaryView> getResourceGroupMockSummary() {
    return List.of(new ResourceItemSummaryView("", true, RESOURCE_NAME, "true"));
  }
}