package uk.co.nstauthority.hydrocarbonmaturation.field;

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

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.cessationofproduction.StartCessationOfProductionController;
import uk.co.nstauthority.hydrocarbonmaturation.field.conceptselectreport.StartConceptSelectReportController;
import uk.co.nstauthority.hydrocarbonmaturation.field.fielddevelopmentplan.StartFieldDevelopmentPlanController;
import uk.co.nstauthority.hydrocarbonmaturation.field.fielddevelopmentplanaddendum.StartFieldDevelopmentPlanAddendumController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectService;

@ContextConfiguration(classes = FieldPurposeController.class)
class FieldPurposeControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  SubmissionTypeSelectService submissionTypeSelectService;

  @MockBean
  SubmissionCreateService submissionCreateService;

  @SecurityTest
  void startFieldSubmission_whenValidFormCreateField_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldPurposeController.class).startFieldSubmission(form, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_FIELD.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartFieldController.class).getStartPage())));
  }

  @SecurityTest
  void startFieldSubmission_whenValidFormCreateCsr_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldPurposeController.class).startFieldSubmission(form, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_CSR.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartConceptSelectReportController.class).getStartPage())));
  }

  @SecurityTest
  void startFieldSubmission_whenValidFormCreateFdp_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldPurposeController.class).startFieldSubmission(form, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_FDP.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartFieldDevelopmentPlanController.class).getStartPage())));
  }

  @SecurityTest
  void startFieldSubmission_whenValidFormCreateFdpa_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldPurposeController.class).startFieldSubmission(form, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_FDPA.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartFieldDevelopmentPlanAddendumController.class).getStartPage())));
  }

  @SecurityTest
  void startFieldSubmission_whenValidFormCreateCop_assertRedirection() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldPurposeController.class).startFieldSubmission(form, null)))
                .with(user(USER))
                .with(csrf())
                .param("submissionType", SubmissionType.CREATE_COP.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(StartCessationOfProductionController.class).getStartPage())));
  }

  @SecurityTest
  void startFieldSubmission_whenValidForm_whenNoPermission_assertForbidden() throws Exception {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    when(submissionTypeSelectService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldPurposeController.class).startFieldSubmission(form, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getSubmissionPurpose_assertModelProperties() throws Exception {
    var submissionTypeOptions = List.of(SubmissionType.values());

    when(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.FIELD)).thenReturn(submissionTypeOptions);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(FieldPurposeController.class).getFieldPurposeForm()))
                .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertEquals(submissionTypeOptions, model.get("submissionTypes"));
    assertEquals(SubmissionTypeSelectForm.class, model.get("form").getClass());
    assertEquals(ReverseRouter.route(on(FieldPurposeController.class).startFieldSubmission(null, null)),
        model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionType()), model.get("backLinkUrl"));
  }
}