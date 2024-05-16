package uk.co.nstauthority.hydrocarbonmaturation.topnavigation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController.WORK_AREA_TITLE;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.fds.navigation.TopNavigationItem;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.LegacyTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SystemOfRecordLandingPageController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveriesOverviewController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Service
public class TopNavigationService {

  public static final String TEAMS = "Teams";
  public static final String LEGACY_TEAMS = "Legacy teams";
  public static final String SEARCH_RESOURCE_RECORDS = "Search resource records";
  public static final String UNDEVELOPED_DISCOVERIES = "Undeveloped discoveries";
  private final InternalTeamService internalTeamService;

  @Autowired
  public TopNavigationService(InternalTeamService internalTeamService) {
    this.internalTeamService = internalTeamService;
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
        new TopNavigationItem(LEGACY_TEAMS,
            ReverseRouter.route(on(LegacyTeamManagementController.class).renderTeamListOrRedirect()))
    );

    navigationItems.add(
        new TopNavigationItem(SEARCH_RESOURCE_RECORDS,
            ReverseRouter.route(on(SystemOfRecordLandingPageController.class).getLandingPage()))
    );

    if (Boolean.TRUE.equals(internalTeamService.isRegulatorOrAreaTeamUser())) {
      navigationItems.add(
          new TopNavigationItem(UNDEVELOPED_DISCOVERIES,
              ReverseRouter.route(on(UndevelopedDiscoveriesOverviewController.class)
                  .viewAllUndevelopedDiscoveries(null))
          )
      );
    }

    return navigationItems;
  }
}
