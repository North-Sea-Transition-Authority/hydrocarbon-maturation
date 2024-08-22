package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTimelineView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TimelineEvent;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
class ProjectStageSummaryService implements SummarySectionService<Project> {
  static final int DISPLAY_ORDER = 20;
  static final String PROJECT_STAGE = "Project stage";

  private final ProjectStageService projectStageService;
  private final TeamQueryService teamQueryService;

  @Autowired
  ProjectStageSummaryService(ProjectStageService projectStageService,
                             TeamQueryService teamQueryService) {
    this.projectStageService = projectStageService;
    this.teamQueryService = teamQueryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(Project project, User user) {
    var summaryItems = List.of(SummaryItem.withCard(PROJECT_STAGE, getProjectStageSummaryCard(project, user)));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  SummaryCard getProjectStageSummaryCard(Project project, User user) {
    var timelineEvents = projectStageService.getOrderedProjectStages(project).stream()
        .map(t -> toTimelineEvent(t, user))
        .toList();

    List<SummaryAction> actions = getActionsForUser(project, user);

    if (timelineEvents.isEmpty()) {
      return SummaryCard.emptySummaryCardWithActions(PROJECT_STAGE, actions);
    }

    return new SummaryCard(
        PROJECT_STAGE,
        SummaryCardType.TIMELINE_SUMMARY,
        new SummaryTimelineView(timelineEvents),
        actions,
        List.of()
    );
  }

  private List<SummaryAction> getActionsForUser(Project project, User user) {
    if (!teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)) {
      return List.of();
    }
    return List.of(
        new SummaryAction(
            "Add",
            ReverseRouter.route(on(ProjectStageController.class)
                .getCreateProjectStageUpdate(project.getField().getResource().getId(), project.getId())),
            ""
        )
    );
  }

  private TimelineEvent toTimelineEvent(ProjectStageHistory projectStageHistory, User user) {
    var stage = projectStageHistory.getStage().getDisplayName();

    var date = DateUtil.formatLongDate(projectStageHistory.getStageDate());

    var project = projectStageHistory.getProject();
    var removeUrl = ReverseRouter.route(on(ProjectStageController.class)
        .getRemoveProjectStageConfirmation(
            project.getField().getResource().getId(),
            project.getId(),
            projectStageHistory.getId())
    );

    var timelineActionsMap = new HashMap<String, String>();

    if (teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)) {
      timelineActionsMap.put("Remove", removeUrl);
    }

    return new TimelineEvent(stage, date, timelineActionsMap);
  }
}
