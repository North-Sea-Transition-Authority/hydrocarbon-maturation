package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamManagementController;

public class ManagementRouteUtil {

  private ManagementRouteUtil() {
  }

  public static String getRouteForTeamType(TeamType teamType) {
    return switch (teamType) {
      case REGULATOR -> ReverseRouter.route(on(RegulatorTeamManagementController.class).renderMemberListRedirect());
      case INDUSTRY -> ReverseRouter.route(on(IndustryTeamManagementController.class).renderTeamList());
    };
  }

  public static String getRouteForTeamIdAndTeamType(TeamType teamType, TeamId teamId) {
    return switch (teamType) {
      case REGULATOR -> ReverseRouter.route(on(
          RegulatorTeamManagementController.class).renderMemberList(teamId));
      case INDUSTRY -> ReverseRouter.route(on(
          IndustryTeamManagementController.class).renderMemberList(teamId));
    };
  }
}
