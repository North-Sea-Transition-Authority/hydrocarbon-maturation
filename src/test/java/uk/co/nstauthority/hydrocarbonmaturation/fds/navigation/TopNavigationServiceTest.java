package uk.co.nstauthority.hydrocarbonmaturation.fds.navigation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchResourceRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.topnavigation.TopNavigationService;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ExtendWith(MockitoExtension.class)
class TopNavigationServiceTest {

  @InjectMocks
  private TopNavigationService topNavigationService;

  @Test
  void getTopNavigationItems_verifyAllTopNavigationItems() {
    var topNavigationItems = topNavigationService.getTopNavigationItems();

    assertThat(topNavigationItems)
        .extracting(
            TopNavigationItem::getDisplayName,
            TopNavigationItem::getUrl
        )
        .containsExactly(
            Tuple.tuple(
                WorkAreaController.WORK_AREA_TITLE,
                StringUtils.stripEnd(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), "/")
            ),
            Tuple.tuple(
                TopNavigationService.TEAM_MANAGEMENT_NAVIGATION_ITEM_TITLE,
                StringUtils.stripEnd(
                    ReverseRouter.route(on(TeamManagementController.class).renderTeamListOrRedirect()), "/")
            ),
            Tuple.tuple(
                TopNavigationService.SEARCH_RESOURCE_RECORDS,
                StringUtils.stripEnd(
                    ReverseRouter.route(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen()), "/")
            )
        );
  }
}