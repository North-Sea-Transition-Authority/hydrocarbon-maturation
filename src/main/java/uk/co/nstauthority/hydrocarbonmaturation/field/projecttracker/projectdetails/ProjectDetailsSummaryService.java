package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class ProjectDetailsSummaryService implements SummarySectionService<Project> {

  static final int DISPLAY_ORDER = 10;
  static final String PROJECT_DETAILS = "Project details";

  static final String PROJECT_NAME = "Project name";
  static final String PROJECT_TYPE = "Project type";
  static final String PROJECT_PIPELINE = "Part of project pipeline";

  @Override
  public Optional<SummarySection> getSummarySection(Project project) {
    var summaryItems = List.of(SummaryItem.withCard(PROJECT_DETAILS, getProjectDetailsSummaryCard(project)));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryCard getProjectDetailsSummaryCard(Project project) {

    var summaryData = new SummaryDataView(new ArrayList<>());

    summaryData.addKeyValue(PROJECT_NAME, project.getProjectName());
    summaryData.addKeyValue(PROJECT_TYPE, project.getProjectType().getDisplayName());
    summaryData.addKeyValue(PROJECT_PIPELINE, BooleanUtil.yesNoFromBoolean(project.getPartOfPipeline()));

    return SummaryCard.simpleSummaryCard(summaryData);
  }
}
