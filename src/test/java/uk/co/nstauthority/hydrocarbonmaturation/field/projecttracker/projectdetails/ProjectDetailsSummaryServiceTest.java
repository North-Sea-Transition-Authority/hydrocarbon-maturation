package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectPhase;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class ProjectDetailsSummaryServiceTest {
  private static final Instant FIXED_INSTANT = Instant.parse("2024-02-01T00:00:00.00Z");
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private TeamQueryService teamQueryService;
  @InjectMocks
  private ProjectDetailsSummaryService projectDetailsSummaryService;

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createPopulatedProject(
        "Project",
        ProjectType.FIELD_DEVELOPMENT_PLAN,
        ProjectPhase.ASSESSMENT_PHASE,
        true,
        FIXED_INSTANT);
  }

  @Nested
  class GivenAnyUser {
    @Test
    void whenGetSummarySection_projectTypeCop_thenAssertCommonProperties() {
      project.setProjectType(ProjectType.CESSATION_OF_PRODUCTION);

      var acceptedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      acceptedSubmissionDetail.setAcceptedTimestamp(Instant.parse("2023-12-01T00:00:00.00Z"));
      project.setSubmissionDetail(acceptedSubmissionDetail);

      var summarySectionOptional = projectDetailsSummaryService.getSummarySection(project, USER);

      assertThat(summarySectionOptional)
          .isPresent()
          .get()
          .extracting(
              SummarySection::summaryText,
              SummarySection::displayOrder
          ).containsExactly(
              "",
              ProjectDetailsSummaryService.DISPLAY_ORDER
          );

      var summaryItems = summarySectionOptional.get().summaryItems();

      assertThat(summaryItems)
          .hasSize(1)
          .extracting(SummaryItem::displayName)
          .contains(ProjectDetailsSummaryService.PROJECT_DETAILS);

      var summaryItem = summaryItems.get(0);
      assertThat(summaryItem.displayName()).isEqualTo(ProjectDetailsSummaryService.PROJECT_DETAILS);
      assertThat(summaryItem.summaryCards()).hasSize(1);

      var summaryCard = summaryItem.summaryCards().get(0);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectDetailsSummaryService.PROJECT_DETAILS,
              SummaryCardType.SIMPLE_SUMMARY
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryDataView.class);

      var expectedKeyValues = List.of(
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_NAME, project.getProjectName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_TYPE, project.getProjectType().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PIPELINE, "Yes"),
          new SummaryKeyValue(ProjectDetailsSummaryService.ASSOCIATED_SUBMISSION,
              "Concept select report accepted on 1 December 2023")
      );

            assertThat(((SummaryDataView) summaryData).keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }

    @Test
    void whenGetSummarySection_forFdpa_thenAssertCommonProperties() {
      var acceptedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      acceptedSubmissionDetail.setAcceptedTimestamp(Instant.parse("2023-12-01T00:00:00.00Z"));
      project.setSubmissionDetail(acceptedSubmissionDetail);

      var fdpaStartUpDate = LocalDate.of(2024, Month.MARCH, 5);
      project.setFdpaStartUpDate(fdpaStartUpDate);

      var summarySectionOptional = projectDetailsSummaryService.getSummarySection(project, USER);

      assertThat(summarySectionOptional)
          .isPresent()
          .get()
          .extracting(
              SummarySection::summaryText,
              SummarySection::displayOrder
          ).containsExactly(
              "",
              ProjectDetailsSummaryService.DISPLAY_ORDER
          );

      var summaryItems = summarySectionOptional.get().summaryItems();

      assertThat(summaryItems)
          .hasSize(1)
          .extracting(SummaryItem::displayName)
          .contains(ProjectDetailsSummaryService.PROJECT_DETAILS);

      var summaryItem = summaryItems.get(0);
      assertThat(summaryItem.displayName()).isEqualTo(ProjectDetailsSummaryService.PROJECT_DETAILS);
      assertThat(summaryItem.summaryCards()).hasSize(1);

      var summaryCard = summaryItem.summaryCards().get(0);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectDetailsSummaryService.PROJECT_DETAILS,
              SummaryCardType.SIMPLE_SUMMARY
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryDataView.class);

      var expectedKeyValues = List.of(
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_NAME, project.getProjectName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_TYPE, project.getProjectType().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PHASE, project.getProjectPhase().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PIPELINE, "Yes"),
          new SummaryKeyValue(ProjectDetailsSummaryService.ASSOCIATED_SUBMISSION,
              "Concept select report accepted on 1 December 2023"),
          new SummaryKeyValue(ProjectDetailsSummaryService.FDPA_START_UP_DATE,
              "5 March 2024")
      );

            assertThat(((SummaryDataView) summaryData).keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }

    @Test
    void whenGetProjectSummaryCard_forCop_thenAssertCommonProperties() {
      project.setProjectType(ProjectType.CESSATION_OF_PRODUCTION);

      var acceptedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      acceptedSubmissionDetail.setAcceptedTimestamp(Instant.parse("2023-12-01T00:00:00.00Z"));
      project.setSubmissionDetail(acceptedSubmissionDetail);

      var summaryCard = projectDetailsSummaryService.getProjectSummaryCard(project, USER);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectDetailsSummaryService.PROJECT_DETAILS,
              SummaryCardType.SIMPLE_SUMMARY
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryDataView.class);

      var expectedKeyValues = List.of(
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_NAME, project.getProjectName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_TYPE, project.getProjectType().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PIPELINE, "Yes"),
          new SummaryKeyValue(ProjectDetailsSummaryService.ASSOCIATED_SUBMISSION,
              "Concept select report accepted on 1 December 2023")
      );

            assertThat(((SummaryDataView) summaryData).keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }

    @Test
    void whenGetProjectSummaryCard_forFdp_thenAssertCommonProperties() {
      project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);

      var acceptedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      acceptedSubmissionDetail.setAcceptedTimestamp(Instant.parse("2023-12-01T00:00:00.00Z"));
      project.setSubmissionDetail(acceptedSubmissionDetail);

      var summaryCard = projectDetailsSummaryService.getProjectSummaryCard(project, USER);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectDetailsSummaryService.PROJECT_DETAILS,
              SummaryCardType.SIMPLE_SUMMARY
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryDataView.class);

      var expectedKeyValues = List.of(
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_NAME, project.getProjectName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_TYPE, project.getProjectType().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PHASE, project.getProjectPhase().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PIPELINE, "Yes"),
          new SummaryKeyValue(ProjectDetailsSummaryService.ASSOCIATED_SUBMISSION,
              "Concept select report accepted on 1 December 2023")
      );

            assertThat(((SummaryDataView) summaryData).keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }

    @Test
    void whenGetSummarySection_forFdp_thenAssertCommonProperties() {
      project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);

      var acceptedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      acceptedSubmissionDetail.setAcceptedTimestamp(Instant.parse("2023-12-01T00:00:00.00Z"));
      project.setSubmissionDetail(acceptedSubmissionDetail);

      var summarySectionOptional = projectDetailsSummaryService.getSummarySection(project, USER);

      assertThat(summarySectionOptional)
          .isPresent()
          .get()
          .extracting(
              SummarySection::summaryText,
              SummarySection::displayOrder
          ).containsExactly(
              "",
              ProjectDetailsSummaryService.DISPLAY_ORDER
          );

      var summaryItems = summarySectionOptional.get().summaryItems();

      assertThat(summaryItems)
          .hasSize(1)
          .extracting(SummaryItem::displayName)
          .contains(ProjectDetailsSummaryService.PROJECT_DETAILS);

      var summaryItem = summaryItems.get(0);
      assertThat(summaryItem.displayName()).isEqualTo(ProjectDetailsSummaryService.PROJECT_DETAILS);
      assertThat(summaryItem.summaryCards()).hasSize(1);

      var summaryCard = summaryItem.summaryCards().get(0);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectDetailsSummaryService.PROJECT_DETAILS,
              SummaryCardType.SIMPLE_SUMMARY
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryDataView.class);

      var expectedKeyValues = List.of(
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_NAME, project.getProjectName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_TYPE, project.getProjectType().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PHASE, project.getProjectPhase().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PIPELINE, "Yes"),
          new SummaryKeyValue(ProjectDetailsSummaryService.ASSOCIATED_SUBMISSION,
              "Concept select report accepted on 1 December 2023")
      );

            assertThat(((SummaryDataView) summaryData).keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }

    @Test
    void whenGetProjectSummaryCard_thenEmptyActionMap() {
      var summaryCard = projectDetailsSummaryService.getProjectSummaryCard(project, USER);
      assertThat(summaryCard.summaryActions())
          .isEmpty();
    }
  }

  @Nested
  class GivenUserIsProjectAnalyst {
    private static final boolean IS_PROJECT_ANALYST = true;

    @BeforeEach
    void setUp() {
      mockUserCallsWithPermissions(IS_PROJECT_ANALYST);
    }

    @Test
    void whenGetSummarySection_thenPopulatedActionMap() {
      var expectedActions = List.of(
          new SummaryAction(
          "Change",
          ReverseRouter.route(on(ProjectDetailsController.class)
              .getUpdateProject(project.getField().getResource().getId(), project.getId())),
              ""
          )
      );

      assertThat(invokeGetSummarySectionAndReturnSummaryCard().summaryActions())
          .usingRecursiveComparison()
          .isEqualTo(expectedActions);
    }
  }

  @Nested
  class GivenUserIsNotProjectAnalyst {
    private static final boolean IS_PROJECT_ANALYST = false;

    @BeforeEach
    void setUp() {
      mockUserCallsWithPermissions(IS_PROJECT_ANALYST);
    }

    @Test
    void whenGetSummarySection_thenEmptyActionMap() {
      var expectedActions = List.of();
      assertThat(invokeGetSummarySectionAndReturnSummaryCard().summaryActions())
          .usingRecursiveComparison()
          .isEqualTo(expectedActions);
    }
  }

  private void mockUserCallsWithPermissions(boolean isProjectAnalyst) {
    var projectAnalystUser = UserTestUtil.Builder().build();
    when(teamQueryService.userHasStaticRole(projectAnalystUser.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
        .thenReturn(isProjectAnalyst);
  }

  private SummaryCard invokeGetSummarySectionAndReturnSummaryCard() {
    var summarySectionOptional = projectDetailsSummaryService.getSummarySection(project, USER);

    var summaryItems = summarySectionOptional.get().summaryItems();

    var summaryItem = summaryItems.get(0);

    return summaryItem.summaryCards().get(0);
  }
}