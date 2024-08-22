package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.StartUpdateDiscoverySubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.StartRelinquishmentSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = DiscoveryPurposeController.class)
class DiscoveryPurposeControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  SubmissionTypeSelectService submissionTypeSelectService;

  @MockBean
  SubmissionCreateService submissionCreateService;
  @Autowired
  private TeamQueryService teamQueryService;

  @SecurityTest
  void startDiscoverySubmission_whenValidFormCreateDiscovery_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryPurposeController.class).startDiscoverySubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_DISCOVERY.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartDiscoveryController.class).getStartPage())));
  }

  @SecurityTest
  void startDiscoverySubmission_whenValidForm_whenNoPermission_assertForbidden() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryPurposeController.class).startDiscoverySubmission(form, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startDiscoverySubmission_whenValidFormCreateDiscoveryFromProspect_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryPurposeController.class).startDiscoverySubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartPostDrillDiscoveryController.class)
            .getStartPostDrillDiscoveryPage())));
  }

  @SecurityTest
  void startDiscoverySubmission_whenValidFormUpdateDiscovery_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryPurposeController.class).startDiscoverySubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.UPDATE_DISCOVERY.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartUpdateDiscoverySubmissionController.class)
            .getStartUpdateDiscoveryPage())));
  }

  @SecurityTest
  void startDiscoverySubmission_whenValidFormRelinquishmentDiscovery_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryPurposeController.class).startDiscoverySubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.RELINQUISHMENT_DISCOVERY.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
            .getStartDiscoveryRelinquishmentPage())));
  }

  @SecurityTest
  void startDiscoverySubmission_whenValidFormCreateProspect_assertForbidden() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryPurposeController.class).startDiscoverySubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_PROSPECT.name()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getSubmissionPurpose_assertModelProperties() throws Exception {
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY, USER)).thenReturn(submissionTypeOptions);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm(null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertEquals(submissionTypeOptions, model.get("submissionTypes"));
    assertEquals(SubmissionTypeSelectForm.class, model.get("form").getClass());
    assertEquals(ReverseRouter.route(on(DiscoveryPurposeController.class).startDiscoverySubmission(null, null, null)),
        model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionType(null)), model.get("backLinkUrl"));
  }
}