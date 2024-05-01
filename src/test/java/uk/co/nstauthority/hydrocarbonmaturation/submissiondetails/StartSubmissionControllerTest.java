package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.ProspectPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = StartSubmissionController.class)
class StartSubmissionControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  ResourceTypeSelectService resourceTypeSelectService;

  @MockBean
  SubmissionCreateService submissionCreateService;

  @SecurityTest
  void startSubmission_whenValidFormAndProspect_assertRedirection() throws Exception {
    var form = new ResourceTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(resourceTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    mockMvc.perform(
        post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(user(USER))
            .with(csrf())
            .param("resourceType", ResourceType.PROSPECT.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm())));
  }

  @SecurityTest
  void startSubmission_whenValidFormAndProspect_whenNoPermission_assertForbidden() throws Exception {
    var form = new ResourceTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(resourceTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
                .with(user(USER))
                .with(csrf())
                .param("resourceType", ResourceType.PROSPECT.name()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startSubmission_whenValidFormAndDiscovery_assertRedirection() throws Exception {
    var form = new ResourceTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(resourceTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
                .with(user(USER))
                .with(csrf())
                .param("resourceType", ResourceType.DISCOVERY.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm())));
  }


  @SecurityTest
  void getSubmissionPurpose_whenNoPermission_assertForbidden() throws Exception {
    when(submissionCreateService.getResourceTypeOptionsForUser()).thenReturn(ResourceType.getResourceTypes());
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionType()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getSubmissionPurpose_assertModelProperties() throws Exception {
    var resourceTypes = ResourceType.getResourceTypes();
    resourceTypes.remove(ResourceType.FIELD.name());

    when(submissionCreateService.getResourceTypeOptionsForUser()).thenReturn(resourceTypes);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionType()))
            .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertEquals(resourceTypes, model.get("resourceTypes"));
    assertEquals(ResourceTypeSelectForm.class, model.get("form").getClass());
    assertEquals(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(null, null)), model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), model.get("backLinkUrl"));
  }
}