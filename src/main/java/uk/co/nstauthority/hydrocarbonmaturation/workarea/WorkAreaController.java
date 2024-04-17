package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.google.common.base.Stopwatch;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.MetricsProvider;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/work-area")
@AccessibleByServiceUsers
public class WorkAreaController {

  private final WorkAreaItemService workAreaItemService;
  private final PermissionService permissionService;
  private final MetricsProvider metricsProvider;

  public static final String WORK_AREA_TITLE = "Work area";

  @Autowired
  public WorkAreaController(WorkAreaItemService workAreaItemService,
                            PermissionService permissionService, MetricsProvider metricsProvider) {
    this.workAreaItemService = workAreaItemService;
    this.permissionService = permissionService;
    this.metricsProvider = metricsProvider;
  }

  @GetMapping
  public ModelAndView getWorkArea(User user) {
    var checkStopWatch = Stopwatch.createStarted();

    var workAreaItems = workAreaItemService.getWorkAreaItems(user);

    var elapsedMs = checkStopWatch.elapsed(TimeUnit.MILLISECONDS);
    metricsProvider.getWorkAreaRequestTimer().record(elapsedMs, TimeUnit.MILLISECONDS);

    return new ModelAndView("remi/workarea/workArea")
        .addObject("startSubmissionUrl",
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionType()))
        .addObject("pageTitle", WORK_AREA_TITLE)
        .addObject("workAreaItems", workAreaItems)
        .addObject("canCreateSubmissions",
            permissionService.hasPermission(user, Set.of(
                RolePermission.EXPLORATION,
                RolePermission.INDUSTRY_SUBMIT,
                RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
                RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
                RolePermission.ONSHORE_AREA_TEAM_MEMBER)));
  }
}