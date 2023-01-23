package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;

public class TeamMemberEditRolesValidatorHints {

  private Team team;
  private TeamMember teamMember;

  public TeamMemberEditRolesValidatorHints(
      Team team,
      TeamMember teamMember) {
    this.team = team;
    this.teamMember = teamMember;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public TeamMember getTeamMember() {
    return teamMember;
  }

  public void setTeamMember(TeamMember teamMember) {
    this.teamMember = teamMember;
  }
}
