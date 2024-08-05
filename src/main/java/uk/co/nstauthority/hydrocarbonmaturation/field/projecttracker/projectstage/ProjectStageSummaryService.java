package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTimelineView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.TimelineEvent;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
class ProjectStageSummaryService implements SummarySectionService<Project> {
  static final int DISPLAY_ORDER = 20;
  static final String PROJECT_STAGE = "Project stage";

  private final ProjectStageService projectStageService;
  private final UserService userService;
  private final PermissionService permissionService;

  @Autowired
  ProjectStageSummaryService(ProjectStageService projectStageService,
                             UserService userService,
                             PermissionService permissionService) {
    this.projectStageService = projectStageService;
    this.userService = userService;
    this.permissionService = permissionService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(Project project) {
    var summaryItems = List.of(SummaryItem.withCard(PROJECT_STAGE, getProjectStageSummaryCard(project)));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  SummaryCard getProjectStageSummaryCard(Project project) {
    var timelineEvents = projectStageService.getOrderedProjectStages(project).stream()
        .map(this::toTimelineEvent)
        .toList();

    var user = userService.getUser();
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
    if (!permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST))) {
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

  private TimelineEvent toTimelineEvent(ProjectStageHistory projectStageHistory) {
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

    var user = userService.getUser();
    if (permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST))) {
      timelineActionsMap.put("Remove", removeUrl);
    }

    return new TimelineEvent(stage, date, timelineActionsMap);
  }
}
