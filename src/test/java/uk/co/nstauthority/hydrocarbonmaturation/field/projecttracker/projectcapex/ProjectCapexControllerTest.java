package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
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
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectTrackerControllerService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = ProjectCapexController.class)
class ProjectCapexControllerTest extends AbstractControllerTest{
  @MockBean
  private ProjectTrackerControllerService projectTrackerControllerService;
  @MockBean
  private ProjectCapexFormValidator projectCapexFormValidator;
  @MockBean
  private ProjectCapexService projectCapexService;

  private final User USER = UserTestUtil.Builder().build();
  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final UUID PROJECT_ID = UUID.randomUUID();
  private final UUID PROJECT_CAPEX_ID = UUID.randomUUID();

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(RESOURCE_ID, 8000), PROJECT_ID);
  }

  @SecurityTest
  void getAddProjectCapex_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(project));

    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectCapexController.class)
                .getAddProjectCapex(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveProjectCapex_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(project));

    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectCapexController.class)
                .saveProjectCapex(RESOURCE_ID, PROJECT_ID, null, null, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getRemoveProjectCapex_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(project));

    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProjectCapexController.class)
                .getRemoveProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void removeProjectCapex_withoutProjectAnalystPermission_thenForbidden() throws Exception {
    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(project));

    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProjectCapexController.class)
                .removeProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Nested
  class GivenGetAddProjectCapexAndIsProjectAnalyst {
    @BeforeEach
    void setUp() {
      when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    }

    @SecurityTest
    void whenProjectNotExtant_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.empty());

      mockMvc.perform(
              get(ReverseRouter.route(on(ProjectCapexController.class)
                  .getAddProjectCapex(RESOURCE_ID, PROJECT_ID)))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @SecurityTest
    void whenProjectNotForResource_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenThrow(RemiEntityNotFoundException.class);

      mockMvc.perform(
              get(ReverseRouter.route(on(ProjectCapexController.class)
                  .getAddProjectCapex(RESOURCE_ID, PROJECT_ID)))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @EnumSource(value = ProjectType.class)
    void whenProjectForResource_thenAssertProperties(ProjectType projectType) throws Exception {
      project.setProjectType(projectType);

      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      var modelMap = Objects.requireNonNull(mockMvc.perform(
                  get(ReverseRouter.route(on(ProjectCapexController.class)
                      .getAddProjectCapex(RESOURCE_ID, PROJECT_ID)))
                      .with(user(USER)))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/projecttracker/addProjectCapex"))
              .andReturn()
              .getModelAndView())
          .getModelMap();

      assertThat(modelMap)
          .contains(
              entry("pageTitle", ProjectCapexController.ADD_PAGE_TITLE),
              entry("pageCaption", project.getProjectName()),
              entry("projectCapexSourceOptions",
                  ProjectCapexSource.getDisplayableOptionsForProjectType(projectType)),
              entry("cancelUrl",
                  ReverseRouter.route(on(ViewProjectController.class)
                      .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
          )
          .containsKey("form");
    }
  }

  @Nested
  class GivenSaveProjectCapexAndIsProjectAnalyst {
    @BeforeEach
    void setUp() {
      when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    }

    @SecurityTest
    void whenProjectNotExtant_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.empty());

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectCapexController.class)
                  .saveProjectCapex(RESOURCE_ID, PROJECT_ID, null, null, null)))
                  .with(csrf())
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @SecurityTest
    void whenProjectNotForResource_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenThrow(RemiEntityNotFoundException.class);

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectCapexController.class)
                  .saveProjectCapex(RESOURCE_ID, PROJECT_ID, null, null, null)))
                  .with(csrf())
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @EnumSource(value = ProjectType.class)
    void whenProjectForResource_andInvalidForm_thenAssertProperties(ProjectType projectType) throws Exception {
      project.setProjectType(projectType);

      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      var form = new ProjectCapexForm();
      when(projectCapexFormValidator.isValid(eq(form), any(BindingResult.class)))
          .thenReturn(false);

      var modelMap = Objects.requireNonNull(mockMvc.perform(
                  post(ReverseRouter.route(on(ProjectCapexController.class)
                      .saveProjectCapex(RESOURCE_ID, PROJECT_ID, null, null, null)))
                      .flashAttr("form", form)
                      .with(csrf())
                      .with(user(USER))))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/projecttracker/addProjectCapex"))
              .andReturn()
              .getModelAndView()
          .getModelMap();

      assertThat(modelMap)
          .contains(
              entry("pageTitle", ProjectCapexController.ADD_PAGE_TITLE),
              entry("pageCaption", project.getProjectName()),
              entry("form", form),
              entry("projectCapexSourceOptions",
                  ProjectCapexSource.getDisplayableOptionsForProjectType(projectType)),
              entry("cancelUrl",
                  ReverseRouter.route(on(ViewProjectController.class)
                      .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
          );
    }

    @Test
    void whenProjectForResource_andValidForm_thenAssertRedirection() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      var form = new ProjectCapexForm();
      when(projectCapexFormValidator.isValid(eq(form), any(BindingResult.class)))
          .thenReturn(true);

      var expectedBanner = NotificationBanner.builder()
          .withBannerType(NotificationBannerType.SUCCESS)
          .withTitle("Success")
          .withContent("Saved total project CAPEX")
          .build();

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectCapexController.class)
                  .saveProjectCapex(RESOURCE_ID, PROJECT_ID, null, null, null)))
                  .flashAttr("form", form)
                  .with(csrf())
                  .with(user(USER)))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
              .getViewProject(RESOURCE_ID, PROJECT_ID, null))))
          .andExpect(notificationBanner(expectedBanner));

      verify(projectCapexService).saveNewProjectCapex(form, project);
      verifyNoMoreInteractions(projectCapexService);
    }
  }

  @Nested
  class GivenGetRemoveProjectCapexAndIsProjectAnalyst {
    @BeforeEach
    void setUp() {
      when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    }

    @SecurityTest
    void whenProjectNotExtant_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.empty());

      mockMvc.perform(
              get(ReverseRouter.route(on(ProjectCapexController.class)
                  .getRemoveProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID)))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @SecurityTest
    void whenProjectNotForResource_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenThrow(RemiEntityNotFoundException.class);

      mockMvc.perform(
              get(ReverseRouter.route(on(ProjectCapexController.class)
                  .getRemoveProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID)))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @SecurityTest
    void whenProjectCapexDoesNotExist_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      when(projectCapexService.getProjectCapexView(PROJECT_ID))
          .thenReturn(Optional.empty());

      mockMvc.perform(
              get(ReverseRouter.route(on(ProjectCapexController.class)
                  .getRemoveProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID)))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @Test
    void whenProjectForResource_thenAssertProperties() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      var projectCapexView = new ProjectCapexView("3 November 2002", "£50 million", "Draft CSR");
      when(projectCapexService.getProjectCapexView(PROJECT_CAPEX_ID))
          .thenReturn(Optional.of(projectCapexView));

      var modelMap = Objects.requireNonNull(mockMvc.perform(
                  get(ReverseRouter.route(on(ProjectCapexController.class)
                      .getRemoveProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID)))
                      .with(user(USER)))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/projecttracker/removeProjectCapexConfirmation"))
              .andReturn()
              .getModelAndView())
          .getModelMap();

      assertThat(modelMap)
          .contains(
              entry("pageTitle", ProjectCapexController.REMOVE_PAGE_TITLE),
              entry("pageCaption", project.getProjectName()),
              entry("projectCapex", projectCapexView),
              entry("cancelUrl",
                  ReverseRouter.route(on(ViewProjectController.class)
                      .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
          );
    }
  }

  @Nested
  class GivenRemoveProjectCapexAndIsProjectAnalyst {
    @BeforeEach
    void setUp() {
      when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)).thenReturn(true);
    }

    @SecurityTest
    void whenProjectNotExtant_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.empty());

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectCapexController.class)
                  .removeProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID, null)))
                  .with(csrf())
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @SecurityTest
    void whenProjectNotForResource_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenThrow(RemiEntityNotFoundException.class);

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectCapexController.class)
                  .removeProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID, null)))
                  .with(csrf())
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @SecurityTest
    void whenProjectCapexDoesNotExist_thenNotFound() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      when(projectCapexService.getProjectCapexView(PROJECT_ID))
          .thenReturn(Optional.empty());

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectCapexController.class)
                  .removeProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID, null)))
                  .with(csrf())
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @Test
    void whenProjectForResource_thenAssertRedirection() throws Exception {
      when(projectService.getProject(PROJECT_ID))
          .thenReturn(Optional.of(project));
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      var projectCapex = new ProjectCapex(PROJECT_CAPEX_ID);
      projectCapex.setStatus(ProjectCapexStatus.CREATED);
      when(projectCapexService.getProjectCapex(PROJECT_CAPEX_ID))
          .thenReturn(Optional.of(projectCapex));

      var expectedBanner = NotificationBanner.builder()
          .withBannerType(NotificationBannerType.SUCCESS)
          .withTitle("Success")
          .withContent("Removed total project CAPEX")
          .build();

      mockMvc.perform(
              post(ReverseRouter.route(on(ProjectCapexController.class)
                  .removeProjectCapex(RESOURCE_ID, PROJECT_ID, PROJECT_CAPEX_ID, null)))
                  .with(csrf())
                  .with(user(USER)))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
              .getViewProject(RESOURCE_ID, PROJECT_ID, null))))
          .andExpect(notificationBanner(expectedBanner));

      verify(projectCapexService).removeProjectCapex(projectCapex);
    }
  }
}