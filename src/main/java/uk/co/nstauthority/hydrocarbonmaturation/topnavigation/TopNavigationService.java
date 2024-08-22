package uk.co.nstauthority.hydrocarbonmaturation.topnavigation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController.WORK_AREA_TITLE;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.navigation.TopNavigationItem;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SystemOfRecordLandingPageController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveriesOverviewController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Service
public class TopNavigationService {

  public static final String TEAMS = "Teams";
  public static final String SEARCH_RESOURCE_RECORDS = "Search";
  public static final String UNDEVELOPED_DISCOVERIES = "Undeveloped discoveries";
  private final InternalTeamService internalTeamService;
  private final UserService userService;

  @Autowired
  public TopNavigationService(InternalTeamService internalTeamService, UserService userService) {
    this.internalTeamService = internalTeamService;
    this.userService = userService;
  }

  public List<TopNavigationItem> getTopNavigationItems() {
    var navigationItems = new ArrayList<TopNavigationItem>();
    navigationItems.add(
        new TopNavigationItem(WORK_AREA_TITLE, ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
    );

    navigationItems.add(
        new TopNavigationItem(TEAMS,
            ReverseRouter.route(on(TeamManagementController.class).renderTeamTypeList(null)))
    );

    navigationItems.add(
        new TopNavigationItem(SEARCH_RESOURCE_RECORDS,
            ReverseRouter.route(on(SystemOfRecordLandingPageController.class).getLandingPage()))
    );

    var user = userService.getOptionalUserDetail();
    if (user.isPresent() && Boolean.TRUE.equals(internalTeamService.isRegulatorOrAreaTeamUser(user.get()))) {
      navigationItems.add(
          new TopNavigationItem(UNDEVELOPED_DISCOVERIES,
              ReverseRouter.route(on(UndevelopedDiscoveriesOverviewController.class)
                  .viewAllUndevelopedDiscoveries())
          )
      );
    }

    return navigationItems;
  }
}
