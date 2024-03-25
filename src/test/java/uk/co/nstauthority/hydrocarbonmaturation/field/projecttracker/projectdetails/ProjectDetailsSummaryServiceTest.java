package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectPhase;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataViewWithActions;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ExtendWith(MockitoExtension.class)
class ProjectDetailsSummaryServiceTest {
  private final Instant FIXED_INSTANT = Instant.parse("2024-02-01T00:00:00.00Z");

  @Mock
  private UserService userService;
  @Mock
  private PermissionService permissionService;
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
    void whenGetSummarySection_forNonFdpa_thenAssertCommonProperties() {
      var acceptedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      acceptedSubmissionDetail.setAcceptedTimestamp(Instant.parse("2023-12-01T00:00:00.00Z"));
      project.setSubmissionDetail(acceptedSubmissionDetail);

      var summarySectionOptional = projectDetailsSummaryService.getSummarySection(project);

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
              SummaryCardType.SIMPLE_SUMMARY_WITH_ACTIONS
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryDataViewWithActions.class);

      var expectedKeyValues = List.of(
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_NAME, project.getProjectName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_TYPE, project.getProjectType().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PHASE, project.getProjectPhase().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PIPELINE, "Yes"),
          new SummaryKeyValue(ProjectDetailsSummaryService.ASSOCIATED_SUBMISSION,
              "Concept select report accepted on 1 December 2023")
      );

      var summaryDataWithActions = (SummaryDataViewWithActions) summaryData;
      assertThat(summaryDataWithActions.keyValues())
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

      var summarySectionOptional = projectDetailsSummaryService.getSummarySection(project);

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
              SummaryCardType.SIMPLE_SUMMARY_WITH_ACTIONS
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryDataViewWithActions.class);

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

      var summaryDataWithActions = (SummaryDataViewWithActions) summaryData;
      assertThat(summaryDataWithActions.keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }

    @Test
    void whenGetProjectSummaryCard_forNonFdpa_thenAssertCommonProperties() {
      var acceptedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      acceptedSubmissionDetail.setAcceptedTimestamp(Instant.parse("2023-12-01T00:00:00.00Z"));
      project.setSubmissionDetail(acceptedSubmissionDetail);

      var summaryCard = projectDetailsSummaryService.getProjectSummaryCard(project);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectDetailsSummaryService.PROJECT_DETAILS,
              SummaryCardType.SIMPLE_SUMMARY_WITH_ACTIONS
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryDataViewWithActions.class);

      var expectedKeyValues = List.of(
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_NAME, project.getProjectName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_TYPE, project.getProjectType().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PHASE, project.getProjectPhase().getDisplayName()),
          new SummaryKeyValue(ProjectDetailsSummaryService.PROJECT_PIPELINE, "Yes"),
          new SummaryKeyValue(ProjectDetailsSummaryService.ASSOCIATED_SUBMISSION,
              "Concept select report accepted on 1 December 2023")
      );

      var summaryDataWithActions = (SummaryDataViewWithActions) summaryData;
      assertThat(summaryDataWithActions.keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }

    @Test
    void whenGetProjectSummaryCard_forFdpa_thenAssertCommonProperties() {
      var acceptedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      acceptedSubmissionDetail.setAcceptedTimestamp(Instant.parse("2023-12-01T00:00:00.00Z"));
      project.setSubmissionDetail(acceptedSubmissionDetail);

      var fdpaStartUpDate = LocalDate.of(2024, Month.MARCH, 5);
      project.setFdpaStartUpDate(fdpaStartUpDate);

      var summaryCard = projectDetailsSummaryService.getProjectSummaryCard(project);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              ProjectDetailsSummaryService.PROJECT_DETAILS,
              SummaryCardType.SIMPLE_SUMMARY_WITH_ACTIONS
          );

      var summaryData = summaryCard.summaryData();
      assertThat(summaryData)
          .isInstanceOf(SummaryDataViewWithActions.class);

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

      var summaryDataWithActions = (SummaryDataViewWithActions) summaryData;
      assertThat(summaryDataWithActions.keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }
  }

  @Nested
  class GivenUserIsProjectAnalyst {
    private static final boolean IS_PROJECT_ANALYST = true;

    private SummaryDataViewWithActions summaryDataViewWithActions;

    @BeforeEach
    void setUp() {
      mockUserCallsWithPermissions(IS_PROJECT_ANALYST);
      summaryDataViewWithActions = getSummaryDataViewWithActions();
    }

    @Test
    void whenGetSummarySection_thenPopulatedActionMap() {
      var expectedActionsMap = Map.of(
          "Change",
          ReverseRouter.route(on(ProjectDetailsController.class)
              .getUpdateProject(project.getField().getResource().getId(), project.getId()))
      );

      assertThat(summaryDataViewWithActions.actionsMap())
          .usingRecursiveComparison()
          .isEqualTo(expectedActionsMap);
    }

    @Test
    void whenGetProjectSummaryCard_thenEmptyActionMap() {
      var summaryCard = projectDetailsSummaryService.getProjectSummaryCard(project);

      var summaryData = (SummaryDataViewWithActions) summaryCard.summaryData();
      assertThat(summaryData.actionsMap())
          .isEmpty();
    }
  }

  @Nested
  class GivenUserIsNotProjectAnalyst {
    private static final boolean IS_PROJECT_ANALYST = false;

    private SummaryDataViewWithActions summaryDataViewWithActions;

    @BeforeEach
    void setUp() {
      mockUserCallsWithPermissions(IS_PROJECT_ANALYST);
      summaryDataViewWithActions = getSummaryDataViewWithActions();
    }

    @Test
    void whenGetSummarySection_thenEmptyActionMap() {

      var expectedActionsMap = Map.of();
      assertThat(summaryDataViewWithActions.actionsMap())
          .usingRecursiveComparison()
          .isEqualTo(expectedActionsMap);
    }

    @Test
    void whenGetProjectSummaryCard_thenEmptyActionMap() {
      var summaryCard = projectDetailsSummaryService.getProjectSummaryCard(project);

      var summaryData = (SummaryDataViewWithActions) summaryCard.summaryData();
      assertThat(summaryData.actionsMap())
          .isEmpty();
    }
  }

  private void mockUserCallsWithPermissions(boolean isProjectAnalyst) {
    var projectAnalystUser = UserTestUtil.Builder().build();
    when(userService.getUser())
        .thenReturn(projectAnalystUser);
    when(permissionService.hasPermission(projectAnalystUser, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(isProjectAnalyst);
  }

  private SummaryDataViewWithActions getSummaryDataViewWithActions() {
    var summarySectionOptional = projectDetailsSummaryService.getSummarySection(project);

    var summaryItems = summarySectionOptional.get().summaryItems();

    var summaryItem = summaryItems.get(0);

    var summaryCard = summaryItem.summaryCards().get(0);

    var summaryData = summaryCard.summaryData();

    return (SummaryDataViewWithActions) summaryData;
  }
}