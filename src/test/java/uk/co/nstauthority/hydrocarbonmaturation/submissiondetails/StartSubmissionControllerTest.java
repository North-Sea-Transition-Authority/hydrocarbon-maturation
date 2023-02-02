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
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartDiscoveryController;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartPostDrillDiscoveryController;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.StartUpdateDiscoverySubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.StartProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.StartUpdateProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.StartRelinquishmentSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = StartSubmissionController.class)
class StartSubmissionControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  SubmissionTypeSelectService submissionTypeSelectService;

  @MockBean
  SubmissionCreateService submissionCreateService;

  @SecurityTest
  void startSubmission_whenValidFormAndCreateProspect_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(
        post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(user(USER))
            .with(csrf())
            .param("submissionType", SubmissionType.CREATE_PROSPECT.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartProspectController.class).getStartPage())));
  }

  @SecurityTest
  void startSubmission_whenValidFormAndCreateProspect_whenNoPermission_assertForbidden() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_PROSPECT.name()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startSubmission_whenValidFormAndUpdateProspect_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    mockMvc.perform(post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(user(USER))
            .with(csrf())
            .param("submissionType", SubmissionType.PROSPECT_UPDATE.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartUpdateProspectController.class).getStartUpdatePage())));
  }

  @SecurityTest
  void startSubmission_whenValidFormAndDiscoveryFromProspect_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(user(USER))
            .with(csrf())
            .param("submissionType", SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage())));
  }

  @Test
  void startSubmission_whenValidFormAndDiscovery_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(user(USER))
            .with(csrf())
            .param("submissionType", SubmissionType.CREATE_DISCOVERY.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartDiscoveryController.class).startDiscovery())));
  }

  @SecurityTest
  void startSubmission_whenValidFormAndUpdateDiscovery_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(user(USER))
            .with(csrf())
            .param("submissionType", SubmissionType.UPDATE_DISCOVERY.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(StartUpdateDiscoverySubmissionController.class).getStartUpdateDiscoveryPage())));
  }

  @SecurityTest
  void startSubmission_whenValidFormAndProspectRelinquishment_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(user(USER))
            .with(csrf())
            .param("submissionType", SubmissionType.RELINQUISHMENT_PROSPECT.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).getStartRelinquishmentPage())));
  }

  @SecurityTest
  void startSubmission_whenInValidForm_assertStatusOk() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    mockMvc.perform(post(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(form, null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getSubmissionPurpose_whenNoPermission_assertForbidden() throws Exception {
    when(submissionCreateService.getSubmissionTypeOptionsForUser())
        .thenReturn(SubmissionType.getSubmissionTypes());
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getSubmissionPurpose_assertModelProperties() throws Exception {
    when(submissionCreateService.getSubmissionTypeOptionsForUser())
        .thenReturn(SubmissionType.getSubmissionTypes());
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()))
            .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertEquals(SubmissionType.getSubmissionTypes(), model.get("submissionType"));
    assertEquals(SubmissionTypeSelectForm.class, model.get("form").getClass());
    assertEquals(ReverseRouter.route(on(StartSubmissionController.class).startSubmission(null, null)), model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), model.get("backLinkUrl"));
  }
}