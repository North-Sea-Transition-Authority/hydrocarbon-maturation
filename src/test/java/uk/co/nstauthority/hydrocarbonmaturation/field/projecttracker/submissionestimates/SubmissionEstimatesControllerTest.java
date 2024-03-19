package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectTrackerControllerService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = SubmissionEstimatesController.class)
class SubmissionEstimatesControllerTest extends AbstractControllerTest {
  @MockBean
  private PermissionService permissionService;
  @MockBean
  private ProjectTrackerControllerService projectTrackerControllerService;

  private final User USER = UserTestUtil.Builder().build();

  private final UUID PROJECT_ID = UUID.randomUUID();
  private final UUID RESOURCE_ID = UUID.randomUUID();

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(RESOURCE_ID, 8000), PROJECT_ID);
  }

  @SecurityTest
  void getCsrSubmissionEstimates_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                .getCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getCsrSubmissionEstimates_whenProjectDoesNotMatchResource_thenNotFound() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenThrow(RemiEntityNotFoundException.class);

    mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID)))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
  }

  @Test
  void getCsrSubmissionEstimates_whenProjectAnalyst_andProjectMatchesResource_assertProperties() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimatesController.class)
                    .getCsrSubmissionEstimates(RESOURCE_ID, PROJECT_ID)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/projecttracker/addSubmissionEstimates"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle",SubmissionEstimatesController.ESTIMATED_CSR),
            entry("pageCaption", project.getProjectName()),
            entry("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(project.getField().getResource().getId(), project.getId())))
            )
        .containsKey("form");
  }

}