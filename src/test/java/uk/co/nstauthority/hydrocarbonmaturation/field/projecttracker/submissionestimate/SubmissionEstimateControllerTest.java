package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

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
import uk.co.nstauthority.hydrocarbonmaturation.util.StringUtil;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.util.NotificationBannerTestUtil.notificationBanner;

@ContextConfiguration(classes = SubmissionEstimateController.class)
class SubmissionEstimateControllerTest extends AbstractControllerTest {
  @MockBean
  private ProjectTrackerControllerService projectTrackerControllerService;
  @MockBean
  private SubmissionEstimateFormValidator submissionEstimateFormValidator;
  @MockBean
  private SubmissionEstimateService submissionEstimateService;

  private final User USER = UserTestUtil.Builder().build();

  private final UUID PROJECT_ID = UUID.randomUUID();
  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final UUID SUBMISSION_ESTIMATE_ID = UUID.randomUUID();
  private final UUID UNRELATED_ID = UUID.randomUUID();

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(RESOURCE_ID, 8000), PROJECT_ID);
    project.setProjectName("Test project");
    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(project));
  }

  @SecurityTest
  void getSubmissionEstimate_whenNotResponsibleRegulator_thenForbidden() throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasStaticRole(
            USER.wuaId(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getTeamType(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getRole()))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionEstimateController.class)
                .getCreateSubmissionEstimate(RESOURCE_ID, PROJECT_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionEstimateType.class)
  void getSubmissionEstimate_whenResponsibleRegulator(SubmissionEstimateType type) throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasStaticRole(
            USER.wuaId(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getTeamType(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getRole()))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionEstimateController.class)
                .getCreateSubmissionEstimate(RESOURCE_ID, PROJECT_ID, String.valueOf(type))))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void getSubmissionEstimate_whenProjectNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
        .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionEstimateController.class)
                .getCreateSubmissionEstimate(RESOURCE_ID, PROJECT_ID, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void saveSubmissionEstimate_whenProjectNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionEstimateController.class)
                .getCreateSubmissionEstimate(RESOURCE_ID, PROJECT_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void saveSubmissionEstimate_whenNotResponsibleRegulator_thenForbidden() throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasStaticRole(
            USER.wuaId(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getTeamType(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getRole()))
            .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionEstimateController.class)
                .saveSubmissionEstimate(RESOURCE_ID, PROJECT_ID, null, null, null, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveSubmissionEstimate_whenResponsibleRegulator_thenOk() throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasStaticRole(
            USER.wuaId(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getTeamType(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getRole()))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionEstimateController.class)
                .saveSubmissionEstimate(RESOURCE_ID, PROJECT_ID, String.valueOf(SubmissionEstimateType.CSR), null, null, null)))
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

    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionEstimateController.class)
                .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATE_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getRemoveConfirmation_whenResponsibleRegulator_thenOk() throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasStaticRole(
            USER.wuaId(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getTeamType(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getRole()))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    var estimates = new SubmissionEstimate();
    estimates.setProject(project);
    estimates.setDateWhenEstimated(LocalDate.now());
    estimates.setMostLikelyDate(LocalDate.now());

    when(submissionEstimateService.getSubmissionEstimate(SUBMISSION_ESTIMATE_ID))
        .thenReturn(Optional.of(estimates));

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionEstimateController.class)
                .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATE_ID, String.valueOf(SubmissionEstimateType.CSR))))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getRemoveConfirmation_whenNotResponsibleRegulator_assertForbidden() throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasStaticRole(
            USER.wuaId(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getTeamType(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getRole()))
            .thenReturn(false);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
            .thenReturn(project);

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionEstimateController.class)
                .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATE_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void removeEstimate_whenProjectNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionEstimateController.class)
                .removeEstimate(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATE_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void removeSubmissionEstimate_whenNotResponsibleRegulator_thenForbidden() throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasStaticRole(
            USER.wuaId(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getTeamType(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getRole()))
            .thenReturn(false);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
            .thenReturn(project);

    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionEstimateController.class)
                .removeEstimate(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATE_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionEstimateType.class)
  void removeSubmissionEstimate_whenResponsibleRegulator_thenRedirect(SubmissionEstimateType type) throws Exception {
    mockRegulatorManagedInfoForField(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    when(teamQueryService.userHasStaticRole(
            USER.wuaId(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getTeamType(),
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA.getRole()))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
            .thenReturn(project);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenReturn(project);

    var estimates = new SubmissionEstimate();
    estimates.setProject(project);
    estimates.setSubmissionEstimateType(type);
    estimates.setDateWhenEstimated(LocalDate.now());
    estimates.setMostLikelyDate(LocalDate.now());

    when(submissionEstimateService.getSubmissionEstimate(SUBMISSION_ESTIMATE_ID))
        .thenReturn(Optional.of(estimates));

    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionEstimateController.class)
                .removeEstimate(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATE_ID, null)))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void getSubmissionEstimate_whenProjectDoesNotMatchResource_thenNotFound() throws Exception {
    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(true);
    when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .thenThrow(RemiEntityNotFoundException.class);

    mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .getCreateSubmissionEstimate(RESOURCE_ID, PROJECT_ID, String.valueOf(SubmissionEstimateType.CSR))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
  }

  @Nested
  class GivenUrlIsInValid {
    @Test
    void whenGetAddEstimate_andInvalidSubmissionEstimateType_thenNotFound() throws Exception {
      when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
              .thenReturn(true);
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      mockMvc.perform(
              get(ReverseRouter.route(on(SubmissionEstimateController.class)
                  .getCreateSubmissionEstimate(RESOURCE_ID, PROJECT_ID, "invalidType")))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @Test
    void whenSaveEstimate_andInvalidSubmissionEstimateType_thenNotFound() throws Exception {
      when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
              .thenReturn(true);
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      mockMvc.perform(
              post(ReverseRouter.route(on(SubmissionEstimateController.class)
                  .saveSubmissionEstimate(RESOURCE_ID, PROJECT_ID, "invalidType", null, null, null)))
                  .with(csrf())
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @Test
    void whenGetRemoveConfirmation_andInvalidSubmissionEstimateType_thenNotFound() throws Exception {
      when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
              .thenReturn(true);
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);

      mockMvc.perform(
              get(ReverseRouter.route(on(SubmissionEstimateController.class)
                  .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATE_ID, "invalidType")))
                  .with(user(USER)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  class GivenIsProjectAnalyst {
    @BeforeEach
    void setUp() {
      when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
              .thenReturn(true);
      when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
          .thenReturn(project);
    }

    @Nested
    class WhenGetSubmissionEstimate {
      @ParameterizedTest
      @EnumSource(value = SubmissionEstimateType.class)
      void andProjectForResourceDoesNotExist_thenNotFound(SubmissionEstimateType type) throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .getCreateSubmissionEstimate(RESOURCE_ID, UNRELATED_ID, String.valueOf(type))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = SubmissionEstimateType.class)
      void assertProperties(SubmissionEstimateType type) throws Exception {
        var modelMap = Objects.requireNonNull(mockMvc.perform(
                    get(ReverseRouter.route(on(SubmissionEstimateController.class)
                        .getCreateSubmissionEstimate(RESOURCE_ID, PROJECT_ID, String.valueOf(type))))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/field/projecttracker/submissionestimates/addEstimate"))
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
    class WhenSaveSubmissionEstimate {
      @ParameterizedTest
      @EnumSource(value = SubmissionEstimateType.class)
      void andProjectForResourceDoesNotExist_thenNotFound(SubmissionEstimateType type) throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .saveSubmissionEstimate(RESOURCE_ID, UNRELATED_ID, String.valueOf(type), null, null, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Nested
      class WhenValidForm {
        @BeforeEach
        void setUp() {
          when(submissionEstimateFormValidator.isValid(any(SubmissionEstimateForm.class), any(BindingResult.class)))
              .thenReturn(true);
        }

        @ParameterizedTest
        @EnumSource(value = SubmissionEstimateType.class)
        void andValidForm_thenAssertPropertiesAndVerifyCall(SubmissionEstimateType type) throws Exception {
          var expectedBanner = NotificationBanner.builder()
              .withBannerType(NotificationBannerType.SUCCESS)
              .withTitle("Success")
              .withContent("Saved %s".formatted(StringUtil.withFirstLetterLowerCase(type.getDisplayName())))
              .build();

          mockMvc.perform(
                  post(ReverseRouter.route(on(SubmissionEstimateController.class)
                      .getCreateSubmissionEstimate(RESOURCE_ID, PROJECT_ID, String.valueOf(type))))
                      .with(user(USER))
                      .with(csrf()))
              .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
                  .getViewProject(RESOURCE_ID, PROJECT_ID, null))))
              .andExpect(notificationBanner(expectedBanner));

          verify(submissionEstimateService).saveSubmissionEstimateFromForm(any(SubmissionEstimateForm.class), eq(project), eq(type));
          verifyNoMoreInteractions(submissionEstimateService);
        }
      }

      @Nested
      class WhenInvalidForm {
        @BeforeEach
        void setUp() {
          when(submissionEstimateFormValidator.isValid(any(SubmissionEstimateForm.class), any(BindingResult.class)))
              .thenReturn(false);
        }

        @ParameterizedTest
        @EnumSource(value = SubmissionEstimateType.class)
        void andProjectMatchesResource_andInvalidForm_thenAssertPropertiesAndVerifyCall(SubmissionEstimateType type) throws Exception {
          mockMvc.perform(
                  post(ReverseRouter.route(on(SubmissionEstimateController.class)
                      .getCreateSubmissionEstimate(RESOURCE_ID, PROJECT_ID, String.valueOf(type))))
                      .with(user(USER))
                      .with(csrf()))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/projecttracker/submissionestimates/addEstimate"));

          verify(submissionEstimateService, never()).saveSubmissionEstimateFromForm(any(), any(), any());
        }
      }
    }

    @Nested
    class WhenGetRemoveConfirmation {
      @ParameterizedTest
      @EnumSource(value = SubmissionEstimateType.class)
      void andProjectForResourceDoesNotExist_thenNotFound(SubmissionEstimateType type) throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .getRemoveConfirmation(RESOURCE_ID, UNRELATED_ID, SUBMISSION_ESTIMATE_ID, String.valueOf(type))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = SubmissionEstimateType.class)
      void andSubmissionEstimateDoesNotExist_thenNotFound(SubmissionEstimateType type) throws Exception {
        when(submissionEstimateService.getSubmissionEstimate(UNRELATED_ID))
            .thenReturn(Optional.empty());

        mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, String.valueOf(type))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = SubmissionEstimateType.class)
      void andSubmissionEstimateDoesNotMatchProject_thenNotFound(SubmissionEstimateType type) throws Exception {
        var unrelatedEstimate = new SubmissionEstimate();

        var unrelatedProject = new Project();
        unrelatedProject.setId(UUID.randomUUID());
        unrelatedEstimate.setProject(unrelatedProject);

        when(submissionEstimateService.getSubmissionEstimate(UNRELATED_ID))
            .thenReturn(Optional.of(unrelatedEstimate));

        mockMvc.perform(
                get(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, String.valueOf(type))))
                    .with(user(USER)))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = SubmissionEstimateType.class)
      void assertProperties(SubmissionEstimateType type) throws Exception {
        var modelMap = assertPropertiesAndReturnModelMap(type);

        assertThat(modelMap)
            .contains(
                entry("pageTitle", "Are you sure you want to remove this %s?"
                    .formatted(StringUtil.withFirstLetterLowerCase(type.getDisplayName())))
            );
      }

      /* Perform get and assert common properties for any estimate type
       * and return the model map to check estimate type specific properties
       */
      private ModelMap assertPropertiesAndReturnModelMap(SubmissionEstimateType submissionEstimateType) throws Exception {
        final var FIXED_DATE = LocalDate.of(2024, 11, 3);

        var estimates = new SubmissionEstimate();
        estimates.setProject(project);
        estimates.setDateWhenEstimated(FIXED_DATE);
        estimates.setMostLikelyDate(FIXED_DATE.minusDays(2));

        when(submissionEstimateService.getSubmissionEstimate(SUBMISSION_ESTIMATE_ID))
            .thenReturn(Optional.of(estimates));

        var modelMap = Objects.requireNonNull(mockMvc.perform(
                    get(ReverseRouter.route(on(SubmissionEstimateController.class)
                        .getRemoveConfirmation(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATE_ID, String.valueOf(submissionEstimateType))))
                        .with(user(USER)))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/field/projecttracker/submissionestimates/removeEstimateConfirmation"))
                .andReturn()
                .getModelAndView())
            .getModelMap();

        assertThat(modelMap)
            .contains(
                entry("pageCaption", project.getProjectName()),
                entry("estimates", new SubmissionEstimateView(
                    "3 November 2024",
                    "",
                    "1 November 2024",
                    ""
                )),
                entry("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
                    .getViewProject(project.getField().getResource().getId(), project.getId(), null)))
            );

        return modelMap;
      }
    }

    @Nested
    class WhenRemoveEstimate {
      @ParameterizedTest
      @EnumSource(value = SubmissionEstimateType.class)
      void andProjectForResourceDoesNotExist_thenNotFound(SubmissionEstimateType type) throws Exception {
        when(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, UNRELATED_ID))
            .thenThrow(RemiEntityNotFoundException.class);

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .getRemoveConfirmation(RESOURCE_ID, UNRELATED_ID, SUBMISSION_ESTIMATE_ID, String.valueOf(type))))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Test
      void andSubmissionEstimateDoesNotExist_thenNotFound() throws Exception {
        when(submissionEstimateService.getSubmissionEstimate(UNRELATED_ID))
            .thenReturn(Optional.empty());

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .removeEstimate(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @Test
      void andSubmissionEstimateDoesNotMatchProject_thenNotFound() throws Exception {
        var unrelatedEstimate = new SubmissionEstimate();

        var unrelatedProject = new Project();
        unrelatedProject.setId(UUID.randomUUID());
        unrelatedEstimate.setProject(unrelatedProject);

        when(submissionEstimateService.getSubmissionEstimate(UNRELATED_ID))
            .thenReturn(Optional.of(unrelatedEstimate));

        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .removeEstimate(RESOURCE_ID, PROJECT_ID, UNRELATED_ID, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().isNotFound());
      }

      @ParameterizedTest
      @EnumSource(value = SubmissionEstimateType.class)
      void assertProperties(SubmissionEstimateType type) throws Exception {
        var expectedBanner = NotificationBanner.builder()
            .withBannerType(NotificationBannerType.SUCCESS)
            .withTitle("Success")
            .withContent("Removed %s".formatted(StringUtil.withFirstLetterLowerCase(type.getDisplayName())))
            .build();

        performRemoveAndAssertProperties(expectedBanner, type);
      }

      private void performRemoveAndAssertProperties(NotificationBanner expectedBanner, SubmissionEstimateType type) throws Exception {
        var estimates = new SubmissionEstimate();
        estimates.setProject(project);
        estimates.setSubmissionEstimateType(type);
        when(submissionEstimateService.getSubmissionEstimate(SUBMISSION_ESTIMATE_ID))
            .thenReturn(Optional.of(estimates));


        mockMvc.perform(
                post(ReverseRouter.route(on(SubmissionEstimateController.class)
                    .removeEstimate(RESOURCE_ID, PROJECT_ID, SUBMISSION_ESTIMATE_ID, null)))
                    .with(user(USER))
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID, null))))
            .andExpect(notificationBanner(expectedBanner));

        verify(submissionEstimateService).removeEstimate(estimates);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void mockRegulatorManagedInfoForField(ResponsibleTeamType responsibleTeamType) {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(project.getField().getResource()))
        .thenReturn(regulatorManagedInformation);
  }
}