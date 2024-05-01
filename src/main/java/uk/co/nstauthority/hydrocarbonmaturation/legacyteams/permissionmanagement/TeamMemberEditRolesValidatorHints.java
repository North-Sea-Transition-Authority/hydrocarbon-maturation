package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement;

import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMember;

public class TeamMemberEditRolesValidatorHints {

  private LegacyTeam legacyTeam;
  private TeamMember teamMember;

  public TeamMemberEditRolesValidatorHints(
      LegacyTeam legacyTeam,
      TeamMember teamMember) {
    this.legacyTeam = legacyTeam;
    this.teamMember = teamMember;
  }

  public LegacyTeam getTeam() {
    return legacyTeam;
  }

  public void setTeam(LegacyTeam legacyTeam) {
    this.legacyTeam = legacyTeam;
  }

  public TeamMember getTeamMember() {
    return teamMember;
  }

  public void setTeamMember(TeamMember teamMember) {
    this.teamMember = teamMember;
  }
}
