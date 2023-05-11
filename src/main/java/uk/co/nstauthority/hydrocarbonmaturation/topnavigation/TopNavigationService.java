package uk.co.nstauthority.hydrocarbonmaturation.topnavigation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController.WORK_AREA_TITLE;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.fds.navigation.TopNavigationItem;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchResourceRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Service
public class TopNavigationService {

  public static final String TEAM_MANAGEMENT_NAVIGATION_ITEM_TITLE = "Manage teams";
  public static final String SEARCH_RESOURCE_RECORDS = "Search resource records";

  public List<TopNavigationItem> getTopNavigationItems() {
    var navigationItems = new ArrayList<TopNavigationItem>();
    navigationItems.add(
        new TopNavigationItem(WORK_AREA_TITLE, ReverseRouter.route(on(WorkAreaController.class).getWorkArea()))
    );

    navigationItems.add(
        new TopNavigationItem(TEAM_MANAGEMENT_NAVIGATION_ITEM_TITLE,
            ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()))
    );

    navigationItems.add(
        new TopNavigationItem(SEARCH_RESOURCE_RECORDS,
            ReverseRouter.route(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen(null)))
    );

    return navigationItems;
  }
}
