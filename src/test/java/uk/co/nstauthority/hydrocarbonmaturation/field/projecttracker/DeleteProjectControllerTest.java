package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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
import static uk.co.nstauthority.hydrocarbonmaturation.util.NotificationBannerTestUtil.notificationBanner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;

@ContextConfiguration(classes = DeleteProjectController.class)
class DeleteProjectControllerTest extends AbstractControllerTest {
  @MockBean
  private ProjectTrackerControllerService projectTrackerControllerService;
  @MockBean
  private ProjectDetailsSummaryService projectDetailsSummaryService;
  @MockBean
  private PermissionService permissionService;

  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final UUID PROJECT_ID = UUID.randomUUID();

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(8000), PROJECT_ID);
    when(projectService.getProject(PROJECT_ID)).thenReturn(Optional.of(project));
  }

  @SecurityTest
  void getDeleteProjectConfirmation_whenNotProjectAnalyst_thenForbidden() throws Exception {
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(DeleteProjectController.class)
                .getDeleteProjectConfirmation(RESOURCE_ID, PROJECT_ID)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getDeleteProjectConfirmation_whenProjectNotExtant_thenNotFound() throws Exception {
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    project.setProjectStatus(ProjectStatus.DELETED);

    mockMvc.perform(
            get(ReverseRouter.route(on(DeleteProjectController.class)
                .getDeleteProjectConfirmation(RESOURCE_ID, PROJECT_ID)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void deleteProject_whenProjectNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    project.setProjectStatus(status);

    mockMvc.perform(
            post(ReverseRouter.route(on(DeleteProjectController.class)
                .getDeleteProjectConfirmation(RESOURCE_ID, PROJECT_ID)))
                .with(csrf())
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @Test
  void getDeleteProjectConfirmation_whenProjectNotForResource_thenNotFound() throws Exception {
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenThrow(RemiEntityNotFoundException.class);

    mockMvc.perform(
            get(ReverseRouter.route(on(DeleteProjectController.class)
                .getDeleteProjectConfirmation(RESOURCE_ID, PROJECT_ID)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @Test
  void getDeleteProjectConfirmation_assertProperties() throws Exception {
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    var summaryCard = new SummaryCard(
        "",
        SummaryCardType.SIMPLE_SUMMARY,
        new SummaryDataView(List.of()),
        List.of());

    when(projectDetailsSummaryService.getProjectSummaryCard(project))
        .thenReturn(summaryCard);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(DeleteProjectController.class)
                    .getDeleteProjectConfirmation(RESOURCE_ID, PROJECT_ID)))
                    .with(user(user)))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/projecttracker/deleteProjectConfirmation"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", DeleteProjectController.DELETE_CONFIRMATION_TITLE),
            entry("pageCaption", project.getProjectName()),
            entry("summaryCard", summaryCard),
            entry("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID, null))));
  }

  @Test
  void deleteProject_assertProperties() throws Exception {
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    var expectedBanner = NotificationBanner.builder()
        .withBannerType(NotificationBannerType.SUCCESS)
        .withTitle("Success")
        .withContent("Project '%s' has been deleted"
            .formatted(project.getProjectName()))
        .build();

    mockMvc.perform(
                post(ReverseRouter.route(on(DeleteProjectController.class)
                    .deleteProject(RESOURCE_ID, PROJECT_ID, null)))
                    .with(user(user))
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(ReverseRouter.route(on(ProjectListController.class)
                .getProjectList(RESOURCE_ID, null))))
            .andExpect(notificationBanner(expectedBanner));

    verify(projectService).deleteProject(project);
  }
}