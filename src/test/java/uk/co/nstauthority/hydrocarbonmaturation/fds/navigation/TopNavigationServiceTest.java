package uk.co.nstauthority.hydrocarbonmaturation.fds.navigation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.LegacyTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SystemOfRecordLandingPageController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.TeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.topnavigation.TopNavigationService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveriesOverviewController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ExtendWith(MockitoExtension.class)
class TopNavigationServiceTest {

  @Mock
  private InternalTeamService internalTeamService;

  @InjectMocks
  private TopNavigationService topNavigationService;

  @Test
  void getTopNavigationItems_verifyAllTopNavigationItems() {
    when(internalTeamService.isRegulatorOrAreaTeamUser())
        .thenReturn(Boolean.TRUE);

    var topNavigationItems = topNavigationService.getTopNavigationItems();

    assertThat(topNavigationItems)
        .extracting(
            TopNavigationItem::getDisplayName,
            TopNavigationItem::getUrl
        )
        .containsExactly(
            Tuple.tuple(
                WorkAreaController.WORK_AREA_TITLE,
                StringUtils.stripEnd(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), "/")
            ),
            Tuple.tuple(
                TopNavigationService.TEAMS,
                StringUtils.stripEnd(
                    ReverseRouter.route(on(TeamManagementController.class).renderTeamTypeList(null)), "/")
            ),
            Tuple.tuple(
                TopNavigationService.LEGACY_TEAMS,
                StringUtils.stripEnd(
                    ReverseRouter.route(on(LegacyTeamManagementController.class).renderTeamListOrRedirect()), "/")
            ),
            Tuple.tuple(
                TopNavigationService.SEARCH_RESOURCE_RECORDS,
                StringUtils.stripEnd(
                    ReverseRouter.route(on(SystemOfRecordLandingPageController.class)
                        .getLandingPage()), "/")
            ),
            Tuple.tuple(
                TopNavigationService.UNDEVELOPED_DISCOVERIES,
                StringUtils.stripEnd(
                    ReverseRouter.route(on(UndevelopedDiscoveriesOverviewController.class)
                        .viewAllUndevelopedDiscoveries()), "/")
            )
        );
  }

  @Test
  void getTopNavigationItems_notRegulatorOrAreaTeamUser() {
    when(internalTeamService.isRegulatorOrAreaTeamUser())
        .thenReturn(Boolean.FALSE);

    var topNavigationItems = topNavigationService.getTopNavigationItems();

    assertThat(topNavigationItems)
        .extracting(
            TopNavigationItem::getDisplayName,
            TopNavigationItem::getUrl
        )
        .containsExactly(
            Tuple.tuple(
                WorkAreaController.WORK_AREA_TITLE,
                StringUtils.stripEnd(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), "/")
            ),
            Tuple.tuple(
                TopNavigationService.TEAMS,
                StringUtils.stripEnd(
                    ReverseRouter.route(on(TeamManagementController.class).renderTeamTypeList(null)), "/")
            ),
            Tuple.tuple(
                TopNavigationService.LEGACY_TEAMS,
                StringUtils.stripEnd(
                    ReverseRouter.route(on(LegacyTeamManagementController.class).renderTeamListOrRedirect()), "/")
            ),
            Tuple.tuple(
                TopNavigationService.SEARCH_RESOURCE_RECORDS,
                StringUtils.stripEnd(
                    ReverseRouter.route(on(SystemOfRecordLandingPageController.class)
                        .getLandingPage()), "/")
            )
        );
  }
}