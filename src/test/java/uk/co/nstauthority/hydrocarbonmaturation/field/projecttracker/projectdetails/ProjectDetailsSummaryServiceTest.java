package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@ExtendWith(MockitoExtension.class)
class ProjectDetailsSummaryServiceTest {
  private final Instant FIXED_INSTANT = Instant.parse("2024-02-01T00:00:00.00Z");

  private ProjectDetailsSummaryService projectDetailsSummaryService;
  private Project project;

  @BeforeEach
  void setUp() {
    projectDetailsSummaryService = new ProjectDetailsSummaryService();
    project = ProjectTestUtils.createPopulatedProject(
        "Project",
        ProjectType.FIELD_DEVELOPMENT_PLAN,
        ProjectStatus.ASSESSMENT_PHASE,
        true,
        FIXED_INSTANT);
  }

  @Test
  void getSummarySection_assertProperties() {
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

    var summaryData = new SummaryDataView(new ArrayList<>());
    summaryData.addKeyValue(ProjectDetailsSummaryService.PROJECT_NAME,
        project.getProjectName());
    summaryData.addKeyValue(ProjectDetailsSummaryService.PROJECT_TYPE,
        project.getProjectType().getDisplayName());
    summaryData.addKeyValue(ProjectDetailsSummaryService.PROJECT_PIPELINE,
        BooleanUtil.yesNoFromBoolean(project.getPartOfPipeline()));

    var expectedSummaryCard = new SummaryCard(null, SummaryCardType.SIMPLE_SUMMARY, summaryData);
    assertThat(summaryCard)
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryCard);
  }
}