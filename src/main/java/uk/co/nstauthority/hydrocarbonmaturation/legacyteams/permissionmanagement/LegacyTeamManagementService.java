package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class LegacyTeamManagementService {
  private final RegulatorTeamService regulatorTeamService;
  private final TeamService teamService;

  @Autowired
  LegacyTeamManagementService(RegulatorTeamService regulatorTeamService, TeamService teamService) {
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

  private List<TeamView> teamsToTeamViews(List<LegacyTeam> legacyTeams) {
    return legacyTeams.stream()
        .map(TeamView::fromTeam)
        .sorted(TeamView.sort())
        .toList();
  }
}
