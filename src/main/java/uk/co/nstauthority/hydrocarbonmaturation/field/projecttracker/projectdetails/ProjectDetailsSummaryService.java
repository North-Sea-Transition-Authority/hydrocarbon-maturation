package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class ProjectDetailsSummaryService implements SummarySectionService<Project> {

  static final int DISPLAY_ORDER = 10;
  static final String PROJECT_DETAILS = "Project details";

  static final String PROJECT_NAME = "Project name";
  static final String PROJECT_TYPE = "Project type";
  static final String PROJECT_PHASE = "Project status";
  static final String PROJECT_PIPELINE = "Part of project pipeline";
  static final String ASSOCIATED_SUBMISSION = "Associated submission";
  static final String NO_ASSOCIATED_SUBMISSION = "No associated submission";
  static final String FDPA_START_UP_DATE = "FDPA start up date";

  private final TeamQueryService teamQueryService;

  @Autowired
  public ProjectDetailsSummaryService(TeamQueryService teamQueryService) {
    this.teamQueryService = teamQueryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(Project project, User user) {
    var includeActions = true;
    var summaryItems = List.of(
        SummaryItem.withCard(
            PROJECT_DETAILS,
            getSummaryCard(project, includeActions, user))
    );

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryCard getSummaryCard(Project project, boolean includeCardActions, User user) {
    var actions = new ArrayList<SummaryAction>();
    if (includeCardActions && userCanViewActions(user)) {
      actions.add(
          new SummaryAction(
              "Change",
              ReverseRouter.route(on(ProjectDetailsController.class)
                  .getUpdateProject(project.getField().getResource().getId(), project.getId())),
              ""
          ));
    }

    var summaryData = new SummaryDataView(new ArrayList<>());

    summaryData.addKeyValue(PROJECT_NAME, project.getProjectName());
    summaryData.addKeyValue(PROJECT_TYPE, project.getProjectType().getDisplayName());

    if (ProjectType.FIELD_DEVELOPMENT_PLAN.equals(project.getProjectType())
        || ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM.equals(project.getProjectType())) {
      summaryData.addKeyValue(PROJECT_PHASE, project.getProjectPhase());
    }

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

    if (project.getFdpaStartUpDate() != null) {
      summaryData.addKeyValue(FDPA_START_UP_DATE, DateUtil.formatLongDate(project.getFdpaStartUpDate()));
    }

    return new SummaryCard(
        PROJECT_DETAILS,
        SummaryCardType.SIMPLE_SUMMARY,
        summaryData,
        actions,
        List.of()
    );
  }

  /** Return a summary card that never has any actions for the project.
   * @param project the project to create a summary for
   * @return a summary card with the project details with no actions
   */
  public SummaryCard getProjectSummaryCard(Project project, User user) {
    var includeActions = false;
    return getSummaryCard(project, includeActions, user);
  }

  private boolean userCanViewActions(User user) {
    return teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST);
  }

}
