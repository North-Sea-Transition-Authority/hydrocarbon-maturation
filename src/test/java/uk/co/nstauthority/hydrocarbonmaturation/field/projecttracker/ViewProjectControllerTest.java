package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = ViewProjectController.class)
class ViewProjectControllerTest extends AbstractProjectTrackerControllerTest {
  @MockitoBean
  private ProjectSummaryService projectSummaryService;

  @SecurityTest
  void whenGetViewProject_andUserIsNotRegulator_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProjectStatus.class,
      names = "EXTANT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void whenGetViewProject_andProjectIsNotExtant_thenNotFound(ProjectStatus status) throws Exception {
    project.setProjectStatus(status);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Nested
  class GivenRegulatorAndProjectExists {
    @BeforeEach
    void setUp() {
      when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    }

    @Test
    void whenGetViewProject_andNonProjectAnalyst_thenAssertModelProperties() throws Exception {
      var summarySections = List.of(mock(SummarySection.class));
      when(projectSummaryService.getSummarySections(project, USER))
          .thenReturn(summarySections);

      var modelMap = Objects.requireNonNull(mockMvc.perform(
                  get(ReverseRouter.route(on(ViewProjectController.class)
                      .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
                      .with(user(USER)))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/projecttracker/viewProject"))
              .andReturn()
              .getModelAndView())
          .getModelMap();

      assertThat(modelMap)
          .contains(
              entry("pageTitle", project.getProjectName()),
              entry("pageCaption", project.getField().getCorrectedLegalFieldName()),
              entry("summarySections", summarySections),
              entry("accordionId", project.getId().toString())
          )
          .containsKey("breadcrumbsMap")
          .doesNotContainKey("deleteUrl");

      @SuppressWarnings(value = "unchecked")
      var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

      assertThat(breadcrumbsMap.entrySet())
          .containsExactly(
              entry(
                  ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null, null)),
                  SearchFieldRecordsController.PAGE_TITLE
              ),
              entry(
                  ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID, USER)),
                  field.getCorrectedLegalFieldName()
              ),
              entry(
                  ReverseRouter.route(on(ProjectListController.class).getProjectList(RESOURCE_ID, null)),
                  ProjectListController.PAGE_TITLE
              )
          );
    }

    @Test
    void whenGetViewProject_andProjectAnalyst_thenAssertModelProperties() throws Exception {
      when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
          .thenReturn(true);

      var summarySections = List.of(mock(SummarySection.class));
      when(projectSummaryService.getSummarySections(project, USER))
          .thenReturn(summarySections);

      var modelMap = Objects.requireNonNull(mockMvc.perform(
                  get(ReverseRouter.route(on(ViewProjectController.class)
                      .getViewProject(RESOURCE_ID, PROJECT_ID, null)))
                      .with(user(USER)))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/projecttracker/viewProject"))
              .andReturn()
              .getModelAndView())
          .getModelMap();

      assertThat(modelMap)
          .contains(
              entry("pageTitle", project.getProjectName()),
              entry("pageCaption", project.getField().getCorrectedLegalFieldName()),
              entry("summarySections", summarySections),
              entry("accordionId", project.getId().toString()),
              entry("deleteUrl", ReverseRouter.route(on(DeleteProjectController.class)
                  .getDeleteProjectConfirmation(project.getField().getResource().getId(), project.getId(), null))))
          .containsKey("breadcrumbsMap");

      @SuppressWarnings(value = "unchecked")
      var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

      assertThat(breadcrumbsMap.entrySet())
          .containsExactly(
              entry(
                  ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null, null)),
                  SearchFieldRecordsController.PAGE_TITLE
              ),
              entry(
                  ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID, null)),
                  field.getCorrectedLegalFieldName()
              ),
              entry(
                  ReverseRouter.route(on(ProjectListController.class).getProjectList(RESOURCE_ID, null)),
                  ProjectListController.PAGE_TITLE
              )
          );
    }
  }
}