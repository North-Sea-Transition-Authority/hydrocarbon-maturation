package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.MetricsProvider;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;

@Controller
@RequestMapping("/work-area")
@AccessibleByServiceUsers
public class WorkAreaController {

  private final WorkAreaItemService workAreaItemService;
  private final MetricsProvider metricsProvider;
  private final TeamQueryService teamQueryService;

  public static final String WORK_AREA_TITLE = "Work area";

  @Autowired
  public WorkAreaController(WorkAreaItemService workAreaItemService,
                            MetricsProvider metricsProvider,
                            TeamQueryService teamQueryService) {
    this.workAreaItemService = workAreaItemService;
    this.metricsProvider = metricsProvider;
    this.teamQueryService = teamQueryService;
  }

  @GetMapping
  public ModelAndView getWorkArea(User user) {
    var checkStopWatch = Stopwatch.createStarted();

    var workAreaItems = workAreaItemService.getWorkAreaItems(user);

    var elapsedMs = checkStopWatch.elapsed(TimeUnit.MILLISECONDS);
    metricsProvider.getWorkAreaRequestTimer().record(elapsedMs, TimeUnit.MILLISECONDS);
    var userRoles = teamQueryService.getTeamRolesForUser(user.wuaId()).stream()
        .map(TeamRole::getRole)
        .collect(Collectors.toSet());
    var rolesThatCanSubmit = SubmissionType.getRolesThatCanSubmit();
    var canCreateSubmissions = rolesThatCanSubmit.stream().anyMatch(userRoles::contains);
    return new ModelAndView("remi/workarea/workArea")
        .addObject("startSubmissionUrl",
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionType(null)))
        .addObject("pageTitle", WORK_AREA_TITLE)
        .addObject("workAreaItems", workAreaItems)
        .addObject("canCreateSubmissions", canCreateSubmissions);
  }
}