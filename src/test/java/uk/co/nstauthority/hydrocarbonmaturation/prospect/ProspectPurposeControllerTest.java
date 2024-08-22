package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartPostDrillDiscoveryController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.StartPostDrillDryHoleSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.StartUpdateProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.StartRelinquishmentSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = ProspectPurposeController.class)
class ProspectPurposeControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  SubmissionTypeSelectService submissionTypeSelectService;

  @MockBean
  SubmissionCreateService submissionCreateService;

  @SecurityTest
  void startProspectSubmission_whenValidFormCreateProspect_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProspectPurposeController.class).startProspectSubmission(form, null, null)))
            .with(user(USER))
            .with(csrf())
            .param("submissionType", SubmissionType.CREATE_PROSPECT.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartProspectController.class).getStartPage())));
  }

  @SecurityTest
  void startProspectSubmission_whenValidForm_whenNoPermission_assertForbidden() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectPurposeController.class).startProspectSubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_PROSPECT.name()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void startProspectSubmission_whenValidFormProspectUpdate_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectPurposeController.class).startProspectSubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.PROSPECT_UPDATE.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartUpdateProspectController.class).getStartUpdatePage())));
  }

  @SecurityTest
  void startProspectSubmission_whenValidFormPostDrillDryHole_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectPurposeController.class).startProspectSubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.POST_DRILL_DRY_HOLE.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class)
            .getStartPostDrillDryHolePage())));
  }

  @SecurityTest
  void startProspectSubmission_whenValidFormCreateDiscoveryFromProspect_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectPurposeController.class).startProspectSubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartPostDrillDiscoveryController.class)
            .getStartPostDrillDiscoveryPage())));
  }

  @SecurityTest
  void startProspectSubmission_whenValidFormRelinquishmentProspect_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectPurposeController.class).startProspectSubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.RELINQUISHMENT_PROSPECT.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
            .getStartProspectRelinquishmentPage())));
  }

  @SecurityTest
  void startProspectSubmission_whenValidFormCreateDiscovery_assertForbidden() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER)).thenReturn(submissionTypeOptions);
    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectPurposeController.class).startProspectSubmission(form, null, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_DISCOVERY.name()))
        .andExpect(status().isForbidden());
  }


  @SecurityTest
  void getProspectPurposeForm_whenNoPermission_assertForbidden() throws Exception {
    when(submissionCreateService.getResourceTypeOptionsForUser(USER)).thenReturn(ResourceType.getResourceTypes());
    when(teamQueryService.userHasRoleInTeamType(eq(USER.wuaId()), any(TeamType.class), any(Role.class))).thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm(null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getSubmissionPurpose_assertModelProperties() throws Exception {
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER)).thenReturn(submissionTypeOptions);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm(null)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertEquals(submissionTypeOptions, model.get("submissionTypes"));
    assertEquals(SubmissionTypeSelectForm.class, model.get("form").getClass());
    assertEquals(ReverseRouter.route(on(ProspectPurposeController.class).startProspectSubmission(null, null, null)),
        model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionType(null)), model.get("backLinkUrl"));
  }
}