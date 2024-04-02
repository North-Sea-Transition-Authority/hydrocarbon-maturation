package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataViewWithActions;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ExtendWith(MockitoExtension.class)
class EnvironmentalActivitySummaryServiceTest {
  @Mock
  private EnvironmentalActivityService environmentalActivityService;
  @Mock
  private UserService userService;
  @Mock
  private PermissionService permissionService;
  @InjectMocks
  private EnvironmentalActivitySummaryService environmentalActivitySummaryService;

  private Project project;
  private User user;

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(8000), UUID.randomUUID());
  }

  @Nested
  class GivenProjectTypeNotForSection {
    @ParameterizedTest
    @EnumSource(
        value = ProjectType.class,
        names = {"FIELD_DEVELOPMENT_PLAN", "FIELD_DEVELOPMENT_PLAN_ADDENDUM"},
        mode = EnumSource.Mode.EXCLUDE
    )
    void getSummarySection_typeNotForSection_thenEmpty(ProjectType projectType) {
      project.setProjectType(projectType);

      var summarySectionOptional = environmentalActivitySummaryService.getSummarySection(project);
      assertThat(summarySectionOptional).isEmpty();
    }
  }

  @Nested
  class GivenProjectTypeForSection {
    @BeforeEach
    void setUp() {
      project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);

      user = UserTestUtil.Builder().build();
      when(userService.getUser()).thenReturn(user);
    }

    @ParameterizedTest
    @EnumSource(
        value = ProjectType.class,
        names = {"FIELD_DEVELOPMENT_PLAN", "FIELD_DEVELOPMENT_PLAN_ADDENDUM"},
        mode = EnumSource.Mode.INCLUDE
    )
    void getSummarySection_forAllTypes_assertStructure(ProjectType projectType) {
      project.setProjectType(projectType);

      when(environmentalActivityService.getEnvironmentalActivity(project))
          .thenReturn(Optional.empty());

      var summarySectionOptional = environmentalActivitySummaryService.getSummarySection(project);

      assertThat(summarySectionOptional)
          .isPresent()
          .get()
          .extracting(
              SummarySection::summaryText,
              SummarySection::displayOrder
          ).containsExactly(
              "",
              EnvironmentalActivitySummaryService.DISPLAY_ORDER
          );

      var summaryItems = summarySectionOptional.get().summaryItems();

      assertThat(summaryItems)
          .hasSize(1)
          .extracting(SummaryItem::displayName)
          .contains(EnvironmentalActivitySummaryService.ENVIRONMENTAL_ACTIVITY);

      var summaryItem = summaryItems.get(0);
      assertThat(summaryItem.displayName()).isEqualTo(EnvironmentalActivitySummaryService.ENVIRONMENTAL_ACTIVITY);
      assertThat(summaryItem.summaryCards()).hasSize(1);

      var summaryCard = summaryItem.summaryCards().get(0);

      assertThat(summaryCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .contains(
              EnvironmentalActivitySummaryService.ENVIRONMENTAL_ACTIVITY,
              SummaryCardType.SIMPLE_SUMMARY_WITH_ACTIONS
          );
    }

    @Test
    void getSummarySection_emptyDates_thenEmptyStrings() {
      when(environmentalActivityService.getEnvironmentalActivity(project))
          .thenReturn(Optional.empty());

      var summarySectionOptional = environmentalActivitySummaryService.getSummarySection(project);
      var summaryItems = summarySectionOptional.get().summaryItems();
      var summaryItem = summaryItems.get(0);
      var summaryCard = summaryItem.summaryCards().get(0);
      var summaryData = summaryCard.summaryData();

      var expectedKeyValues = List.of(
          new SummaryKeyValue(EnvironmentalActivitySummaryService.SUBMISSION_DATE, ""),
          new SummaryKeyValue(EnvironmentalActivitySummaryService.CONSULTATION_START_DATE, ""),
          new SummaryKeyValue(EnvironmentalActivitySummaryService.CONSULTATION_CLOSING_DATE, "")
      );

      var summaryDataWithActions = (SummaryDataViewWithActions) summaryData;
      assertThat(summaryDataWithActions.keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }

    @Test
    void getSummarySection_populatedDates_thenFormattedDates() {
      var activity = new EnvironmentalActivity();
      final var FIXED_DATE = LocalDate.of(2020, 3, 27);
      activity.setSubmissionDate(FIXED_DATE);
      activity.setConsultationStartDate(FIXED_DATE.plusMonths(1));
      activity.setConsultationClosingDate(FIXED_DATE.plusYears(1));

      when(environmentalActivityService.getEnvironmentalActivity(project))
          .thenReturn(Optional.of(activity));

      var summarySectionOptional = environmentalActivitySummaryService.getSummarySection(project);
      var summaryItems = summarySectionOptional.get().summaryItems();
      var summaryItem = summaryItems.get(0);
      var summaryCard = summaryItem.summaryCards().get(0);
      var summaryData = summaryCard.summaryData();

      var expectedKeyValues = List.of(
          new SummaryKeyValue(EnvironmentalActivitySummaryService.SUBMISSION_DATE,
              "27 March 2020"),
          new SummaryKeyValue(EnvironmentalActivitySummaryService.CONSULTATION_START_DATE,
              "27 April 2020"),
          new SummaryKeyValue(EnvironmentalActivitySummaryService.CONSULTATION_CLOSING_DATE,
              "27 March 2021")
      );

      var summaryDataWithActions = (SummaryDataViewWithActions) summaryData;
      assertThat(summaryDataWithActions.keyValues())
          .usingRecursiveComparison()
          .isEqualTo(expectedKeyValues);
    }

    @Test
    void getSummarySection_isProjectAnalyst_thenHasActions() {
      when(environmentalActivityService.getEnvironmentalActivity(project))
          .thenReturn(Optional.empty());
      when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);

      var summarySectionOptional = environmentalActivitySummaryService.getSummarySection(project);
      var summaryItems = summarySectionOptional.get().summaryItems();
      var summaryItem = summaryItems.get(0);
      var summaryCard = summaryItem.summaryCards().get(0);

      var expectedActionsMap = Map.of(
          "Change",
          ReverseRouter.route(on(EnvironmentalActivityController.class)
              .getEnvironmentalActivity(project.getField().getResource().getId(), project.getId()))

      );

      assertThat(summaryCard.actionsMap())
          .usingRecursiveComparison()
          .isEqualTo(expectedActionsMap);
    }

    @Test
    void getSummarySection_isNotProjectAnalyst_thenHasNoActions() {
      when(environmentalActivityService.getEnvironmentalActivity(project))
          .thenReturn(Optional.empty());
      when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(false);

      var summarySectionOptional = environmentalActivitySummaryService.getSummarySection(project);
      var summaryItems = summarySectionOptional.get().summaryItems();
      var summaryItem = summaryItems.get(0);
      var summaryCard = summaryItem.summaryCards().get(0);

      assertThat(summaryCard.actionsMap())
          .isEmpty();
    }
  }
}