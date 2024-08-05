package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
class EnvironmentalActivitySummaryService implements SummarySectionService<Project> {
  static final int DISPLAY_ORDER = 40;
  static final String ENVIRONMENTAL_ACTIVITY = "Environmental statement dates";

  static final String SUBMISSION_DATE = "Environment statement submission date";
  static final String CONSULTATION_START_DATE = "Environmental statement consultation start date";
  static final String CONSULTATION_CLOSING_DATE = "Environmental statement consultation closing date";

  static final Set<ProjectType> PROJECT_TYPES_FOR_SECTION =
      EnumSet.of(ProjectType.FIELD_DEVELOPMENT_PLAN, ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM);

  private final EnvironmentalActivityService environmentalActivityService;
  private final UserService userService;
  private final PermissionService permissionService;

  @Autowired
  EnvironmentalActivitySummaryService(EnvironmentalActivityService environmentalActivityService,
                                      UserService userService,
                                      PermissionService permissionService) {
    this.environmentalActivityService = environmentalActivityService;
    this.userService = userService;
    this.permissionService = permissionService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(Project project) {
    if (!PROJECT_TYPES_FOR_SECTION.contains(project.getProjectType())) {
      return Optional.empty();
    }

    var summaryItems = List.of(
        SummaryItem.withCard(ENVIRONMENTAL_ACTIVITY, getEnvironmentalActivitySummaryCard(project)));

    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));

  }

  private SummaryCard getEnvironmentalActivitySummaryCard(Project project) {
    var actions = new ArrayList<SummaryAction>();
    var user = userService.getUser();
    if (permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST))) {
      actions.add(
          new SummaryAction(
              "Change",
              ReverseRouter.route(on(EnvironmentalActivityController.class)
                  .getEnvironmentalActivity(project.getField().getResource().getId(), project.getId())),
              ""
          ));
    }

    var summaryDataView = new SummaryDataView(new ArrayList<>());

    var activity = environmentalActivityService.getEnvironmentalActivity(project)
        .orElse(new EnvironmentalActivity());

    summaryDataView.addKeyValue(
        SUBMISSION_DATE,
        ObjectUtils.defaultIfNull(activity.getSubmissionDate(), "")
    );
    summaryDataView.addKeyValue(
        CONSULTATION_START_DATE,
        ObjectUtils.defaultIfNull(activity.getConsultationStartDate(), "")
    );
    summaryDataView.addKeyValue(
        CONSULTATION_CLOSING_DATE,
        ObjectUtils.defaultIfNull(activity.getConsultationClosingDate(), "")
    );

    return new SummaryCard(ENVIRONMENTAL_ACTIVITY, SummaryCardType.SIMPLE_SUMMARY, summaryDataView, actions, List.of());
  }
}
