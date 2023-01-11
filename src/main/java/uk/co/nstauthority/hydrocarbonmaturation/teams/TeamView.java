package uk.co.nstauthority.hydrocarbonmaturation.teams;

import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.ManagementRouteUtil;

public record TeamView(TeamId teamId, TeamType teamType, String displayName) {
  public static TeamView fromTeam(Team team) {
    var teamId = new TeamId(team.getUuid());
    return new TeamView(teamId, team.getTeamType(), team.getDisplayName());
  }

  public String manageUrl() {
    return ManagementRouteUtil.getRouteForTeamIdAndTeamType(teamType, teamId);
  }
}
