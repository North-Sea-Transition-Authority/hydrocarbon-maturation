package uk.co.nstauthority.hydrocarbonmaturation.legacyteams;

import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamManagementUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

public record TeamView(TeamId teamId, TeamType teamType, String displayName) {
  public static TeamView fromTeam(LegacyTeam legacyTeam) {
    var teamId = legacyTeam.toTeamId();
    return new TeamView(teamId, legacyTeam.getTeamType(), legacyTeam.getDisplayName());
  }

  public String manageUrl() {
    return ReverseRouter.route(TeamManagementUtil.getRenderMemberListRoute(teamId, teamType));
  }

  public static TeamViewComparator sort() {
    return new TeamViewComparator();
  }
}
