package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;


@ContextConfiguration(classes = NewProspectRelinquishmentController.class)
class NewProspectRelinquishmentControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private ProspectRelinquishmentService prospectRelinquishmentService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getRelinquishmentPage_assertModelProperties() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(
            get(ReverseRouter.route(on(NewProspectRelinquishmentController.class).getNewProspectRelinquishmentPage()))
                .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "prospectApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).getStartProspectRelinquishmentPage()),
            ReverseRouter.route(on(NewProspectRelinquishmentController.class)
                .createNewProspectRelinquishmentSubmission(null, null)),
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspects(null, SubmissionType.RELINQUISHMENT_PROSPECT)));
  }

  @SecurityTest
  void createNewRelinquishmentSubmission_asValidForm_assertRedirect() throws Exception {
    var form = new ProspectRelinquishmentForm();

    var bindingResult = new BeanPropertyBindingResult(ProspectRelinquishmentForm.class, "form");
    when(prospectRelinquishmentService.validate(any(), any())).thenReturn(bindingResult);
    when(prospectRelinquishmentService.createProspectRelinquishment(form)).thenReturn(submissionDetail);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewProspectRelinquishmentController.class)
                .createNewProspectRelinquishmentSubmission(null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(
            TaskListController.class).getTaskList(submissionDetailId))));
  }

  @SecurityTest
  void createNewRelinquishmentSubmission_asInvalidForm_assertIsOk() throws Exception {
    var form = new ProspectRelinquishmentForm();

    var bindingResult = new BeanPropertyBindingResult(ProspectRelinquishmentForm.class, "form");
    bindingResult.addError(new ObjectError("form", "invalid form"));
    when(prospectRelinquishmentService.validate(any(), any())).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    var modelAndView = mockMvc.perform(
            post(ReverseRouter.route(on(NewProspectRelinquishmentController.class)
                .createNewProspectRelinquishmentSubmission(null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "form",
            "prospectApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).getStartProspectRelinquishmentPage()),
            ReverseRouter.route(on(NewProspectRelinquishmentController.class)
                .createNewProspectRelinquishmentSubmission(null, null)),
            form,
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspects(null, SubmissionType.RELINQUISHMENT_PROSPECT))
        );

    verify(prospectRelinquishmentService, times(1)).validate(any(), any());
    verify(prospectRelinquishmentService, never()).createProspectRelinquishment(any());
  }
}