package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore.OnshoreAreaTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry.IndustryTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamManagementController;

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
