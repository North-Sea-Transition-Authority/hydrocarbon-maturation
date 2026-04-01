package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectTrackerControllerService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = EnvironmentalActivityController.class)
class EnvironmentalActivityControllerTest extends AbstractControllerTest {
  @MockitoBean
  private ProjectTrackerControllerService projectTrackerControllerService;
  @MockitoBean
  private EnvironmentalActivityService environmentalActivityService;
  @MockitoBean
  private EnvironmentalActivityFormValidator environmentalActivityFormValidator;

  private final User USER = UserTestUtil.Builder().build();
  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final UUID PROJECT_ID = UUID.randomUUID();

  private Project project;

  @BeforeEach
  void setUp() {
    final var FIELD_IDENTIFIER = 8000;
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(RESOURCE_ID, FIELD_IDENTIFIER), PROJECT_ID);

    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(project));
  }

  @SecurityTest
  void getEnvironmentalActivity_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(EnvironmentalActivityController.class)
                .getEnvironmentalActivity(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveEnvironmentalActivity_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(EnvironmentalActivityController.class)
                .saveEnvironmentalActivity(RESOURCE_ID, PROJECT_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void getEnvironmentalActivity_andProjectIsNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectService.getProjectOrThrow(PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(EnvironmentalActivityController.class)
                .getEnvironmentalActivity(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void saveEnvironmentalActivity_andProjectIsNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectService.getProjectOrThrow(PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(EnvironmentalActivityController.class)
                .saveEnvironmentalActivity(RESOURCE_ID, PROJECT_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getEnvironmentalActivity_whenProjectDoesNotMatchResource_thenNotFound() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenThrow(RemiEntityNotFoundException.class);

    mockMvc.perform(
            get(ReverseRouter.route(on(EnvironmentalActivityController.class)
                .getEnvironmentalActivity(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void saveEnvironmentalActivity_whenProjectDoesNotMatchResource_thenNotFound() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenThrow(RemiEntityNotFoundException.class);

    mockMvc.perform(
            post(ReverseRouter.route(on(EnvironmentalActivityController.class)
                .saveEnvironmentalActivity(RESOURCE_ID, PROJECT_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getEnvironmentalActivity_whenProjectExistsAndIsProjectAnalyst_assertProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    var form = new EnvironmentalActivityForm();
    when(environmentalActivityService.getEnvironmentalActivityForm(project))
        .thenReturn(form);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                get(ReverseRouter.route(on(EnvironmentalActivityController.class)
                    .getEnvironmentalActivity(RESOURCE_ID, PROJECT_ID)))
                    .with(user(USER))
                    .flashAttr("form", form))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/field/projecttracker/environmentalActivity"))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", EnvironmentalActivityController.PAGE_TITLE),
            entry("pageCaption", project.getProjectName()),
            entry("form", form),
            entry("cancelUrl",
                ReverseRouter.route(on(ViewProjectController.class)
                    .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
        );
  }

  @Test
  void saveEnvironmentalActivity_whenValidForm_assertRedirection() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    when(environmentalActivityFormValidator.isValid(any(EnvironmentalActivityForm.class), any(BindingResult.class)))
        .thenReturn(true);

    var expectedBanner = NotificationBanner.builder()
        .withBannerType(NotificationBannerType.SUCCESS)
        .withTitle("Success")
        .withContent("Saved environmental statement activities")
        .build();

    mockMvc.perform(
                post(ReverseRouter.route(on(EnvironmentalActivityController.class)
                    .saveEnvironmentalActivity(RESOURCE_ID, PROJECT_ID, null, null, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID, null))))
            .andExpect(notificationBanner(expectedBanner));

    verify(environmentalActivityService).saveEnvironmentalActivity(any(EnvironmentalActivityForm.class), eq(project));
    verifyNoMoreInteractions(environmentalActivityService);
  }

  @Test
  void saveEnvironmentalActivity_whenInvalidForm_assertProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    when(environmentalActivityFormValidator.isValid(any(EnvironmentalActivityForm.class), any(BindingResult.class)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(EnvironmentalActivityController.class)
                .saveEnvironmentalActivity(RESOURCE_ID, PROJECT_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("remi/field/projecttracker/environmentalActivity"));

    verify(environmentalActivityService, never()).saveEnvironmentalActivity(any(), any());
  }
}