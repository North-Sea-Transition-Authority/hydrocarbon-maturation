package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.NewResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = PostFieldDiscoveryController.class)
class PostFieldDiscoveryControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  PostFieldDiscoveryService postFieldDiscoveryService;

  @MockBean
  ResourceService resourceService;

  @MockBean
  NewResourceDetailsFormValidator newResourceDetailsFormValidator;

  Resource resource;
  UUID resourceId;

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.FIELD);
    resourceId = resource.getId();
  }

  @SecurityTest
  void getCreatePostField_DiscoveryPage_noPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(false);

    mockMvc.perform(get(ReverseRouter.route(on(PostFieldDiscoveryController.class)
                            .getCreatePostFieldDiscoveryPage(resourceId, null)))
                            .with(user(USER)))
            .andExpect(status().isForbidden());
  }

  @SecurityTest
  void createPostFieldDiscovery_noPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(false);

    mockMvc.perform(post(ReverseRouter.route(on(PostFieldDiscoveryController.class)
                    .createPostFieldDiscovery(resourceId, null, null, null)))
                    .with(user(USER))
                    .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void createPostFieldDiscovery_hasPermission_validForm_assertRedirection() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(true);
    when(resourceService.getResourceById(resourceId)).thenReturn(Optional.of(resource));
    when(newResourceDetailsFormValidator.isValid(any(), eq(ResourceType.DISCOVERY), eq(USER))).thenReturn(true);
    when(postFieldDiscoveryService.createNewDiscoveryFromField(eq(resource), eq(USER), any(ResourceDetailsForm.class)))
        .thenReturn(submissionDetail);

    mockMvc.perform(post(ReverseRouter.route(on(PostFieldDiscoveryController.class)
                    .createPostFieldDiscovery(resourceId, null, null,  null)))
                    .with(user(USER))
                    .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId(), null))));
  }

  @SecurityTest
  void createPostFieldDiscovery_hasPermission_invalidForm_assertOk() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(true);
    when(resourceService.getResourceById(resourceId)).thenReturn(Optional.of(resource));
    when(newResourceDetailsFormValidator.isValid(any(), eq(ResourceType.DISCOVERY), eq(USER))).thenReturn(false);

    mockMvc.perform(post(ReverseRouter.route(on(PostFieldDiscoveryController.class)
            .createPostFieldDiscovery(resourceId, null, null,  null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  void getCreatePostFieldDiscoveryPage_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(true);

    var field = new Field();
    field.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);

    when(fieldPersistenceService.getFieldOrThrow(resourceId)).thenReturn(field);

    var form = new ResourceDetailsForm();
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    var modelMap = Objects.requireNonNull(mockMvc.perform(get(ReverseRouter.route(on(PostFieldDiscoveryController.class)
                    .getCreatePostFieldDiscoveryPage(resourceId, null)))
                .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView())
            .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("backLinkUrl", ReverseRouter.route(on(StartPostFieldDiscoveryController.class)
                    .getStartPostFieldDiscoveryPage(resourceId, null))),
            entry("actionUrl", ReverseRouter.route(on(PostFieldDiscoveryController.class)
                    .createPostFieldDiscovery(resourceId, null, null, null))),
            entry("organisationUnitEndpoint", SearchSelectorService.route(on(OrganisationUnitRestController.class)
                            .searchOrganisationUnits(null, null))),
            entry("resourceType", ResourceType.DISCOVERY.getDisplayName().toLowerCase()),
            entry("isRegulatorOrAreaTeamUser", true),
            entry("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore()),
            entry("alreadySelectedOrganisation",
                organisationUnitQueryService.getPreSelectedOrganisationUnit(
                    NumberUtils.createInteger(form.getResponsibleOrganisationId())))
        );

    var resourceForm = (ResourceDetailsForm) modelMap.get("form");
    
    assertThat(resourceForm).extracting(ResourceDetailsForm::getOnshoreOffshore).isEqualTo(OnshoreOffshore.OFFSHORE);
  }
}
