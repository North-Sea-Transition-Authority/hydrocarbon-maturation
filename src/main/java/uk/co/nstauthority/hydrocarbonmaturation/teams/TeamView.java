package uk.co.nstauthority.hydrocarbonmaturation.teams;

import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamManagementUtil;

public record TeamView(TeamId teamId, TeamType teamType, String displayName) {
  public static TeamView fromTeam(Team team) {
    var teamId = team.toTeamId();
    return new TeamView(teamId, team.getTeamType(), team.getDisplayName());
  }

  public String manageUrl() {
    return ReverseRouter.route(TeamManagementUtil.getRenderMemberListRoute(teamId, teamType));
  }

  public static TeamViewComparator sort() {
    return new TeamViewComparator();
  }
}
