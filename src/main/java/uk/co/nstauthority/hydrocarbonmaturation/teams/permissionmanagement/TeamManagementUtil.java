package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamManagementController;

public class TeamManagementUtil {

  private TeamManagementUtil() {
  }

  public static ModelAndView getRenderMemberListRoute(TeamId teamId, TeamType teamType) {
    return switch (teamType) {
      case REGULATOR -> on(RegulatorTeamManagementController.class).renderMemberList(teamId);
      case INDUSTRY -> on(IndustryTeamManagementController.class).renderMemberList(teamId);
      case NORTHERN_OFFSHORE_AREA -> on(NorthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId);
      case SOUTHERN_OFFSHORE_AREA -> on(SouthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId);
      case ONSHORE_AREA -> on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId);
    };
  }
}
