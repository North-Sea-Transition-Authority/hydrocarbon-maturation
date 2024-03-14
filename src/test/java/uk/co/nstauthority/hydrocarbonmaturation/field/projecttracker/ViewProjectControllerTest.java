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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ContextConfiguration(classes = ViewProjectController.class)
class ViewProjectControllerTest extends AbstractProjectTrackerControllerTest {
  @MockBean
  private ProjectSummaryService projectSummaryService;

  @SecurityTest
  void whenGetViewProject_andUserIsNotRegulator_thenForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(RESOURCE_ID, PROJECT_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Nested
  class GivenRegulator {
    @BeforeEach
    void setUp() {
      setupUserIsRegulator(user);
    }

    @Test
    void whenGetViewProject_thenAssertModelProperties() throws Exception {
      var summarySections = List.of(mock(SummarySection.class));
      when(projectSummaryService.getSummarySections(project))
          .thenReturn(summarySections);

      var modelMap = Objects.requireNonNull(mockMvc.perform(
                  get(ReverseRouter.route(on(ViewProjectController.class)
                      .getViewProject(RESOURCE_ID, PROJECT_ID)))
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
          ).containsKey("breadcrumbsMap");

      @SuppressWarnings(value = "unchecked")
      var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

      assertThat(breadcrumbsMap.entrySet())
          .containsExactly(
              entry(
                  ReverseRouter.route(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null)),
                  SearchFieldRecordsController.PAGE_TITLE
              ),
              entry(
                  ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID)),
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