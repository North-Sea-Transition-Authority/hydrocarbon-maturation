package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class TeamManagementService {
  private final RegulatorTeamService regulatorTeamService;
  private final TeamService teamService;

  @Autowired
  TeamManagementService(RegulatorTeamService regulatorTeamService, TeamService teamService) {
    this.regulatorTeamService = regulatorTeamService;
    this.teamService = teamService;
  }

  public boolean isIndustryAccessManagerInRegulatorTeam(User user) {
    return regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER);
  }

  List<TeamView> getTeamsThatUserHasAccessTo(User user) {
    if (isIndustryAccessManagerInRegulatorTeam(user)) {
      return teamsToTeamViews(teamService.getAllTeamOfTypes(Set.of(TeamType.REGULATOR, TeamType.INDUSTRY)));
    }

    return teamsToTeamViews(teamService.getTeamsThatUserBelongsTo(user));
  }

  private List<TeamView> teamsToTeamViews(List<Team> teams) {
    return teams.stream()
        .map(TeamView::fromTeam)
        .sorted(TeamView.sort())
        .toList();
  }
}
