package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class ProjectDetailsSummaryService implements SummarySectionService<Project> {

  static final int DISPLAY_ORDER = 10;
  static final String PROJECT_DETAILS = "Project details";

  static final String PROJECT_NAME = "Project name";
  static final String PROJECT_TYPE = "Project type";
  static final String PROJECT_STATUS = "Project status";
  static final String PROJECT_PIPELINE = "Part of project pipeline";
  static final String ASSOCIATED_SUBMISSION = "Associated submission";
  public static final String NO_ASSOCIATED_SUBMISSION = "No associated submission";

  @Override
  public Optional<SummarySection> getSummarySection(Project project) {
    var summaryItems = List.of(SummaryItem.withCard(PROJECT_DETAILS, getProjectDetailsSummaryCard(project)));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryCard getProjectDetailsSummaryCard(Project project) {

    var summaryData = new SummaryDataView(new ArrayList<>());

    summaryData.addKeyValue(PROJECT_NAME, project.getProjectName());
    summaryData.addKeyValue(PROJECT_TYPE, project.getProjectType().getDisplayName());
    summaryData.addKeyValue(PROJECT_STATUS, project.getProjectStatus().getDisplayName());
    summaryData.addKeyValue(PROJECT_PIPELINE, BooleanUtil.yesNoFromBoolean(project.getPartOfPipeline()));

    var submissionDetail = project.getSubmissionDetail();
    if (submissionDetail != null) {
      var associatedSubmissionText = "%s accepted on %s"
          .formatted(
              StringUtils.capitalize(submissionDetail.getSubmissionType().getTypeName().toLowerCase()),
              DateUtil.formatLongDate(submissionDetail.getAcceptedTimestamp())
          );
      summaryData.addKeyValue(ASSOCIATED_SUBMISSION, associatedSubmissionText);
    } else {
      summaryData.addKeyValue(ASSOCIATED_SUBMISSION, NO_ASSOCIATED_SUBMISSION);
    }

    return SummaryCard.simpleSummaryCard(summaryData);
  }
}
