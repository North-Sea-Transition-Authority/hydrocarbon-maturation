package uk.co.nstauthority.hydrocarbonmaturation.field.conceptselectreport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSelectValidator;
import uk.co.nstauthority.hydrocarbonmaturation.field.NewSubmissionFromFieldService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = NewConceptSelectReportController.class)
class NewConceptSelectReportControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private FieldSelectValidator fieldSelectValidator;

  @MockBean
  private NewSubmissionFromFieldService newSubmissionFromFieldService;

  @BeforeEach
  void setUp() {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
  }

  @SecurityTest
  void getNewCsrPage_whenNoPermission_assertForbidden() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.INDUSTRY_SUBMIT,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(NewConceptSelectReportController.class).getNewCsrPage(null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getNewCsrPage_assertModelProperties() throws Exception {
    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                get(ReverseRouter.route(on(NewConceptSelectReportController.class).getNewCsrPage(null)))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView()
        )
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("actionUrl",
                ReverseRouter.route(on(NewConceptSelectReportController.class).createNewCsr(null, null))),
            entry("backLinkUrl", ReverseRouter.route(on(StartConceptSelectReportController.class).getStartPage())),
            entry("fieldApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class).searchFields(null, SubmissionType.CREATE_CSR, null)))
        );
  }

  @SecurityTest
  void createdNewCsr_whenInvalidForm_assertStatusIsOk() throws Exception {
    when(fieldSelectValidator.isValid(any(BindingResult.class))).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewConceptSelectReportController.class)
                .createNewCsr(null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(newSubmissionFromFieldService, never()).createSubmissionFromField(any(FieldSelectForm.class), any(SubmissionType.class));
  }

  @Test
  void createdNewCsr_whenValidForm_assertSaveAndRedirect() throws Exception {
    when(fieldSelectValidator.isValid(any(BindingResult.class))).thenReturn(true);
    var submissionDetail = new SubmissionDetail();
    when(newSubmissionFromFieldService.createSubmissionFromField(any(FieldSelectForm.class), eq(SubmissionType.CREATE_CSR)))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewConceptSelectReportController.class)
                .createNewCsr(null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId()))));
  }
}