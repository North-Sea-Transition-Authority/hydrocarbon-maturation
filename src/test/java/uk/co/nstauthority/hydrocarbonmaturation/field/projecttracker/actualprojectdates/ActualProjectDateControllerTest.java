package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

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

import java.time.LocalDate;
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
import org.springframework.ui.ModelMap;
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
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = ActualProjectDateController.class)
class ActualProjectDateControllerTest extends AbstractControllerTest {
  @MockBean
  private ProjectTrackerControllerService projectTrackerControllerService;
  @MockBean
  private ActualProjectDateFormValidator actualProjectDateFormValidator;
  @MockBean
  private ActualProjectDateService actualProjectDateService;

  private static final User USER = UserTestUtil.Builder().build();

  private static final UUID PROJECT_ID = UUID.randomUUID();
  private static final UUID RESOURCE_ID = UUID.randomUUID();
  private static final UUID PROJECT_DATE_ID = UUID.randomUUID();
  private static final UUID UNRELATED_ID = UUID.randomUUID();

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(RESOURCE_ID, 8000), PROJECT_ID);
    project.setProjectName("Test project");
    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(project));
  }

  @SecurityTest
  void getProjectDate_whenNotProjectAnalyst_thenForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(
            USER.wuaId(),
            TeamType.REGULATOR,
            Role.PROJECT_ANALYST))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ActualProjectDateController.class)
                .getCreateProjectDate(RESOURCE_ID, PROJECT_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = ActualProjectDateType.class)
  void getProjectDate_whenProjectAnalyst(ActualProjectDateType type) throws Exception {
    when(teamQueryService.userHasRoleInTeamType(
            USER.wuaId(),
            TeamType.REGULATOR,
            Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(ActualProjectDateController.class)
                .getCreateProjectDate(RESOURCE_ID, PROJECT_ID, String.valueOf(type))))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void getProjectDate_whenProjectNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(ActualProjectDateController.class)
                .getCreateProjectDate(RESOURCE_ID, PROJECT_ID, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void saveProjectDate_whenProjectNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(ActualProjectDateController.class)
                .getCreateProjectDate(RESOURCE_ID, PROJECT_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void saveProjectDate_whenNotProjectAnalyst_thenForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(
            USER.wuaId(),
            TeamType.REGULATOR,
            Role.PROJECT_ANALYST))
            .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(ActualProjectDateController.class)
                .saveProjectDate(RESOURCE_ID, PROJECT_ID, null, null, null, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveProjectDate_whenProjectAnalyst_thenOk() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(
            USER.wuaId(),
            TeamType.REGULATOR,
            Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(ActualProjectDateController.class)
                .saveProjectDate(RESOURCE_ID, PROJECT_ID, String.valueOf(ActualProjectDateType.CSR_FIRST_DRAFT), null, null, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void getRemoveConfirmation_whenProjectNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(ActualProjectDateController.class)
                .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, PROJECT_DATE_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getRemoveConfirmation_whenProjectAnalyst_thenOk() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(
            USER.wuaId(),
            TeamType.REGULATOR,
            Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    var date = new ActualProjectDate();
    date.setProject(project);
    date.setDate(LocalDate.now());

    when(actualProjectDateService.getDate(PROJECT_DATE_ID)).thenReturn(Optional.of(date));

    mockMvc.perform(
            get(ReverseRouter.route(on(ActualProjectDateController.class)
                .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, PROJECT_DATE_ID, String.valueOf(ActualProjectDateType.CSR_FIRST_DRAFT))))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getRemoveConfirmation_whenNotProjectAnalyst_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(
            USER.wuaId(),
            TeamType.REGULATOR,
            Role.PROJECT_ANALYST))
            .thenReturn(false);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
            .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(ActualProjectDateController.class)
                .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, PROJECT_DATE_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void removeDate_whenProjectNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(ActualProjectDateController.class)
                .removeProjectDate(RESOURCE_ID, PROJECT_ID, PROJECT_DATE_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void removeProjectDate_whenNotProjectAnalyst_thenForbidden() throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasRoleInTeamType(
            USER.wuaId(),
            TeamType.REGULATOR,
            Role.PROJECT_ANALYST))
            .thenReturn(false);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
            .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(ActualProjectDateController.class)
                .removeProjectDate(RESOURCE_ID, PROJECT_ID, PROJECT_DATE_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = ActualProjectDateType.class)
  void removeProjectDate_whenProjectAnalyst_thenRedirect(ActualProjectDateType type) throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasRoleInTeamType(
            USER.wuaId(),
            TeamType.REGULATOR,
            Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
            .thenReturn(project);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    var date = new ActualProjectDate();
    date.setProject(project);
    date.setProjectDateType(type);
    date.setDate(LocalDate.now());

    when(actualProjectDateService.getDate(PROJECT_DATE_ID)).thenReturn(Optional.of(date));

    mockMvc.perform(
            post(ReverseRouter.route(on(ActualProjectDateController.class)
                .removeProjectDate(RESOURCE_ID, PROJECT_ID, PROJECT_DATE_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void getProjectDate_whenProjectDoesNotMatchResource_thenNotFound() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenThrow(RemiEntityNotFoundException.class);

    mockMvc.perform(
                get(ReverseRouter.route(on(ActualProjectDateController.class)
                    .getCreateProjectDate(RESOURCE_ID, PROJECT_ID, String.valueOf(ActualProjectDateType.CSR_FIRST_DRAFT))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
  }

  @Nested
  class GivenUrlIsInValid {
    @Test
    void whenGetAddDate_andInvalidProjectDateType_thenNotFound() throws Exception {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
              .thenReturn(true);
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      mockMvc.perform(
              get(ReverseRouter.route(on(ActualProjectDateController.class)
                  .getCreateProjectDate(RESOURCE_ID, PROJECT_ID, "invalidType")))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @Test
    void whenSaveDate_andInvalidProjectDateType_thenNotFound() throws Exception {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
              .thenReturn(true);
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      mockMvc.perform(
              post(ReverseRouter.route(on(ActualProjectDateController.class)
                  .saveProjectDate(RESOURCE_ID, PROJECT_ID, "invalidType", null, null, null)))
                  .with(csrf())
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @Test
    void whenGetRemoveConfirmation_andInvalidProjectDateType_thenNotFound() throws Exception {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
              .thenReturn(true);
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      mockMvc.perform(
              get(ReverseRouter.route(on(ActualProjectDateController.class)
                  .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, PROJECT_DATE_ID, "invalidType")))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  class GivenIsProjectAnalyst {
    @BeforeEach
    void setUp() {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
              .thenReturn(true);
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);
    }

    @Nested
    class WhenGetActualProjectDate {
      @ParameterizedTest
      @EnumSource(value = ActualProjectDateType.class)
      void andProjectForResourceDoesNotExist_thenNotFound(ActualProjectDateType type) throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                get(ReverseRouter.route(on(ActualProjectDateController.class)
                    .getCreateProjectDate(RESOURCE_ID, UNRELATED_ID, String.valueOf(type))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = ActualProjectDateType.class)
      void assertProperties(ActualProjectDateType type) throws Exception {
        var modelMap = Objects.requireNonNull(mockMvc.perform(
                    get(ReverseRouter.route(on(ActualProjectDateController.class)
                        .getCreateProjectDate(RESOURCE_ID, PROJECT_ID, String.valueOf(type))))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/field/projecttracker/actualprojectdates/addActualDate"))
                .andReturn()
                .getModelAndView())
            .getModelMap();

        assertThat(modelMap)
            .contains(
                entry("pageTitle", type.getDisplayName()),
                entry("pageCaption", project.getProjectName()),
                entry("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
                    .getViewProject(project.getField().getResource().getId(), project.getId(), null)))
            )
            .containsKey("form");
      }
    }

    @Nested
    class WhenSaveActualProjectDate {
      @ParameterizedTest
      @EnumSource(value = ActualProjectDateType.class)
      void andProjectForResourceDoesNotExist_thenNotFound(ActualProjectDateType type) throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                post(ReverseRouter.route(on(ActualProjectDateController.class)
                    .saveProjectDate(RESOURCE_ID, UNRELATED_ID, String.valueOf(type), null, null, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Nested
      class WhenValidForm {
        @BeforeEach
        void setUp() {
          when(actualProjectDateFormValidator.isValid(any(ActualProjectDateForm.class), any(BindingResult.class)))
              .thenReturn(true);
        }

        @ParameterizedTest
        @EnumSource(value = ActualProjectDateType.class)
        void andValidForm_thenAssertPropertiesAndVerifyCall(ActualProjectDateType type) throws Exception {
          var expectedBanner = NotificationBanner.builder()
              .withBannerType(NotificationBannerType.SUCCESS)
              .withTitle("Success")
              .withContent("Saved %s".formatted(type.getLowercaseFormatDisplayName()))
              .build();

          mockMvc.perform(
                  post(ReverseRouter.route(on(ActualProjectDateController.class)
                      .getCreateProjectDate(RESOURCE_ID, PROJECT_ID, String.valueOf(type))))
                      .with(user(USER))
                      .with(csrf()))
              .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
                  .getViewProject(RESOURCE_ID, PROJECT_ID, null))))
              .andExpect(notificationBanner(expectedBanner));

          verify(actualProjectDateService).saveDateFromForm(any(ActualProjectDateForm.class), eq(project), eq(type));
          verifyNoMoreInteractions(actualProjectDateService);
        }
      }

      @Nested
      class WhenInvalidForm {
        @BeforeEach
        void setUp() {
          when(actualProjectDateFormValidator.isValid(any(ActualProjectDateForm.class), any(BindingResult.class)))
              .thenReturn(false);
        }

        @ParameterizedTest
        @EnumSource(value = ActualProjectDateType.class)
        void andProjectMatchesResource_andInvalidForm_thenAssertPropertiesAndVerifyCall(ActualProjectDateType type) throws Exception {
          mockMvc.perform(
                  post(ReverseRouter.route(on(ActualProjectDateController.class)
                      .getCreateProjectDate(RESOURCE_ID, PROJECT_ID, String.valueOf(type))))
                      .with(user(USER))
                      .with(csrf()))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/projecttracker/actualprojectdates/addActualDate"));

          verify(actualProjectDateService, never()).saveDateFromForm(any(), any(), any());
        }
      }
    }

    @Nested
    class WhenGetRemoveConfirmation {
      @ParameterizedTest
      @EnumSource(value = ActualProjectDateType.class)
      void andProjectForResourceDoesNotExist_thenNotFound(ActualProjectDateType type) throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                get(ReverseRouter.route(on(ActualProjectDateController.class)
                    .getRemoveConfirmation(RESOURCE_ID, UNRELATED_ID, PROJECT_DATE_ID, String.valueOf(type))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = ActualProjectDateType.class)
      void andProjectDateDoesNotExist_thenNotFound(ActualProjectDateType type) throws Exception {
        when(actualProjectDateService.getDate(UNRELATED_ID))
            .thenReturn(Optional.empty());

        mockMvc.perform(
                get(ReverseRouter.route(on(ActualProjectDateController.class)
                    .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, String.valueOf(type))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = ActualProjectDateType.class)
      void andProjectDateDoesNotMatchProject_thenNotFound(ActualProjectDateType type) throws Exception {
        var unrelatedDate = new ActualProjectDate();

        var unrelatedProject = new Project();
        unrelatedProject.setId(UUID.randomUUID());
        unrelatedDate.setProject(unrelatedProject);

        when(actualProjectDateService.getDate(UNRELATED_ID))
            .thenReturn(Optional.of(unrelatedDate));

        mockMvc.perform(
                get(ReverseRouter.route(on(ActualProjectDateController.class)
                    .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, String.valueOf(type))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = ActualProjectDateType.class)
      void assertProperties(ActualProjectDateType type) throws Exception {
        var modelMap = assertPropertiesAndReturnModelMap(type);

        assertThat(modelMap)
            .contains(
                entry("pageTitle", "Are you sure you want to remove the %s?"
                    .formatted(type.getLowercaseFormatDisplayName()))
            );
      }

      /* Perform get and assert common properties for any date type
       * and return the model map to check date type specific properties
       */
      private ModelMap assertPropertiesAndReturnModelMap(ActualProjectDateType actualProjectDateType) throws Exception {
        var date = new ActualProjectDate();
        date.setProject(project);
        date.setDate(LocalDate.of(2024, 11, 3));

        when(actualProjectDateService.getDate(PROJECT_DATE_ID)).thenReturn(Optional.of(date));

        var modelMap = Objects.requireNonNull(mockMvc.perform(
                    get(ReverseRouter.route(on(ActualProjectDateController.class)
                        .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, PROJECT_DATE_ID, String.valueOf(actualProjectDateType))))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/field/projecttracker/actualprojectdates/removeActualDate"))
                .andReturn()
                .getModelAndView())
            .getModelMap();

        assertThat(modelMap)
            .contains(
                entry("pageCaption", project.getProjectName()),
                entry("projectDate", "3 November 2024"),
                entry("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
                    .getViewProject(project.getField().getResource().getId(), project.getId(), null)))
            );

        return modelMap;
      }
    }

    @Nested
    class WhenRemoveDate {
      @ParameterizedTest
      @EnumSource(value = ActualProjectDateType.class)
      void andProjectForResourceDoesNotExist_thenNotFound(ActualProjectDateType type) throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                post(ReverseRouter.route(on(ActualProjectDateController.class)
                    .getRemoveConfirmation(RESOURCE_ID, UNRELATED_ID, PROJECT_DATE_ID, String.valueOf(type))))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Test
      void andProjectDateDoesNotExist_thenNotFound() throws Exception {
        when(actualProjectDateService.getDate(UNRELATED_ID))
            .thenReturn(Optional.empty());

        mockMvc.perform(
                post(ReverseRouter.route(on(ActualProjectDateController.class)
                    .removeProjectDate(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Test
      void andProjectDateDoesNotMatchProject_thenNotFound() throws Exception {
        var unrelatedDate = new ActualProjectDate();

        var unrelatedProject = new Project();
        unrelatedProject.setId(UUID.randomUUID());
        unrelatedDate.setProject(unrelatedProject);

        when(actualProjectDateService.getDate(UNRELATED_ID))
            .thenReturn(Optional.of(unrelatedDate));

        mockMvc.perform(
                post(ReverseRouter.route(on(ActualProjectDateController.class)
                    .removeProjectDate(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = ActualProjectDateType.class)
      void assertProperties(ActualProjectDateType type) throws Exception {
        var expectedBanner = NotificationBanner.builder()
            .withBannerType(NotificationBannerType.SUCCESS)
            .withTitle("Success")
            .withContent("Removed %s".formatted(type.getLowercaseFormatDisplayName()))
            .build();

        performRemoveAndAssertProperties(expectedBanner, type);
      }

      private void performRemoveAndAssertProperties(NotificationBanner expectedBanner, ActualProjectDateType type) throws Exception {
        var dates = new ActualProjectDate();
        dates.setProject(project);
        dates.setProjectDateType(type);
        when(actualProjectDateService.getDate(PROJECT_DATE_ID))
            .thenReturn(Optional.of(dates));


        mockMvc.perform(
                post(ReverseRouter.route(on(ActualProjectDateController.class)
                    .removeProjectDate(RESOURCE_ID, PROJECT_ID, PROJECT_DATE_ID, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID, null))))
            .andExpect(notificationBanner(expectedBanner));

        verify(actualProjectDateService).removeDate(dates);
      }
    }
  }
  
  private void mockRegulatorManagedInfoForField(ResponsibleTeamType responsibleTeamType) {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(project.getField().getResource()))
        .thenReturn(regulatorManagedInformation);
  }
}