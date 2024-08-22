package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
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
import uk.co.nstauthority.hydrocarbonmaturation.util.StringUtil;

@Service
class ProjectCapexSummaryService implements SummarySectionService<Project> {
  static final int DISPLAY_ORDER = 20;
  static final String PROJECT_CAPEX = "Project CAPEX";
  static final Set<ProjectType> TYPES_FOR_SECTION = Set.of(
      ProjectType.FIELD_DEVELOPMENT_PLAN,
      ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM);

  private final ProjectCapexService projectCapexService;
  private final TeamQueryService teamQueryService;

  @Autowired
  ProjectCapexSummaryService(ProjectCapexService projectCapexService,
                             TeamQueryService teamQueryService) {
    this.projectCapexService = projectCapexService;
    this.teamQueryService = teamQueryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(Project project, User user) {
    if (!TYPES_FOR_SECTION.contains(project.getProjectType())) {
      return Optional.empty();
    }

    var summaryItems = List.of(SummaryItem.withCard(PROJECT_CAPEX, getProjectCapexSummaryCard(project, user)));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryCard getProjectCapexSummaryCard(Project project, User user) {
    var timelineEvents = projectCapexService.getOrderedCreatedProjectCapexs(project).stream()
        .map(t -> toTimelineEvent(t, user))
        .toList();

    var actions = getActionsForUser(project, user);

    if (timelineEvents.isEmpty()) {
      return SummaryCard.emptySummaryCardWithActions(PROJECT_CAPEX, actions);
    }

    return new SummaryCard(
        PROJECT_CAPEX,
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
            ReverseRouter.route(on(ProjectCapexController.class)
                .getAddProjectCapex(project.getField().getResource().getId(), project.getId())),
            ""
        )
    );
  }

  private TimelineEvent toTimelineEvent(ProjectCapex projectCapex, User user) {
    var totalCapex = StringUtil.formatMoneyInMillions(projectCapex.getTotalCapex());

    var date = DateUtil.formatLongDate(projectCapex.getDateProvided());
    var caption = "%s - provided %s".formatted(projectCapex.getSourceOfInformation().getDisplayName(), date);

    var resourceId = projectCapex.getProject().getField().getResource().getId();
    var projectId = projectCapex.getProject().getId();
    var removeUrl = ReverseRouter.route(on(ProjectCapexController.class)
        .removeProjectCapex(resourceId, projectId, projectCapex.getId(), null));

    var timelineActionsMap = new HashMap<String, String>();
    if (teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST)) {
      timelineActionsMap.put("Remove", removeUrl);
    }

    return new TimelineEvent(totalCapex, caption, timelineActionsMap);
  }
}
